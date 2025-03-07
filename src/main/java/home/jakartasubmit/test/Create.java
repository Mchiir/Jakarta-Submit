package home.jakartasubmit.test;

import home.jakartasubmit.models.Submission;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class Create {
    public static void main(String[] args) {
        // Example 3: Admin User
        User instructor = new User("Charlie Brown", "brown@example.com", "hashedpassword789", User.Role.INSTRUCTOR);

        //bell@gmail.com, bell12345, "ADMIN"
        // brown@example.com, hashedpassword789, "INSTRUCTOR"
        // mchrispin14@gmail.com, mugisha1234, "STUDENT"

        // Example 2: Creating a Task associated with the Instructor
        Task task = new Task(
                instructor,  // Instructor for the task
                "Java Programming 101",  // Course Name
                "Complete the Java assignment on inheritance and polymorphism.",  // Description
                LocalDateTime.of(2025, 2, 28, 23, 59)  // Deadline
        );

        // Example 1: Creating User (Student)
        User student = new User("Bob Smith", "smith@example.com", "hashedpassword789", User.Role.STUDENT);
        // Example 3: Creating Submission
        Submission submission = new Submission(
                student,       // Student who submitted the task
                task,          // Task being submitted
                "/path/to/assignment.zip"  // File path
        );
        if(createUser(student)) {
            // Transaction management for batch creation
            if (createUser(instructor) && createTask(task) && createSubmission(submission)) {
                System.out.println("User Created: " + instructor.getFullName());
                System.out.println("Task Created: " + task.getCourseName());
                System.out.println("Submission Created for: " + submission.getStudent().getFullName());
            } else {
                System.out.println("One or more creation operations failed.");
            }
        }
    }

    public static boolean createUser(User user) {
        if (user == null) {
            System.out.println("User cannot be null");
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean createTask(Task task) {
        if (task == null) {
            System.out.println("Task cannot be null");
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(task);
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error creating task: " + e.getMessage());
            return false;
        }
    }

    public static boolean createSubmission(Submission submission) {
        if (submission == null) {
            System.out.println("Submission cannot be null");
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(submission);
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error creating submission: " + e.getMessage());
            return false;
        }
    }
}