package com.gaa.player.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    // Use DKIT MySQL Server (NOT localhost)
    private static final String URL = "jdbc:mysql://mysql05.comp.dkit.ie:3306/D00265095";
    private static final String USERNAME = "D00265095"; // Your student ID
    private static final String PASSWORD = ""; // Try empty password first

    private DBConnection() {
        try {
            System.out.println(" Attempting to load MySQL JDBC Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");

            System.out.println(" Attempting to connect to Dkit MySQL server...");
            System.out.println(" URL: " + URL);
            System.out.println(" Username: " + USERNAME);
            System.out.println(" Password: " + (PASSWORD.isEmpty() ? "[empty]" : "[set]"));

            // Add connection timeout to prevent hanging
            String urlWithTimeout = URL + "?connectTimeout=5000&socketTimeout=5000";
            this.connection = DriverManager.getConnection(urlWithTimeout, USERNAME, PASSWORD);

            System.out.println(" Connected to Dkit MySQL database successfully!");
            System.out.println(" Database: " + connection.getCatalog());

        } catch (ClassNotFoundException e) {
            System.err.println(" MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(" Dkit database connection failed: " + e.getMessage());
            System.err.println(" Try these solutions:");
            System.err.println("   1. Are you on campus network or using Dkit VPN?");
            System.err.println("   2. Try different passwords (empty, your student ID, your Dkit password)");
            System.err.println("   3. Contact Dkit IT: it@dkit.ie");
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
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println(" Connection was closed, reconnecting...");
                instance = new DBConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection: " + e.getMessage());
        }
        return connection;
    }
}