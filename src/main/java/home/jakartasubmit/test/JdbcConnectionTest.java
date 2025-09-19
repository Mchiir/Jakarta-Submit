package home.jakartasubmit.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/vote_ease_db";
        String user = "guest";  // Replace with your DB username
        String password = "guest";  // Replace with your DB password
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("✅ Connection Successful!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}