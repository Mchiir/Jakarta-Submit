package home.jakartasubmit.services;

import home.jakartasubmit.DTOs.UserDTO;
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

    public boolean isValid(Task task, UserDTO currentUser) {
        if(task == null) {
            throw new IllegalArgumentException("task is null");
        }

        if(currentUser.getRole() != User.Role.INSTRUCTOR){
            throw new SecurityException("Only instructors can perform this operation");
        }

        return !task.getCourseName().isEmpty() &&
                task.getCourseName().length() < 100 &&
                !task.getDescription().isEmpty() && task.getDescription().length() < 255 &&
                task.getDeadline() != null && !task.getDeadline().isBefore(LocalDateTime.now());
    }

    // Register a new task (Save task to the database)
    public void registerTask(Task task, UserDTO currentUser) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if(isValid(task, currentUser)){
                Transaction transaction = session.beginTransaction();
                session.persist(task);  // Save the task
                transaction.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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
    public void updateTask(Task task, UserDTO currentUser) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if(isValid(task, currentUser)){
                Transaction transaction = session.beginTransaction();
                session.merge(task);  // Update the task
                transaction.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Delete a task by ID
    public void deleteTask(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Task task = session.get(Task.class, id); // Retrieve task by ID

            if (task == null) {
                throw new IllegalArgumentException("Task not found.");
            }

            try {
                session.remove(task); // Attempt to delete
                transaction.commit();
            } catch (org.hibernate.exception.ConstraintViolationException e) {
                transaction.rollback(); // Rollback transaction if FK violation occurs
                throw new RuntimeException("Task is still referenced in submissions.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
