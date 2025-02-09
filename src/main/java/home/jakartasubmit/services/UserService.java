package home.jakartasubmit.services;

import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    // Register a new user (Save user to the database)
    public boolean registerUser(User user) {
        if (!user.isValid()) {
            System.out.println("User is not valid.");
            return false;
        }
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);  // Save the user directly using Hibernate
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Fetch a user by ID
    public User getUserById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);  // Fetch user by ID
        }
    }

    public boolean loginUser(String email, String password) {
        // Retrieve the user by email
        User user = getUserByEmail(email);  // Method to query the database for user by email

        // If user doesn't exist or password is incorrect, return false
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            System.out.println("Invalid email or password.");
            return false;
        }

        // Successful login
        return true;
    }

    // Update an existing user
    public boolean updateUser(User user) {
        if (!user.isValid()) {
            System.out.println("User is not valid.");
            return false;
        }
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(user);  // Update the user in the database
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Delete a user by ID
    public boolean deleteUser(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User user = session.get(User.class, id);  // Retrieve user by ID
            if (user != null) {
                session.remove(user);  // Delete the user if it exists
                transaction.commit();
                return true;
            }
            return false;
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }
}
