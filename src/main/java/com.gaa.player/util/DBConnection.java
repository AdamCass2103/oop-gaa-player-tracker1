package com.gaa.player.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    // Use LOCAL MySQL instead of Dkit server
    private static final String URL = "jdbc:mysql://localhost:3306/D00265095";
    private static final String USERNAME = "root"; // default local MySQL username
    private static final String PASSWORD = ""; // default local MySQL password

    private DBConnection() {
        try {
            System.out.println("🔍 Attempting to load MySQL JDBC Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully!");

            System.out.println("🔍 Attempting to connect to: " + URL);
            System.out.println("🔍 Username: " + USERNAME);
            System.out.println("🔍 Password: " + PASSWORD);

            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Connected to LOCAL MySQL database successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            System.err.println("💡 Install MySQL locally or check credentials");
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}