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
            if(students != null){ request.setAttribute("students", students); }

            request.setAttribute("currentUser", currentUserDTO);
            request.setAttribute("isLoggedIn", true);
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
