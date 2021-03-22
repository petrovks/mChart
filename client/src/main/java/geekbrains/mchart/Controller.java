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
        if (name != null){
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            logoutPanel.setVisible(true);
            logoutPanel.setManaged(true);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            logoutPanel.setVisible(false);
            logoutPanel.setManaged(false);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        }
    }

    public void sendMsg() {
        try {
            String msg = msgField.getText();
            out.writeUTF(msg);
            //socket.getOutputStream().write(msg.getBytes());
            msgField.clear();
            msgField.requestFocus();


        } catch (IOException e) {
            Alert alert =  new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение!", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    public void login() {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        if (usernameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Имя пользователя не может быть пустым", ButtonType.CLOSE);
            alert.showAndWait();
            return;
        }

        try {
            out.writeUTF("/login " + usernameField.getText());
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
                                System.exit(0);
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
           Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно подключится к серверу", ButtonType.OK);
           alert.showAndWait();
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

    public void loginAnswer () {

    }
}
