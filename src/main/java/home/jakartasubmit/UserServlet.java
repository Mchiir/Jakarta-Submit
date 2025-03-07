package home.jakartasubmit;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.jakartasubmit.DTOs.UserDTO;
import home.jakartasubmit.models.User;
import home.jakartasubmit.services.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "UserServlet", value = "/user-servlet")
public class UserServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equalsIgnoreCase(action)) {
            handleUserRegistration(request, response);
        } else if ("login".equalsIgnoreCase(action)) {
            handleUserLogin(request, response);
        } else if("logout".equalsIgnoreCase(action)) {
            handleLogOut(request, response);
        }
    }

    private void handleUserRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User.Role role = User.Role.valueOf(request.getParameter("role").toUpperCase());

        User user = new User(fullName, email, password, role);
        String message = null;
        String messageType = null;
        String page = null;

        try{
            if (userService.registerUser(user)) {
                message = "User registered successfully!";
                messageType = "success";
                page = "login.jsp"; // Redirecting to login page after registration
            } else {
                message = "Registration failed. Please try again.";
                messageType = "danger";
                page = "register.jsp"; // Stay on registration page in case of failure
            }
        } catch (Exception e) {
            message = "An error occurred during registration: " + e.getMessage();
            messageType = "danger"; // Use danger type for errors
            page = "register.jsp"; // Go back to the registration page

            // Log the exception for better debugging
            e.printStackTrace();
        }

        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Forward the request to the appropriate page
        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        dispatcher.forward(request, response);
    }

    private void handleUserLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String dashboardPage = "login.jsp"; // Default page on failure
        String message = null;
        String messageType = null;

        try {
            // Attempt login
            if (userService.loginUser(email.trim(), password.trim())) {
                // Fetch user details and convert to DTO
                UserDTO loggedInUser = userService.convertToDTO(userService.getUserByEmail(email));

                // Invalidate any existing session
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                // Create a new session
                HttpSession sessionObj = request.getSession(true);
                sessionObj.setAttribute("isLoggedIn", true);
                sessionObj.setAttribute("currentUser", loggedInUser);

                // Determine the dashboard page based on the user's role
                dashboardPage = switch (loggedInUser.getRole()) {
                    case ADMIN -> "admin.jsp";
                    case STUDENT -> "student.jsp";
                    case INSTRUCTOR -> "instructor.jsp";
                    default -> "login.jsp"; // Default fallback
                };

                // Redirect to the corresponding dashboard page
                RequestDispatcher dispatcher = request.getRequestDispatcher(dashboardPage);
                dispatcher.forward(request, response);
                return; // Exit here as we've handled successful login
            }

            // If login fails, set a failure message
            message = "Login failed. Please try again.";
            messageType = "danger";

        } catch (Exception e) {
            // Handle any exceptions during login
            message = "An error occurred during login: " + e.getMessage();
            messageType = "danger"; // Error style
            e.printStackTrace(); // Log the exception for debugging
        }

        // Set the message and messageType, and forward to the appropriate page
        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);
        request.getRequestDispatcher(dashboardPage).forward(request, response);
    }

    private void handleLogOut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       response.setContentType("text/html");
       PrintWriter out = response.getWriter();

        try{
            HttpSession session = request.getSession(false);
            UserDTO currentUser = null;
            Boolean isLoggedIn = false;
            String dashboardPage = "";

            if(session != null){
                currentUser = (UserDTO) session.getAttribute("currentUser");
                isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");

                dashboardPage = switch (currentUser.getRole()) {
                    case ADMIN -> "admin.jsp";
                    case STUDENT -> "student.jsp";
                    case INSTRUCTOR -> "instructor.jsp";
                };
                session.invalidate();
            }

            Thread.sleep(1000);
            request.getRequestDispatcher(dashboardPage).forward(request, response);
        } catch (Exception e){
            out.println("<html><body>");
            out.println("<div>Error with Logout, "+ e.getMessage() +"</div>");
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
        String email = request.getParameter("userEmail");
        User user = userService.getUserByEmail(email);

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
