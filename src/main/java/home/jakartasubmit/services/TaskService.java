package home.jakartasubmit.services;

import home.jakartasubmit.models.Task;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.UUID;

public class TaskService {

    // Register a new task (Save task to the database)
    public boolean registerTask(Task task) {
        if (!task.isValid()) {
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
        }
    }

    // Update an existing task
    public boolean updateTask(Task task) {
        if (!task.isValid()) {
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
