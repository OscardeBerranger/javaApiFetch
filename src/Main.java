import org.json.JSONObject;

import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public class Main {
    static JFrame frame;
    static JButton button1, button2, button3;
    static JLabel label;
    static JTextField textField;
    static JPasswordField passwordField;
    static JPanel panel;

    public static void main(String[] args) {
        generateFrame();
    }


    private static void generateFrame(){
        frame = new JFrame("pannel");
        label = new JLabel("Login toi :)");
        textField = new JTextField(20);
        passwordField = new JPasswordField(20);
        panel = new JPanel();
        button1 = new JButton("login");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (textField.getText().equals("") || passwordField.getText().equals("")) {
                    JOptionPane.showMessageDialog(frame, "Please fill all the fields");
                }else{
                    callApi(textField.getText(), passwordField.getText());
                }
            }
        });


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.add(new JLabel("Username:"));
        panel.add(textField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(button1);
        panel.add(label);
        panel.setBackground(Color.WHITE);
        frame.add(panel);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private static void callApi(String username, String password) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpClient client = HttpClient.newBuilder()
                    .sslContext(sc)
                    .build();

            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);
            String finalBody = body.toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://localhost:8000/api/login_check"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(finalBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject myObject = new JSONObject(response.body());
                System.out.println(myObject.getString("token"));
                JPanel panel1 = new JPanel();
                panel1.add(new Label("You have been succesfully logged in"));
                panel1.add(new Label("token : " + myObject.getString("token")));
                frame.remove(panel);
                frame.setContentPane(panel1);
                frame.revalidate();
                frame.repaint();

            } else {
                System.out.println(response);
            }

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}