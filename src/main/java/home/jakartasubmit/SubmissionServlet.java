package home.jakartasubmit;

import home.jakartasubmit.models.Submission;
import home.jakartasubmit.models.User;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.services.SubmissionService;
import home.jakartasubmit.services.TaskService;
import home.jakartasubmit.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "SubmissionServlet", value = "/submission-servlet")
public class SubmissionServlet extends HttpServlet {
    private final SubmissionService submissionService = new SubmissionService();
    private final UserService userService = new UserService();
    private final TaskService taskService = new TaskService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve parameters from the request
        UUID studentId = UUID.fromString(request.getParameter("studentId"));
        UUID taskId = UUID.fromString(request.getParameter("taskId"));
        String filePath = request.getParameter("filePath");

        // Fetch the student and task from the database using the respective service methods
        User student = userService.getUserById(studentId);
        Task task = taskService.getTaskById(taskId);

        // Check if both student and task are found
        if (student != null && task != null) {
            // Create the submission object
            Submission submission = new Submission(student, task, filePath);

            // Register the submission via the service
            if (submissionService.registerSubmission(submission)) {
                response.getWriter().write("Submission created successfully!");
            } else {
                response.getWriter().write("Submission creation failed.");
            }
        } else {
            response.getWriter().write("Invalid student or task.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID submissionId = UUID.fromString(request.getParameter("id"));
        Submission submission = submissionService.getSubmissionById(submissionId);

        if (submission != null) {
            response.getWriter().write("Submission: " + submission.getFilePath());
        } else {
            response.getWriter().write("Submission not found.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID submissionId = UUID.fromString(request.getParameter("id"));
        Submission submission = submissionService.getSubmissionById(submissionId);

        if (submission != null) {
            submission.setFilePath(request.getParameter("filePath"));
            if (submissionService.updateSubmission(submission)) {
                response.getWriter().write("Submission updated successfully.");
            } else {
                response.getWriter().write("Error updating submission.");
            }
        } else {
            response.getWriter().write("Submission not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID submissionId = UUID.fromString(request.getParameter("id"));
        if (submissionService.deleteSubmission(submissionId)) {
            response.getWriter().write("Submission deleted successfully.");
        } else {
            response.getWriter().write("Error deleting submission.");
        }
    }
}
