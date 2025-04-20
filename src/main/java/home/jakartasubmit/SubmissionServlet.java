package home.jakartasubmit;

import home.jakartasubmit.DTOs.UserDTO;
import home.jakartasubmit.models.*;
import home.jakartasubmit.services.SubmissionService;
import home.jakartasubmit.services.TaskService;
import home.jakartasubmit.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "SubmissionServlet", value = "/submission-servlet")
@MultipartConfig
public class SubmissionServlet extends HttpServlet {
    private final SubmissionService submissionService = new SubmissionService();
    private final UserService userService = new UserService();
    private final TaskService taskService = new TaskService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action == null) {
            handleGetSubmissions(request, response);
        } else if ("download_file".equalsIgnoreCase(action)) {
            handleDownloadFile(request, response);
        }
    }

    private void handleDownloadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");

        String originalFileName = Files.formatFileName(fileName);

        String filePath = submissionService.getFilePath(fileName);
        File file = new File(filePath);

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        // Determine MIME type
        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Default if unknown
        }

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + originalFileName + "\"");

        // Stream the file to the response
        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush(); // preventing incomplete downloads
        }
    }

    private void handleGetSubmissions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retreiving formData
        HttpSession sessionobj = request.getSession(false);
        UserDTO currentUserDTO = null;
        if (sessionobj != null && sessionobj.getAttribute("isLoggedIn") != null) {
            currentUserDTO = (UserDTO) sessionobj.getAttribute("currentUser");
        }else{
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
        User.Role role = currentUserDTO.getRole();
        String email = currentUserDTO.getEmail();

        List<Submission> submissions = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        List<User> students = new ArrayList<>();
        String page = "error.jsp";

        switch (role) {
            case ADMIN:
                submissions = submissionService.getAllSubmissions();
                tasks = taskService.getAllTasks();
                page = "/admin/Submissions.jsp";
                break;
            case INSTRUCTOR:
                User instructor = userService.getUserByEmail(currentUserDTO.getEmail());
                submissions = submissionService.getSubmissionsByInstructor(instructor);
                tasks = taskService.getTasksByInstructor(instructor);
                page = "/instructor/Submissions.jsp";
                break;
            case STUDENT:
                User student = userService.getUserByEmail(currentUserDTO.getEmail());
                submissions = submissionService.getSubmissionsByStudent(student);
                students = userService.getUsersByRole(role);
                tasks = taskService.getAllTasks();
                page = "/student/Submissions.jsp";
                break;
            default:
                response.sendRedirect("error.jsp"); // Handle unexpected role case
                return;
        }

        submissions
                .forEach(submission -> submission.setFilePath(submissionService.getFileName(submission.getFilePath())));

        if (submissions != null || tasks != null) {
            request.setAttribute("submissions", submissions);
            request.setAttribute("tasks", tasks);

            request.setAttribute("message", "Submissions got successfully");
            request.setAttribute("messageType", "success");
            request.getRequestDispatcher(page).forward(request, response);
        } else {
            request.setAttribute("message", "Error getting submissions");
            request.setAttribute("messageType", "danger");
            request.getRequestDispatcher(page).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action == null){
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } else if ("add".equalsIgnoreCase(action)) {
            handleSubmissionRegistration(request, response);
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
        }else{
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }

        UUID taskId = UUID.fromString(request.getParameter("taskId"));
        String filePath = "";
        Part filePart = request.getPart("submissionFile"); // Ensure the form has enctype="multipart/form-data"

        try{
            // Ensure file is present
            if (filePart == null || filePart.getSize() == 0) {
                response.getWriter().write("No file uploaded. <a href="+ request.getContextPath() + "/submission" +">Return back</a>");
                return;
            }

            submissionService.isValidFile(filePart);
            filePath = submissionService.saveFileLocally(filePart);

            User student = userService.getUserByEmail(currentUserDTO.getEmail());
            Task task = taskService.getTaskById(taskId);

            // Check if both student and task are found
            if (student != null && task != null) {

                // Create the submission object
                Submission submission = new Submission(student, task, filePath);

                // Register the submission via the service
                if (submissionService.registerSubmission(submission)) {
                    // let's save the
                    response.getWriter().write("Submission created successfully!, <a href=" + request.getContextPath()+ "/submission" +">Return back</a>");
                } else {
                    response.getWriter().write("Submission creation failed. <a href=" + request.getContextPath()+ "/submission" + ">Return back</a>");
                }
            } else {
                response.getWriter().write("Invalid student or task. <a href=" + request.getContextPath()+ "/submission" + ">Return back</a>");
            }
        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // Set status code to 500
            response.getWriter().write("Error with submission registration: " + e.getMessage() +
                    "<br> <a href=\"" + request.getContextPath() + "/submission\">Return back</a>");
            e.printStackTrace();
        }
    }

    private void handleSubmissionDeletion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        UUID submissionId = UUID.fromString(request.getParameter("submissionId"));


        if (submissionService.deleteSubmission(submissionId)) {
            response.getWriter().write("Submission deleted successfully. <a href=" + request.getContextPath()+ "/submission" + ">Return back</a>");
        } else {
            response.getWriter().write("Error deleting submission. <a href=" + request.getContextPath() + "/submission" + ">Return back</a>");
        }
    }
}
