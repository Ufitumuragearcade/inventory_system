package com.inventory.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAMPP password is empty
    
    private static DatabaseConfig instance;
    private Connection connection;
    
    // Private constructor for Singleton pattern
    private DatabaseConfig() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Ensure database exists before making connection
            ensureDatabaseExists();
            
            // Create connection
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }
    
    // Singleton pattern to get single instance
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }
    
    // Get connection
    public Connection getConnection() {
        try {
            // If connection is closed or null, create new one
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    // Ensure database and tables exist
    private void ensureDatabaseExists() {
        try {
            // First connect without database to create it if needed
            String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
            Connection tempConn = DriverManager.getConnection(baseUrl, USER, PASSWORD);
            Statement stmt = tempConn.createStatement();
            
            // Create database if not exists
            String createDB = "CREATE DATABASE IF NOT EXISTS inventory_system";
            stmt.executeUpdate(createDB);
            System.out.println("Database 'inventory_system' ensured.");
            
            stmt.close();
            tempConn.close();
            
            // Now connect to the database and create tables
            Connection dbConn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement dbStmt = dbConn.createStatement();
            
            // Create assets table
            String createAssetsTable = 
                "CREATE TABLE IF NOT EXISTS assets (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    name VARCHAR(100) NOT NULL," +
                "    type VARCHAR(50) NOT NULL," +
                "    serial_number VARCHAR(100) UNIQUE," +
                "    condition_status VARCHAR(100)," +
                "    status VARCHAR(50) DEFAULT 'available'," +
                "    created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
            dbStmt.executeUpdate(createAssetsTable);
            
            // Create users table
            String createUsersTable = 
                "CREATE TABLE IF NOT EXISTS users (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    name VARCHAR(100) NOT NULL," +
                "    department VARCHAR(100)," +
                "    email VARCHAR(100) UNIQUE," +
                "    username VARCHAR(100) UNIQUE NOT NULL," +
                "    password VARCHAR(255) NOT NULL" +
                ")";
            dbStmt.executeUpdate(createUsersTable);
            
            // Create transactions table
            String createTransactionsTable = 
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "    id INT AUTO_INCREMENT PRIMARY KEY," +
                "    asset_id INT," +
                "    user_id INT," +
                "    issue_date DATETIME," +
                "    return_date DATETIME," +
                "    status VARCHAR(50) NOT NULL," +
                "    message TEXT," +
                "    created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (asset_id) REFERENCES assets(id)," +
                "    FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";
            dbStmt.executeUpdate(createTransactionsTable);
            
            System.out.println("All tables created successfully!");
            
            dbStmt.close();
            dbConn.close();
            
        } catch (SQLException e) {
            System.err.println("Error ensuring database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Static method for quick connection testing
    public static Connection getConnectionDirect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Test main method
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        DatabaseConfig dbConnection = DatabaseConfig.getInstance();
        Connection conn = dbConnection.getConnection();
        
        if (conn != null) {
            System.out.println("✓ Database connection successful!");
        } else {
            System.out.println("✗ Database connection failed!");
        }
        
        dbConnection.closeConnection();
    }
}