package dao;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static String connector;
    private static String userName;
    private static String password;
    private static String driver;

    public static Connection getConnection() {
        Connection connection = null;
        try {
            parseDBdetails();
            Class.forName(driver);
            connection = DriverManager.getConnection(connector, userName, password);
        } catch (Exception e) {
            System.out.println("Connection with database failed!");
        }
        return connection;
    }

    public static void parseDBdetails() {
        Properties props = new Properties();
        try {
            FileReader reader = new FileReader("database.properties");
            props.load(reader);
            driver = props.getProperty("driver");
            connector = props.getProperty("connector");
            userName = props.getProperty("userName");
            password = props.getProperty("password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void endConnnection(Connection c) {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
