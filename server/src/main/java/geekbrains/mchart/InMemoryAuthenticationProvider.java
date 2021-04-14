package geekbrains.mchart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticationProvider{
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psInsert;
    private static final Logger LOGGER = LogManager.getLogger(InMemoryAuthenticationProvider.class);

/*
    CREATE TABLE users (
            id       INTEGER PRIMARY KEY AUTOINCREMENT
            NOT NULL,
            nickname TEXT    NOT NULL,
            password TEXT    NOT NULL,
            username TEXT    NOT NULL
    );*/

    @Override
    public void createNewUser(String nickname, String password) {
        try {
       //     connect();
            ResultSet rs = stmt.executeQuery("insert into users (nickname, password, username) values ('" + nickname + "', '" + password + "', 'empty');");

        } catch (SQLException s) {
            LOGGER.throwing(s);
        }
      //  finally {
      //      disconnect();
      //  }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        /*for (User u: user) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.nickname;
            }
        }*/

        try {
          //  connect();
            ResultSet rs = stmt.executeQuery("select username from users where nickname = '" + login + "' AND password = '" + password + "';");
            return rs.getString(1);
        } catch (SQLException s) {
            LOGGER.throwing(s);
        }
      //  finally {
         //   disconnect();
       // }
        return null;
    }

    @Override
    public void changeNickname(String oldNickname, String newNickname) {
        /*for (User u: user){
            if (u.nickname.equals(oldNickname)) {
                u.nickname = newNickname;
                return;
            }
        }*/
        try {
         //   connect();
            stmt.executeUpdate("update users set username = '" + newNickname + "' where username = '" + oldNickname + "';");

        } catch (SQLException s) {
            LOGGER.throwing(s);
        }
     //   finally {
     //       disconnect();
      //  }

    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chartdb.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("Невозможно подключиться к БД");
            throw new RuntimeException("Невозможно подключиться к БД");
        }
    }

    public void disconnect() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            LOGGER.throwing(e);
        }
        try {
            if (psInsert != null) {
                psInsert.close();
            }
        } catch (SQLException e) {
            LOGGER.throwing(e);
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                LOGGER.throwing(throwables);
            }
        }
        try {
            if (connection.isClosed()) {
                LOGGER.info("Отключились от БД.");
            }
        } catch (SQLException throwables) {
            LOGGER.throwing(throwables);
        }
    }
}
