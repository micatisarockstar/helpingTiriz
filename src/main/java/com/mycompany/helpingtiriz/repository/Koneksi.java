package com.mycompany.helpingtiriz.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    // connectTimeout / socketTimeout: supaya aplikasi ga "membeku" lama bila MySQL mati.
    private static final String URL = "jdbc:mysql://localhost:3306/db_tiriz"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta"
            + "&connectTimeout=5000&socketTimeout=20000";
    private static final String USER = "root";
    private static final String PASSWORD = "";   

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
