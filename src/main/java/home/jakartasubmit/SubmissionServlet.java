package home.jakartasubmit;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.jakartasubmit.DTOs.UserDTO;
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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "SubmissionServlet", value = "/submission-servlet")
public class SubmissionServlet extends HttpServlet {
    private final SubmissionService submissionService = new SubmissionService();
    private final UserService userService = new UserService();
    private final TaskService taskService = new TaskService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retreiving formData
        HttpSession sessionobj = request.getSession(false);
        UserDTO currentUserDTO = null;
        if (sessionobj != null && sessionobj.getAttribute("isLoggedIn") != null) {
            currentUserDTO = (UserDTO) sessionobj.getAttribute("currentUser");
        }
        User.Role role = currentUserDTO.getRole();
        String email = currentUserDTO.getEmail();

        List<Submission> submissions = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        List<User> students = new ArrayList<>();

        switch (role) {
            case ADMIN:
                submissions = submissionService.getAllSubmissions();
                tasks = taskService.getAllTasks();
                break;
            case INSTRUCTOR:
                User instructor = userService.getUserByEmail(currentUserDTO.getEmail());
                submissions = submissionService.getAllSubmissions();
                tasks = taskService.getTasksByInstructor(instructor);
                break;
            case STUDENT:
                User student = userService.getUserByEmail(currentUserDTO.getEmail());
                submissions = submissionService.getSubmissionsByStudent(student);
                students = userService.getUsersByRole(role);
                tasks = taskService.getAllTasks();
                break;
            default:
                response.sendRedirect("error.jsp"); // Handle unexpected role case
                return;
        }

        if (submissions != null || tasks != null) {
            request.setAttribute("submissions", submissions);
            request.setAttribute("tasks", tasks);

            request.setAttribute("message", "Submissions got successfully");
            request.setAttribute("messageType", "success");
            request.getRequestDispatcher("public/Submissions.jsp").forward(request, response);
        } else {
            request.setAttribute("message", "Error getting submissions");
            request.setAttribute("messageType", "danger");
            request.getRequestDispatcher("submission.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equalsIgnoreCase(action)) {
            handleSubmissionRegistration(request, response);
        } else if ("edit".equalsIgnoreCase(action)) {
            handleSubmissionEdit(request, response);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleSubmissionDeletion(request, response);
        }
    }

    private void handleSubmissionRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        // Retrieve parameters from the request
        HttpSession sessionobj = request.getSession(false);
        UserDTO currentUserDTO = null;
        if (sessionobj != null && sessionobj.getAttribute("isLoggedIn") != null) {
            currentUserDTO = (UserDTO) sessionobj.getAttribute("currentUser");
        }

        UUID taskId = UUID.fromString(request.getParameter("taskId"));
        String filePath = request.getParameter("filePath");

        User student = userService.getUserByEmail(currentUserDTO.getEmail());
        Task task = taskService.getTaskById(taskId);

        // Check if both student and task are found
        if (student != null && task != null) {
            // Create the submission object
            Submission submission = new Submission(student, task, filePath);

            // Register the submission via the service
            if (submissionService.registerSubmission(submission)) {
                response.getWriter().write("Submission created successfully!, <a href=\"public/Submissions.jsp\">Return back</a>");
            } else {
                response.getWriter().write("Submission creation failed. <a href=\"public/Submissions.jsp\">Return back</a>");
            }
        } else {
            response.getWriter().write("Invalid student or task. <a href=\"public/Submissions.jsp\">Return back</a>");
        }
    }

    private void handleSubmissionEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        HttpSession sessionobj = request.getSession(false);
        UserDTO currentUserDTO = null;

        if (sessionobj != null && sessionobj.getAttribute("isLoggedIn") != null) {
            currentUserDTO = (UserDTO) sessionobj.getAttribute("currentUser");
        }

        if (currentUserDTO == null) {
            response.getWriter().write("Unauthorized access. <a href=\"login.jsp\">Return to Login</a>");
            return;
        }

        try {
            UUID submissionId = UUID.fromString(request.getParameter("submissionId"));
            Submission submission = submissionService.getSubmissionById(submissionId);

            if (submission != null) {
                String newFilePath = request.getParameter("filePath");

                // Update only if the value is provided
                if (newFilePath != null && !newFilePath.isEmpty()) {
                    submission.setFilePath(newFilePath);
                }

                // Attempt to update in DB
                if (submissionService.updateSubmission(submission)) {
                    response.getWriter().write("<p style='color: green;'>Submission updated successfully.</p>");
                } else {
                    response.getWriter().write("<p style='color: red;'>Error updating submission.</p>");
                }
            } else {
                response.getWriter().write("<p style='color: red;'>Submission not found.</p>");
            }
        } catch (Exception e) {
            response.getWriter().write("<p style='color: red;'>Invalid request: " + e.getMessage() + "</p>");
        }

        // Return link
        response.getWriter().write("<br/><a href=\"/Jakarta-Submit-1.0-SNAPSHOT/submission\">Return back</a>");
    }

    private void handleSubmissionDeletion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID submissionId = UUID.fromString(request.getParameter("submissionId"));
        if (submissionService.deleteSubmission(submissionId)) {
            response.getWriter().write("Submission deleted successfully. <a href=\"/Jakarta-Submit-1.0-SNAPSHOT/submission\">Return back</a>");
        } else {
            response.getWriter().write("Error deleting submission. <a href=\"/Jakarta-Submit-1.0-SNAPSHOT/submission\">Return back</a>");
        }
    }
}
