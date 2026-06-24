package com.group3.utils;

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
        String url = "jdbc:sqlite:db_files/main";
        return DriverManager.getConnection(url);
    }
}
