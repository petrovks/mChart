package geekbrains.mchart;

public enum LogData {
    BOB("bob", "1234", "Bob"),
    JACK("jack", "4321", "Jack"),
    MAKS("maks", "123456", "Maks"),
    ALEX("alex", "654321", "Alex"),
    MAT("mat", "1111", "Mat");

    private String login;
    private String password;
    private String nikName;

    LogData(String login, String password, String nickName) {
        this.login = login;
        this.password = password;
        this.nikName = nickName;
    }
}
