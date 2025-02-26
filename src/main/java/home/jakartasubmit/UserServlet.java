package home.jakartasubmit;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.jakartasubmit.models.User;
import home.jakartasubmit.services.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@WebServlet(name = "UserServlet", value = "/user-servlet")
public class UserServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equalsIgnoreCase(action)) {
            handleUserRegistration(request, response);
        } else if ("login".equalsIgnoreCase(action)) {
            handleUserLogin(request, response);
        }
    }

    private void handleUserRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User.Role role = User.Role.valueOf(request.getParameter("role").toUpperCase());

        User user = new User(fullName, email, password, role);
        boolean isSuccess = userService.registerUser(user);
        String message = isSuccess ? "User registered successfully" : "User registration failed, email in use.";
        String messageType = isSuccess ? "success" : "danger";

        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);
        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.forward(request, response);
    }

    private void handleUserLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        boolean loginSuccessful = userService.loginUser(email, password);
        if (loginSuccessful) {
            User loggedInUser = userService.getUserByEmail(email);
            HttpSession session = request.getSession();
            session.setAttribute("userEmail", email);
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("userRole", loggedInUser.getRole());

            String dashboardPage = switch (loggedInUser.getRole()) {
                case ADMIN -> "admin.jsp";
                case STUDENT -> "student.jsp";
                case INSTRUCTOR -> "instructor.jsp";
            };
            RequestDispatcher dispatcher = request.getRequestDispatcher(dashboardPage);
            dispatcher.forward(request, response);
        } else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<div>Login failed! <a href=\"register.jsp\">Register</a> or <a href=\"login.jsp\">login again</a></div>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> responseJson = new HashMap<>();

        String email = request.getParameter("email");
        String id = request.getParameter("id");

        try {
            User user = null;
            if (email != null) {
                user = userService.getUserByEmail(email);
            } else if (id != null) {
                user = userService.getUserById(UUID.fromString(id));
            }

            if (user != null) {
                responseJson.put("exists", true);
                responseJson.put("user", userService.convertToDTO(user));
            } else {
                responseJson.put("exists", false);
            }
            response.setStatus(200);
        } catch (Exception e) {
            response.setStatus(500);
            responseJson.put("error", "An error occurred while processing the request.");
        }
        out.write(new ObjectMapper().writeValueAsString(responseJson));
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID userId = UUID.fromString(request.getParameter("id"));
        User user = userService.getUserById(userId);

        if (user != null) {
            user.setFullName(request.getParameter("fullName"));
            user.setEmail(request.getParameter("email"));
            user.setPassword(request.getParameter("password"));

            boolean isUpdated = userService.updateUser(user);
            response.getWriter().write(isUpdated ? "User updated successfully." : "Error updating user.");
        } else {
            response.getWriter().write("User not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID userId = UUID.fromString(request.getParameter("id"));
        boolean isDeleted = userService.deleteUser(userId);
        response.getWriter().write(isDeleted ? "User deleted successfully." : "Error deleting user.");
    }
   }
