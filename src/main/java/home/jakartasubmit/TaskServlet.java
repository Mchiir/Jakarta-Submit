package home.jakartasubmit;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.jakartasubmit.models.Task;
import home.jakartasubmit.models.User;
import home.jakartasubmit.services.TaskService;
import home.jakartasubmit.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "TaskServlet", value = "/task-servlet")
public class TaskServlet extends HttpServlet {
    private final TaskService taskService = new TaskService();
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Retrieve parameters from the request
        UUID instructorId = UUID.fromString(request.getParameter("instructorId"));
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

        User instructor = userService.getUserById(instructorId);

        if (userService.isValid(instructor)) {
            // Create the Task object
            Task task = new Task(instructor, courseName, description, deadline);

            // Register the Task
            if (taskService.registerTask(task)) {
                response.getWriter().write("Task successfully created!");
            } else {
                response.getWriter().write("Task creation failed.");
            }
        } else {
            response.getWriter().write("Instructor not found.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> responseJson = new HashMap<>();

        try {
            UUID taskId = UUID.fromString(request.getParameter("id"));
            Task task = taskService.getTaskById(taskId);

            if (task != null) {
                responseJson.put("exists", true);
                responseJson.put("task", task);
            } else {
                responseJson.put("exists", false);
            }
            response.setStatus(200);
        }catch (Exception e) {
            responseJson.put("exists", false);
            responseJson.put("error", "An error occurred while processing the request.");
        }

        out.write(new ObjectMapper().writeValueAsString(responseJson));
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID taskId = UUID.fromString(request.getParameter("id"));
        Task task = taskService.getTaskById(taskId);

        if (task != null) {
            task.setCourseName(request.getParameter("courseName"));
            task.setDescription(request.getParameter("description"));
            task.setDeadline(LocalDateTime.parse(request.getParameter("deadline")));

            boolean isUpdated = taskService.updateTask(task);
                response.getWriter().write(isUpdated ? "Task updated successfully.": "Task update failed.");
        } else {
            response.getWriter().write("Task not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID taskId = UUID.fromString(request.getParameter("id"));
        boolean isDeleted = taskService.deleteTask(taskId);
        response.getWriter().write(isDeleted ? "Task deleted successfully." : "Task update failed.");
    }
}
