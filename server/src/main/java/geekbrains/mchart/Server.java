package geekbrains.mchart;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static int count;
    private int port;
    private List<ClientHandler> clients;

    public int getCount() {
        return count;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);
            while (true) {
                System.out.println("Ждем нового клиента...");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String isUserOnline(String nick, String password) {
      /*  for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;*/
        for (LogData l: LogData.values()) {
            if(l.getLogin().toString().equals(nick) && l.getPassword().toString().equals(password)) {
                return l.getNikName().toString();
            }
        }
        return null;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
                o.sendMsg(msg);
                count++;
        }
    }

    public synchronized void changeName(ClientHandler o, String name){
        String lastName = o.getName();
        clients.remove(o);
        o.setName(name);
        clients.add(o);
        broadcastMsg("Клиент " + lastName + " изменил имя на " + o.getName());
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastMsg("Клиент " + o.getName() + " вышел из чата");
        broadcastClientsList();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastMsg("Клиент " + o.getName() + " вошел в чат");
        broadcastClientsList();
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        for (ClientHandler c : clients) {
            if (c.getName().equals(receiverUsername)) {
                c.sendMsg("От: " + sender.getName() + " Сообщение: " + message);
                sender.sendMsg("Пользователю: " + receiverUsername + " Сообщение: " + message);
                count++;
                return;
            }
        }
        sender.sendMsg("Невозможно отправить сообщение пользователю: " + receiverUsername + ". Такого пользователя нет в сети.");
    }


    public synchronized void broadcastClientsList() {
        if (!clients.isEmpty()){
            StringBuilder stringBuilder = new StringBuilder("/clients_list ");
            for (ClientHandler c: clients) {
                stringBuilder.append(c.getName()).append(" ");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            String clientList = stringBuilder.toString();
            for (ClientHandler clientHandler : clients) {
                clientHandler.sendMsg(clientList);
            }
        }
    }



}
