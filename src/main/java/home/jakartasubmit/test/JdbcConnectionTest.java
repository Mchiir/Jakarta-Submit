package home.jakartasubmit.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/<db_name>"; // Replace with your DB URL
        String user = "<username>";  // Replace with your DB username
        String password = "<password>";  // Replace with your DB password
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("✅ Connection Successful!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}