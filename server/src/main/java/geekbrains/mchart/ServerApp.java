package geekbrains.mchart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    private static int count = 0;

    public static void main(String[] args){
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Сервер запущен, ожидаем подключения клиента...");
            Socket socket = serverSocket.accept();
            System.out.println("Клиент подключился");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String msg;
            while(true){
                msg = in.readUTF();
               // System.out.print(msg);

                if (msg.equals("/exit")) {
                    System.exit(0);
                }
                else if (msg.equals("/stat")) {
                    out.writeUTF("Количество сообщений - " + count);
                }
                else {
                    out.writeUTF(msg);
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
