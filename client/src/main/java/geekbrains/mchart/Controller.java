package geekbrains.mchart;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextField msgField, usernameField;

    @FXML
    PasswordField passwordField;

    @FXML
    TextArea msgArea;

    @FXML
    HBox loginPanel, msgPanel, logoutPanel;

    @FXML
    ListView<String> clientList;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       setName(null);
    }

    public void setName(String name) {
        this.name = name;
        boolean nameIsNull = name == null;

            loginPanel.setVisible(nameIsNull);
            loginPanel.setManaged(nameIsNull);
            logoutPanel.setVisible(!nameIsNull);
            logoutPanel.setManaged(!nameIsNull);
            msgPanel.setVisible(!nameIsNull);
            msgPanel.setManaged(!nameIsNull);
            clientList.setVisible(!nameIsNull);
            clientList.setManaged(!nameIsNull);

    }

    public void sendMsg() {
        try {
            String msg = msgField.getText();
            out.writeUTF(msg);
            //socket.getOutputStream().write(msg.getBytes());
            msgField.clear();
            msgField.requestFocus();


        } catch (IOException e) {
            showErrorAlert("Невозможно отправить сообщение!");
        }
    }

    public void login() {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        if (usernameField.getText().isEmpty()) {
            showErrorAlert("Имя пользователя не может быть пустым");
            return;
        }

        try {
            out.writeUTF("/login " + usernameField.getText() + " " + passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() ->{

                try {
                 /*   while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/login_ok ")) {
                            setName(msg.split("\\s")[1]);
                            break;
                        }
                        if (msg.startsWith("/login_failed ")) {
                            String cause = msg.split("\\s",2)[1];
                            msgArea.appendText(cause + "\n");
                        }
                    }*/
                    while (true) {

                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            if (msg.startsWith("/login_ok ")) {
                                setName(msg.split("\\s")[1]);
                                // break;
                            }
                            if (msg.startsWith("/login_failed ")) {
                                String cause = msg.split("\\s", 2)[1];
                                msgArea.appendText(cause + "\n");
                            }
                            if (msg.startsWith("/clients_list ")) {
                                // /clients_list Bob Max Jack
                                String[] tokens = msg.split("\\s");

                                Platform.runLater(() -> {
                                    System.out.println(Thread.currentThread().getName());
                                    clientList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                    clientList.getItems().add(tokens[i]);
                                }
                                  });
                            }
                            if (msg.startsWith("/logout_ok ")) {
                                setName(null);
                                msgArea.appendText("Вы вышли из чата" + "\n");
                            }
                            if (msg.equals("/exit")) {
                                break;
                                //System.exit(0);
                            }

                            continue;
                        }
                        msgArea.appendText(msg + "\n");

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    disconnect();
                }

            });
            t.start();
        } catch (IOException e) {
           showErrorAlert("Невозможно подключится к серверу");
        }
    }

    public void disconnect() {
        setName(null);
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
            try {
                out.writeUTF("/logout " + this.name);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void showErrorAlert (String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("Chat error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void createNewUser(){
        if (socket == null || socket.isClosed()) {
            connect();
        }

        if (usernameField.getText().isEmpty()) {
            showErrorAlert("Имя пользователя не может быть пустым");
            return;
        }

        try {
            out.writeUTF("/create " + usernameField.getText() + " " + passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
