package com.pharmacy.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static String dbPath = null;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] SQLite JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    public static synchronized String resolveDatabasePath() {
        if (dbPath != null) return dbPath;

        String[] potentialPaths = {
            "DBMS/pharmacy.db",
            "JAVA/pharmacy.db",
            "pharmacy.db",
            "../DBMS/pharmacy.db",
            "../pharmacy.db"
        };

        for (String p : potentialPaths) {
            File f = new File(p);
            if (f.exists()) {
                dbPath = f.getAbsolutePath();
                return dbPath;
            }
        }

        // Default path
        File defaultFile = new File("DBMS/pharmacy.db");
        defaultFile.getParentFile().mkdirs();
        dbPath = defaultFile.getAbsolutePath();
        return dbPath;
    }

    public static Connection getConnection() throws SQLException {
        String resolved = resolveDatabasePath();
        String url = "jdbc:sqlite:" + resolved;
        Connection conn = DriverManager.getConnection(url);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[ERROR] Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public static String getDatabaseLocation() {
        return resolveDatabasePath();
    }
}
