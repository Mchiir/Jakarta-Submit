package home.jakartasubmit.services;

import home.jakartasubmit.models.FileTypes;
import home.jakartasubmit.models.Submission;
import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.UUID;

public class SubmissionService {

    public boolean isValid(Submission submission) {
        return submission != null && submission.getStudent() != null &&
                submission.getTask() != null &&
                submission.getFilePath() != null && !submission.getFilePath().isEmpty() &&
                FileTypes.isValidFileType(submission.getFilePath());
    }

    // Register a new submission (Save submission to the database)
    public boolean registerSubmission(Submission submission) {
        if (!isValid(submission)) {
            System.out.println("Submission is not valid.");
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(submission);  // Save the submission
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error registering submission: " + e.getMessage());
            return false;
        }
    }

    // Fetch a submission by ID
    public Submission getSubmissionById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Submission.class, id);  // Fetch submission by ID
        }
    }

    // Fetch all submissions
    public List<Submission> getAllSubmissions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Submission", Submission.class).list();
        }
    }

    public List<Submission> getSubmissionsByStudent(User student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Submission where student = :student", Submission.class).setParameter("student", student).list();
        }
    }

    // Update an existing submission
    public boolean updateSubmission(Submission submission) {
        if (!isValid(submission)) {
            System.out.println("Submission is not valid.");
            return false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(submission);  // Update the submission
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error updating submission: " + e.getMessage());
            return false;
        }
    }

    // Delete a submission by ID
    public boolean deleteSubmission(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Submission submission = session.get(Submission.class, id);  // Retrieve submission by ID
            if (submission != null) {
                session.remove(submission);  // Delete the submission if it exists
                transaction.commit();
                return true;
            }
            return false;
        }
    }
}
