package com.group3.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
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

        // .env sits at the repo root, and its APP_DB_PATH is relative to Manager/;
        // support launching from either the repo root or the Manager directory
        File repoRoot = new File(".env").exists() ? new File(".") : new File("..");
        Dotenv dotenv = Dotenv.configure().directory(repoRoot.getPath()).load();
        String dbPath = new File(new File(repoRoot, "Manager"), dotenv.get("APP_DB_PATH")).getPath();

        // SQLite ignores FOREIGN KEY constraints unless each connection opts in
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        return DriverManager.getConnection("jdbc:sqlite:" + dbPath, config.toProperties());
    }
}
