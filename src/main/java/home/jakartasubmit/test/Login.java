package home.jakartasubmit.test;

import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

public class Login {
    public static void main(String[] args) {
        User user = new User("John Doe", "doe@example.com", "hashedpassword", User.Role.INSTRUCTOR);
        if(authenticateUser(user.getEmail(), user.getPassword())){
            System.out.println("User logged in");
        }else{
            System.out.println("Incorrect cridentials");
        }
    }

    // Method to authenticate user (Login)
    public static boolean authenticateUser(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Fetch user by email
            User user = session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();

            if (user != null) {
                // Verify password
                return BCrypt.checkpw(password, user.getPassword());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
