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

    public void setName(String name) {
        this.name = name;
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
                        String[] str = msg.split("\\s");
                        if (str.length != 3){
                            sendMsg("/login_failed Введите имя пользователя и пароль");
                            continue;
                        }
                        String login = str[1];
                        String password = str[2];

                        String usernameFromLogin = server.getAuthenticationProvider().getNicknameByLoginAndPassword(login, password);

                        if (usernameFromLogin == null) {
                            sendMsg("/login_failed Введен неправильный логин или пароль");
                            continue;
                        }
                        if (server.isUserOnline(usernameFromLogin)) {
                            sendMsg("login_failed Учетная запись уже используется");
                            continue;
                        }

                        this.name = usernameFromLogin;
                        sendMsg("/login_ok " + this.name);
                        server.subscribe(this);
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
            case "/w":
                String[] tokens = msg.split("\\s", 3);
                if (tokens.length != 3){
                    sendMsg("Server: Введена некоректная команда");
                }
                server.sendPrivateMessage(this, tokens[1], tokens[2]);
                break;//   /w Bob Hello, Bob
            case "/stat":
                sendMsg("Количество сообщений отправленных через чат - " + server.getCount());
                break;
            case "/who_am_i":
                sendMsg("Ваш ник - " + getName());
                break;
            case "/exit":
                //System.exit(0);
                server.unsubscribe(this);
                break;
            case "/logout" :
                sendMsg("/logout_ok " + this.name);
                server.unsubscribe(this);
                break;
            case "/change_nick" :
                //System.out.println("high");
                String[] tok = msg.split("\\s");
                if (tok.length != 2) {
                    sendMsg("Server: Введена некоректная команда");
                }
                String newNickname = tok[1];
                if (server.isUserOnline(newNickname)) {
                    sendMsg("Server: Такой никнейм уже занят");
                    return;
                }
                server.getAuthenticationProvider().changeNickname(name, newNickname);
                this.name = tok[1];
                sendMsg("Server: Вы изменили никнейм на " + newNickname);
                server.broadcastClientsList();
                break;

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
