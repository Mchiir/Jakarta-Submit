package home.jakartasubmit.services;

import home.jakartasubmit.models.Submission;
import home.jakartasubmit.models.User;
import home.jakartasubmit.util.HibernateUtil;
import jakarta.servlet.http.Part;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SubmissionService {
    private final String UPLOAD_DIR = "C:/Program Files/Apache Software Foundation/Tomcat 11.0/webapps/Jakarta-Submit-1.0-uploads/submissions"; // Directory where files will be stored

    public boolean isValid(Submission submission) {
        return submission != null && submission.getStudent() != null &&
                submission.getTask() != null &&
                submission.getFilePath() != null;
    }

    public boolean isValidFile(Part filePart) throws IOException {
        final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx", "pptx", "zip", "xls", "xlsx");
        final long MAX_FILE_SIZE = 30 * 1024 * 1024; // 30MB

        String fileName = Path.of(filePart.getSubmittedFileName()).getFileName().toString();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: " + ALLOWED_EXTENSIONS);
        }

        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 30MB limit.");
        }

        return true;
    }

    public String saveFileLocally(Part filePart) throws IOException {
        if (!isValidFile(filePart)) {
            throw new IllegalArgumentException("File validation failed.");
        }

        File uploadFolder = new File(UPLOAD_DIR);
        // Ensure directory exists
        if (!uploadFolder.exists()) {
            boolean result =  uploadFolder.mkdirs();
            if (!result)
                throw new IOException("Unable to create upload folder.");
        }

        // Generate unique filename
        String fileName = System.currentTimeMillis() + "_" + Path.of(filePart.getSubmittedFileName()).getFileName().toString();
        Path filePath = Path.of(UPLOAD_DIR, fileName);

        // Save file
        filePart.write(filePath.toString());

        return filePath.toString(); // Return saved file path
    }

    // Register a new submission (Save submission to the database)
    public boolean registerSubmission(Submission submission) {
        if (!isValid(submission)) {
            throw new IllegalArgumentException("Submission validation failed.");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(submission);  // Save the submission
            transaction.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
