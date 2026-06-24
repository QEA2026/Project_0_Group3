package com.group3.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {
    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Dotenv dotenv = Dotenv.configure().directory("..").load();
        String dbPath = dotenv.get("APP_DB_PATH");

        return DriverManager.getConnection(dbPath);
    }
}
