package geekbrains.mchart;

public interface AuthenticationProvider {
    String getNicknameByLoginAndPassword(String login, String password);
    void changeNickname(String oldNickname, String newNickname);
    void createNewUser(String nickname, String password);
    void connect();
    void disconnect();
}
