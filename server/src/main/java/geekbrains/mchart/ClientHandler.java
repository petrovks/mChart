package geekbrains.mchart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {

                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/login ")) {

                        String usernameFromLogin = msg.split("\\s")[1];

                        if (server.isNickBusy(usernameFromLogin)) {
                            sendMsg("/login_failed Current nickname is already used");
                            continue;
                        }

                        this.name = usernameFromLogin;
                        sendMsg("/login_ok " + this.name);
                        server.subscribe(this);
                        System.out.println(this.name);
                        break;
                    }
                }

                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        cmd(msg);
                        continue;
                    }
                    server.broadcastMsg(name + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMsg(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e){
            disconnect();
        }

    }

    public void cmd(String msg) {
        String m = msg.split("\\s")[0];
        switch (m){
            case "/w": server.sendPrivateMessage(this, msg.split("\\s")[1], msg.split("\\s", 3)[2]); break;//   /w Bob Hello, Bob
            case "/stat": sendMsg("Количество сообщений отправленных через чат - " + server.getCount()); break;
            case "/who_am_i": sendMsg("Ваш ник - " + getName()); break;
            case "/exit": System.exit(0); break;
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
