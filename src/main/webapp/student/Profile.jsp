<%@ page import="home.jakartasubmit.DTOs.UserDTO" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    HttpSession sessionObj = request.getSession(false);
    if (sessionObj == null || sessionObj.getAttribute("isLoggedIn") == null) {
        response.sendRedirect("auth/login.jsp");
        return;
    }

%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <title>Profile</title>
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="mr-3">
            <a href="${pageContext.request.contextPath}/student/student.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>

        </div>

        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white text-center">
                    <h2>Your Profile</h2>
                </div>
                <div class="card-body">
                    <!-- Check if the user is logged in -->
                    <c:if test="${not empty sessionScope.currentUser.email}">
                        <div class="user-info text-center">
                            <p><strong>Full Name:</strong> ${sessionScope.currentUser.fullName}</p>
                            <p><strong>Email:</strong> ${sessionScope.currentUser.email}</p>
                            <p><strong>Role:</strong> ${sessionScope.currentUser.role}</p>
                        </div>

                        <!-- Edit Profile Button -->
                        <form action="${pageContext.request.contextPath}/user" method="POST" class="text-center mt-3">
                            <input type="hidden" name="action" value="editProfile">

                            <input type="hidden" name="userEmail" value="${sessionScope.currentUser.email}">
                            <button type="submit" class="btn btn-primary">Change</button>
                        </form>
                    </c:if>
                    
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>