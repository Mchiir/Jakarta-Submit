package home.jakartasubmit.services;

import home.jakartasubmit.models.Submission;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskService {

    public boolean isValid(Task task) {
        return task != null && task.getInstructor() != null && !task.getCourseName().isEmpty() &&
                task.getCourseName().length() < 100 &&
                !task.getDescription().isEmpty() && task.getDescription().length() < 255 &&
                task.getDeadline() != null && !task.getDeadline().isBefore(LocalDateTime.now());
    }

    // Register a new task (Save task to the database)
    public boolean registerTask(Task task) {
        if (!isValid(task)) {
            System.out.println("Task is not valid.");
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(task);  // Save the task
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error registering task: " + e.getMessage());
            return false;
        }
    }

    // Fetch a task by ID
    public Task getTaskById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Task.class, id);  // Fetch task by ID
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> getAllTasks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Task", Task.class).list();
        }
    }

    public List<Task> getTasksByInstructor(User instructor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Task where instructor = :instructor", Task.class).setParameter("instructor", instructor).list();
        }
    }

    // Update an existing task
    public boolean updateTask(Task task) {
        if (!isValid(task)) {
            System.out.println("Task is not valid.");
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(task);  // Update the task
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error updating task: " + e.getMessage());
            return false;
        }
    }

    // Delete a task by ID
    public boolean deleteTask(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Task task = session.get(Task.class, id);  // Retrieve task by ID
            if (task != null) {
                session.remove(task);  // Delete the task if it exists
                transaction.commit();
                return true;
            }
            return false;
        }
    }
}
