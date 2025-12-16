package at.fh.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = new Properties();
                InputStream is = DatabaseConfig.class.getClassLoader()
                        .getResourceAsStream("db.properties");

                if (is == null) {
                    throw new RuntimeException("db.properties not found");
                }

                props.load(is);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, password);

            } catch (IOException | SQLException e) {
                throw new RuntimeException("Database connection failed: ", e);
            }
        }
        return connection;
    }
}