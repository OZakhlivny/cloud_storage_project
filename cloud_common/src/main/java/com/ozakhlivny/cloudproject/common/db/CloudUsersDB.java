package com.ozakhlivny.cloudproject.common.db;

import java.sql.*;

public class CloudUsersDB {
    private static final String DB_URL = "jdbc:sqlite:cloud_common/users.db";
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void connectToDB() throws SQLException {
        connection = null;
        connection = DriverManager.getConnection(DB_URL);
        statement = connection.createStatement();
    }

    public static boolean isRegisteredUser (String login, String password) throws SQLException{
        resultSet = statement.executeQuery(String.format("SELECT id FROM users WHERE login=\"%s\" AND password=\"%s\";", login, password));
        if(resultSet.next()) return true;
        else return false;
    }

    public static void closeConnection() throws SQLException {
        resultSet.close();
        statement.close();
        connection.close();
    }
}
