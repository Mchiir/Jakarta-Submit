package home.jakartasubmit.services;

import home.jakartasubmit.DTOs.UserDTO;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;

public class UserService {

    public UserDTO convertToDTO(User user) {
        return new UserDTO(user.getFullName(), user.getEmail(), user.getRole());
    }

    public User convertToEntity(UserDTO userDTO, String password) {
        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail().toLowerCase()); // Normalize email
        user.setRole(userDTO.getRole());
        user.setPassword(password); // Hashing is handled inside User entity
        return user;
    }

    // Validate user before persisting
    public boolean isValid(User user) {
        return  user != null && user.getFullName() != null && !user.getFullName().isEmpty() && user.getFullName().length() <= 100 &&
                user.getEmail() != null && !user.getEmail().isEmpty() && user.getEmail().length() <= 100 &&
                user.getPassword() != null && !user.getPassword().isEmpty() && user.getPassword().length() <= 255 &&
                user.getRole() != null;
    }

    // Register a new user (Save user to the database)
    public boolean registerUser(User user) {
        if (!isValid(user)) {
            System.out.println("User is not valid.");
            return false;
        }

        if(getUserByEmail(user.getEmail()) != null) {
            System.out.println("User Email already in use.");
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

    // Login user with password verification
    public boolean loginUser(String email, String password) {
        User user = getUserByEmail(email);

        // If user doesn't exist or password is incorrect, return false
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            System.out.println("Invalid email or password.");
            return false;
        }

        // Successful login
        return true;
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        }
    }

    // Fetch a user by ID
    public User getUserById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    // Update an existing user
    public boolean updateUser(User user) {
        if (!isValid(user)) {
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

    public List<User> getUsersByRole(User.Role role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE role = :role", User.class).setParameter("role", role).list();
        }
    }

    public User getUserByEmailAndId(String email, UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email and id = :id", User.class);
            query.setParameter("email", email);
            query.setParameter("id", id);
            return query.uniqueResult();
        }
    }
}
