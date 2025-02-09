package home.jakartasubmit.test;

import home.jakartasubmit.DAOs.UserDAO;
import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;

public class Main {
    private static HibernateUtil session;


    public static void main(String[] args) {
        // Create and save a new user
        User newUser = new User("John Doe", "john@example.com", "hashedpassword", User.Role.INSTRUCTOR);

        // Fetch the user
    }
}
