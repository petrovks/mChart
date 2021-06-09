package geekbrains.mchart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int count;
    private int port;
    private List<ClientHandler> clients;
    private AuthenticationProvider authenticationProvider;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public int getCount() {
        return count;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.authenticationProvider = new InMemoryAuthenticationProvider();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            //System.out.println("Сервер запущен на порту " + port);
            LOGGER.info("Сервер запущен на порту " + port);
            this.authenticationProvider.connect();
            while (true) {
                //System.out.println("Ждем нового клиента...");
                LOGGER.info("Ждем нового клиента...");
                Socket socket = server.accept();
                //System.out.println("Клиент подключился");
                LOGGER.info("Клиент подключился");
                new ClientHandler(this, socket, executorService);
            }
        } catch (IOException e) {
          //  e.printStackTrace();
            LOGGER.throwing(e);
        }
        finally {
            this.authenticationProvider.disconnect();
            executorService.shutdown();
        }
    }

    public synchronized boolean isUserOnline(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                LOGGER.warn("Пользователь с именем \"" + nick + "\" существует");
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
                o.sendMsg(msg);
                count++;
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastMsg("Клиент " + o.getName() + " вышел из чата");
        LOGGER.info("Клиент " + o.getName() + " вышел из чата");
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
