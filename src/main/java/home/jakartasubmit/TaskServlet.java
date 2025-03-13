package home.jakartasubmit;

import home.jakartasubmit.DTOs.UserDTO;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.models.User;
import home.jakartasubmit.services.TaskService;
import home.jakartasubmit.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet(name = "TaskServlet", value = "/task-servlet")
public class TaskServlet extends HttpServlet {
    private final TaskService taskService = new TaskService();
    private final UserService userService = new UserService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> responseJson = new HashMap<>();

        HttpSession session = request.getSession(false);
        Boolean isLoggedIn = false;
        UserDTO currentUser = null;
        if (session != null) {
            isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
            currentUser = (UserDTO) session.getAttribute("currentUser");
        }
        User.Role role = currentUser.getRole();
        String page = "";
        page = switch (currentUser.getRole()) {
            case ADMIN -> "/admin/Tasks.jsp";
            case INSTRUCTOR -> "/instructor/Tasks.jsp";
            case STUDENT -> "/student/Tasks.jsp";
            default -> page = "/error.jsp";
        };

        List<Task> tasks = taskService.getAllTasks();
        switch (role) {
            case ADMIN:
                tasks = taskService.getAllTasks();
                break;
            case INSTRUCTOR:
                User instructor = userService.getUserByEmail(currentUser.getEmail());
                tasks = taskService.getTasksByInstructor(instructor);
                break;
            case STUDENT:
                User student = userService.getUserByEmail(currentUser.getEmail());
                tasks = taskService.getAllTasks();
                break;
            default:
                response.sendRedirect("error.jsp"); // Handle unexpected role case
                return;
        }

        if (tasks != null) {
            request.setAttribute("tasks", tasks);

            request.setAttribute("message", "Submissions got successfully");
            request.setAttribute("messageType", "success");
            request.getRequestDispatcher(page).forward(request, response);
        } else {
            request.setAttribute("message", "Error getting submissions");
            request.setAttribute("messageType", "danger");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action == null) {
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } else if ("add".equalsIgnoreCase(action)) {
            handleRegisterTask(request, response);
        } else if("delete".equalsIgnoreCase(action)) {
            handleTaskDeletion(request, response);
        }
    }

    private void handleRegisterTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        // Retreiving formData
        HttpSession sessionobj = request.getSession(false);
        UserDTO currentUserDTO = null;
        if (sessionobj != null && sessionobj.getAttribute("isLoggedIn") != null) {
            currentUserDTO = (UserDTO) sessionobj.getAttribute("currentUser");
        }else{
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
        String email = currentUserDTO.getEmail();

        // Retrieve parameters from the request
        String courseName = request.getParameter("courseName");
        String description = request.getParameter("description");
        LocalDateTime deadline = null;

        try {
            deadline = LocalDateTime.parse(request.getParameter("deadline"));
        } catch (DateTimeParseException e) {
            response.getWriter().write("Invalid deadline format. Please use a valid date-time format.");
            return; // Exit early
        }
        if (courseName == null || courseName.isEmpty() || description == null || description.isEmpty()) {
            response.getWriter().write("Course name and description are required.");
            return; // Exit early
        }

        try{
            User instructor = userService.getUserByEmail(email);

            if (userService.isValid(instructor)) {
                // Create the Task object
                Task task = new Task(instructor, courseName, description, deadline);

                // Register the Task
                taskService.registerTask(task, currentUserDTO);
                response.getWriter().write("Task created successfully!, <a href=" + request.getContextPath() + "/task" + ">Return back</a>");
            }
        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // Set status code to 500
            response.getWriter().write("Error with task registration: "+ e.getMessage() +"<br><a href=" + request.getContextPath() + "/task" + ">Return back</a>");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID taskId = UUID.fromString(request.getParameter("id"));
        Task task = taskService.getTaskById(taskId);

        var sessionobj = request.getSession(false);
        if(sessionobj != null && sessionobj.getAttribute("isLoggedIn") != null) {
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
        var currentUser = (UserDTO) sessionobj.getAttribute("currentUser");

        if (task != null) {
            try {
                task.setCourseName(request.getParameter("courseName"));
                task.setDescription(request.getParameter("description"));
                task.setDeadline(LocalDateTime.parse(request.getParameter("deadline")));

                taskService.updateTask(task, currentUser);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        }
    }

    private void handleTaskDeletion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        UUID taskId = UUID.fromString(request.getParameter("taskId"));

        try{
            taskService.deleteTask(taskId);
            response.getWriter().write("Task deleted successfully. <a href=" + request.getContextPath()+ "/task" + ">Return back</a>");
        } catch(Exception e) {
            response.getWriter().write("Error deleting task: "+ e.getMessage() +"<br><a href=" + request.getContextPath() + "/task" + ">Return back</a>");
        }
    }
}
