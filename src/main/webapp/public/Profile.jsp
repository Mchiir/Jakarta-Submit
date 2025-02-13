<%@ page import="home.jakartasubmit.models.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User loggedInUser = (User) request.getAttribute("loggedInUser");

    if (loggedInUser != null) {
        session.setAttribute("userEmail", loggedInUser.getEmail());
        session.setAttribute("isLoggedIn", true);
        session.setAttribute("userRole", loggedInUser.getRole().toString());
    } else {
        String userEmail = "asdf@gmail.com";
        boolean isLoggedIn = true;
        String userRole = "STUDENT";

        session.setAttribute("userEmail", userEmail);
        session.setAttribute("isLoggedIn", isLoggedIn);
        session.setAttribute("userRole", userRole);
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Profile</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<div class="profile-container">
    <h1>User Profile</h1>

    <!-- Check if the user is logged in -->
    <c:if test="${not empty userEmail}">
        <div class="user-info">
            <p><strong>Full Name:</strong> ${user.fullName}</p>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Role:</strong> ${user.role}</p>
        </div>

        <!-- Edit Profile Button -->
        <form action="user-servlet" method="POST">
            <input type="hidden" name="action" value="editProfile">
            <input type="hidden" name="userId" value="${user.userId}">
            <button type="submit">Edit Profile</button>
        </form>

    </c:if>

    <!-- Message if the user is not logged in -->
    <c:if test="${empty userEmail}">
        <p>You are not logged in. <a href="/auth/login.jsp">Login</a> to view your profile.</p>
    </c:if>
</div>
</body>
</html>