package hospitalms.database;

import java.sql.*;
import javax.swing.JOptionPane;

public class DBConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "hospital_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL Driver not found!\nPlease add mysql-connector JAR to Libraries.",
                "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Cannot connect to database!\n\nMake sure:\n"
                + "1. XAMPP is running\n"
                + "2. MySQL is started\n"
                + "3. Database 'hospital_db' exists\n\n"
                + "Error: " + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Close Error: " + e.getMessage());
        }
    }
}