package com.ims.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = resolveUrl();
    private static final String USER = resolveUsername();
    private static final String PASSWORD = resolvePassword();

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String resolveUrl() {
        String directUrl = read("DB_URL");
        if (hasText(directUrl)) {
            return directUrl;
        }

        String host = read("MYSQLHOST");
        String port = defaultIfBlank(read("MYSQLPORT"), "3306");
        String database = defaultIfBlank(read("MYSQLDATABASE"), "inventory_system");

        if (hasText(host)) {
            return "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        }

        return "jdbc:mysql://localhost:3306/inventory_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    }

    private static String resolveUsername() {
        return defaultIfBlank(read("DB_USERNAME"), defaultIfBlank(read("MYSQLUSER"), "root"));
    }

    private static String resolvePassword() {
        return defaultIfBlank(read("DB_PASSWORD"), defaultIfBlank(read("MYSQLPASSWORD"), ""));
    }

    private static String read(String key) {
        String systemProperty = System.getProperty(key);
        if (hasText(systemProperty)) {
            return systemProperty;
        }
        return System.getenv(key);
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String defaultIfBlank(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }
}
