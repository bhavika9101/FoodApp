package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static volatile Connection connection;

    private DBConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (DBConnection.class) {
                if (connection == null) {
                    try {
                        Properties props = new Properties();
                        InputStream input = DBConnection.class.getClassLoader()
                                .getResourceAsStream("db.properties");
                        if (input == null) {
                            throw new RuntimeException("db.properties not found on classpath.");
                        }
                        props.load(input);

                        String url = props.getProperty("db.url");
                        String username = props.getProperty("db.username");
                        String password = props.getProperty("db.password");

                        connection = DriverManager.getConnection(url, username, password);
                        System.out.println("Database connection established.");
                    } catch (IOException | SQLException e) {
                        throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
                    }
                }
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
