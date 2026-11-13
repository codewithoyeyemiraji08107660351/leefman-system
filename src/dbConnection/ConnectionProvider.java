/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dbConnection;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author oyeyemi
 */
public class ConnectionProvider {
    
    private static String url;
    private static String user;
    private static String password;
    private static Connection con;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        try {
            Properties props = new Properties();
            
            // Try multiple locations for config file
            String[] configPaths = {
                "config/db.properties",
                "../config/db.properties",
                "./db.properties"
            };
            
            boolean loaded = false;
            for (String path : configPaths) {
                File configFile = new File(path);
                if (configFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(configFile)) {
                        props.load(fis);
                        loaded = true;
                        break;
                    }
                }
            }
            
            if (loaded) {
                String host = props.getProperty("db.host", "127.0.0.1");
                String port = props.getProperty("db.port", "3307");
                String dbName = props.getProperty("db.name", "tailoring_system");
                user = props.getProperty("db.user", "root");
                password = props.getProperty("db.password", "oyeyemi");
                
                url = "jdbc:mysql://" + host + ":" + port + "/" + dbName 
                      + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            } else {
                // Default values
                url = "jdbc:mysql://127.0.0.1:3307/tailoring_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                user = "root";
                password = "oyeyemi";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback
            url = "jdbc:mysql://127.0.0.1:3306/tailoring_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            user = "root";
            password = "leefman";
        }
    }
    
    public static Connection getCon() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(url, user, password);
//                System.out.println("Database connected successfully!");
//                System.out.println("URL: " + url);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "MySQL Driver not found!\nPlease ensure mysql-connector-java.jar is in the lib folder.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database Connection Error:\n" + e.getMessage() + 
                "\n\nPlease check:\n1. MySQL is running\n2. Config file exists: config/db.properties\n3. Database credentials are correct",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return con;
    }
    
    public static void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                con = null;
//                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
   
}