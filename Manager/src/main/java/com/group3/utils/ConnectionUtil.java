package com.group3.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

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

        // SQLite ignores FOREIGN KEY constraints unless each connection opts in
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        return DriverManager.getConnection("jdbc:sqlite:" + dbPath, config.toProperties());
    }
}
