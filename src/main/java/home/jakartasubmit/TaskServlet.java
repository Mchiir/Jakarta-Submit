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

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("isLoggedIn") == null || !(Boolean) session.getAttribute("isLoggedIn")) {
            request.setAttribute("message", "Please log in to continue.");
            request.setAttribute("messageType", "danger");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
            return;

        }

        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            request.setAttribute("message", "Please log in to continue.");
            request.setAttribute("messageType", "danger");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }

        User.Role role = currentUser.getRole();
        String page;
        List<Task> tasks;
        String message;
        String messageType;

        switch (role) {
            case ADMIN -> {
                page = "/admin/Tasks.jsp";
                tasks = taskService.getAllTasks();
                message = tasks.isEmpty() ? "No tasks so far.": "All tasks loaded successfully.";
                messageType = "success";
            }
            case INSTRUCTOR -> {
                page = "/instructor/Tasks.jsp";
                User instructor = userService.getUserByEmail(currentUser.getEmail());
                tasks = taskService.getTasksByInstructor(instructor);
                message = tasks.isEmpty() ? "No tasks created so far for your courses." : "Created tasks loaded successfully.";
                messageType = "success";
            }
            case STUDENT -> {
                page = "/student/Tasks.jsp";
//                User student = userService.getUserByEmail(currentUser.getEmail());
                tasks = taskService.getAllTasks();
                message = tasks.isEmpty() ? "No tasks so far." : "Tasks loaded successfully.";
                messageType = "success";
            }
            default -> {
                request.setAttribute("message", "Unexpected user role: " + role);
                request.setAttribute("messageType", "danger");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }

        // Forward response
        request.setAttribute("tasks", tasks);
        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);
        request.getRequestDispatcher(page).forward(request, response);
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
}
