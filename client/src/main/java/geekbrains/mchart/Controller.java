package geekbrains.mchart;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    DataOutputStream out;
    DataInputStream in;
    @FXML
    TextField msgField;

    @FXML
    TextArea msgArea;

    private Socket socket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost",8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(() -> {
                String msg;
                try {
                while (true) {
                    msg = in.readUTF();
                    msgArea.appendText(msg + "\n");
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            t.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect to server [ localhost:8189 ]");
        }

    }

    public void sendMsg() {
        try {
            String msg = msgField.getText();
            out.writeUTF(msg);
            //socket.getOutputStream().write(msg.getBytes());
            msgField.clear();
            if (msg.equals("/exit")) { System.exit(0);}

        } catch (IOException e) {
            Alert alert =  new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение!", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }
}
