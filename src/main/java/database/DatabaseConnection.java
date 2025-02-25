package database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static final Dotenv dotenv = Dotenv.load(); // Tự động tìm .env

    private static final String SUrl = dotenv.get("SUrl");
    private static final String SUser = dotenv.get("SUser");
    private static final String SPass = dotenv.get("SPass");

    static {
        if (SUrl == null || SUser == null || SPass == null) {
            logger.log(Level.SEVERE, "Missing database environment variables! Check your .env file.");
        }
    }

    public static Connection getConnection() {
        if (SUrl == null || SUser == null || SPass == null) {
            logger.log(Level.SEVERE, "Cannot establish connection: Database credentials are missing.");
            return null;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(SUrl, SUser, SPass);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL driver not found!", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error connecting to the database!", e);
        }
        return null;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing the connection!", e);
            }
        }
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                System.out.println("Kết nối thành công");
            } else {
                System.err.println("Kết nối thất bại! Vui lòng kiểm tra thông tin kết nối.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error while handling the database connection", e);
        }
    }
}
