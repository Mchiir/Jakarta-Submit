<%@ page import="home.jakartasubmit.DTOs.UserDTO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    HttpSession sessionObj = request.getSession(false);
    if (sessionObj == null || sessionObj.getAttribute("isLoggedIn") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    boolean isLoggedIn = sessionObj.getAttribute("isLoggedIn") != null;
    UserDTO currentUser = (UserDTO) sessionObj.getAttribute("currentUser");
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.bundle.min.js"></script>
    <title>User Profile</title>
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="mr-3">
            <!-- Back Button (Left-Aligned) -->
            <c:choose>
                <c:when test="${sessionScope.currentUser.role == 'STUDENT'}">
                    <a href="${pageContext.request.contextPath}/student/student.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>
                </c:when>
                <c:when test="${sessionScope.currentUser.role == 'INSTRUCTOR'}">
                    <a href="${pageContext.request.contextPath}/instructor/instructor.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>
                </c:when>
                <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/admin/admin.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/error.jsp" class="btn btn-outline-secondary position-absolute start-0">Back</a>
                </c:otherwise>
            </c:choose>

        </div>

        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white text-center">
                    <h2>User Profile</h2>
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
                        <form action="/Jakarta-Submit-1.0-SNAPSHOT/user" method="POST" class="text-center mt-3">
                            <input type="hidden" name="action" value="editProfile">

                            <input type="hidden" name="userEmail" value="${sessionScope.currentUser.email}">
                            <button type="submit" class="btn btn-primary">Edit Profile</button>
                        </form>
                    </c:if>

                    <!-- Message if the user is not logged in -->
                    <c:if test="${empty sessionScope.currentUser.email}">
                        <div class="alert alert-warning text-center">
                            <p>You are not logged in. <a href="login.jsp" class="alert-link">Login</a> to view your profile.</p>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>