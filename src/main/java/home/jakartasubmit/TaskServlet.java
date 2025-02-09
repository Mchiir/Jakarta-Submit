package home.jakartasubmit;

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
import java.time.LocalDateTime;
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
        LocalDateTime deadline = LocalDateTime.parse(request.getParameter("deadline"));

        // Fetch the instructor from the database using UserService
        User instructor = userService.getUserById(instructorId);

        // Check if the instructor is valid
        if (instructor.isValid()) {
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
        UUID taskId = UUID.fromString(request.getParameter("id"));
        Task task = taskService.getTaskById(taskId);

        if (task != null) {
            response.getWriter().write("Task: " + task.getCourseName());
        } else {
            response.getWriter().write("Task not found.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID taskId = UUID.fromString(request.getParameter("id"));
        Task task = taskService.getTaskById(taskId);

        if (task != null) {
            task.setCourseName(request.getParameter("courseName"));
            task.setDescription(request.getParameter("description"));
            task.setDeadline(LocalDateTime.parse(request.getParameter("deadline")));
            if (taskService.updateTask(task)) {
                response.getWriter().write("Task updated successfully.");
            } else {
                response.getWriter().write("Error updating task.");
            }
        } else {
            response.getWriter().write("Task not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID taskId = UUID.fromString(request.getParameter("id"));
        if (taskService.deleteTask(taskId)) {
            response.getWriter().write("Task deleted successfully.");
        } else {
            response.getWriter().write("Error deleting task.");
        }
    }
}
