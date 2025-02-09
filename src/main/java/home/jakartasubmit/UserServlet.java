package home.jakartasubmit;

import home.jakartasubmit.models.User;
import home.jakartasubmit.services.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

//@WebServlet(name = "UserServlet", value = "/user-servlet")
public class UserServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // If the action is register, handle user registration
        if ("register".equalsIgnoreCase(action)) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            User.Role role = User.Role.valueOf(request.getParameter("role").toUpperCase());

            User user = new User(fullName, email, password, role);
            boolean isSuccess = false;
            String message = "";
            String messageType = "";

            try {
//                response.getWriter().write("User successfully registered!");
                userService.registerUser(user);
                isSuccess = true;
                message = "Student successfully registered!";
                messageType = "success";  // Green success message
            } catch(Exception e) {
//                response.getWriter().write("User registration failed.");
                isSuccess = false;
                message = "Registration failed. Please try again.";
                messageType = "danger";  // Red danger message
            }
            // Set message and messageType as request attributes
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);

            // Forward to the welcome page (index.jsp)
            RequestDispatcher dispatcher = request.getRequestDispatcher("reg-welcome.jsp");
            dispatcher.forward(request, response);
        }

        // If the action is login, handle user login
        else if ("login".equalsIgnoreCase(action)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            boolean loginSuccessful = userService.loginUser(email, password);
            boolean isSuccess = false;
            String message = "";
            String messageType = "";

            try {
                if (loginSuccessful) {
    //                response.getWriter().write("Login successful!");
                    messageType = "success";
                    message = "Login successful!";
                    isSuccess = true;
                }
            } catch(Exception e) {
//                response.getWriter().write("Invalid email or password.");
                isSuccess = false;
                message = "Login failed. Please try again.";
                messageType = "danger";
            }

            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
            RequestDispatcher dispatcher = request.getRequestDispatcher("log-welcome.jsp");
            dispatcher.forward(request, response);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID userId = UUID.fromString(request.getParameter("id"));
        User user = userService.getUserById(userId);

        if (user != null) {
            response.getWriter().write("User: " + user.getFullName());
        } else {
            response.getWriter().write("User not found.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID userId = UUID.fromString(request.getParameter("id"));
        User user = userService.getUserById(userId);
        
        if (user != null) {
            user.setFullName(request.getParameter("fullName"));
            user.setEmail(request.getParameter("email"));
            user.setPassword(request.getParameter("password"));
            if (userService.updateUser(user)) {
                response.getWriter().write("User updated successfully.");
            } else {
                response.getWriter().write("Error updating user.");
            }
        } else {
            response.getWriter().write("User not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID userId = UUID.fromString(request.getParameter("id"));
        if (userService.deleteUser(userId)) {
            response.getWriter().write("User deleted successfully.");
        } else {
            response.getWriter().write("Error deleting user.");
        }
    }
}
