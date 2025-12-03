package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/testdb?useSSL=false";
    // Changez ici pour le nouvel utilisateur
    private static final String USER = "root";
    // Changez ici pour le nouveau mot de passe
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
