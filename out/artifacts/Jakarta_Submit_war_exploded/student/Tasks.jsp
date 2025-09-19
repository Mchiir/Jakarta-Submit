<%@ page import="home.jakartasubmit.DTOs.UserDTO" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="f" uri="http://example.com/functions" %>

<%
    HttpSession sessionobj = request.getSession(false);
    if (sessionobj == null || sessionobj.getAttribute("isLoggedIn") == null || !(boolean) sessionobj.getAttribute("isLoggedIn")) {
        response.sendRedirect("auth/login.jsp");
        return;
    }

    UserDTO currentUser = (UserDTO) sessionobj.getAttribute("currentUser");
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Task Manager</title>
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
</head>
<body class="container py-4">
<div class="position-relative p-3">
    <a href="${pageContext.request.contextPath}/student/student.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>

    <!-- Page Title (Centered) -->
    <h2 class="text-center m-0 w-100">Task Manager</h2>
</div>

<%-- Display success or error messages --%>
<% String message = (String) request.getAttribute("message"); %>
<% String messageType = (String) request.getAttribute("messageType"); %>
<% if (message != null && messageType != null) { %>
<div class="alert alert-<%= "success".equals(messageType) ? "success" : "danger" %>" role="alert">
    <%= message %>
</div>
<% } %>

<h3>All Tasks</h3>

<table class="table table-striped table-hover">
    <thead class="table-dark">
    <tr>
        <th>Subject</th>
        <th>Instructor</th>
        <th>Description</th>
        <th>Deadline</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="task" items="${tasks}">
        <tr>
            <td>${task.courseName}</td>
            <td>${task.instructor.fullName}</td>
            <td>${task.description}</td>
            <td>${f:formatLocalDateTime(task.deadline, 'EEE dd/MM/yyyy HH')}:00</td>

            <td>
                <c:choose>
                    <c:when test="${sessionScope.currentUser.role == 'INSTRUCTOR' || sessionScope.currentUser.role == 'ADMIN'}">
                        <form action="${pageContext.request.contextPath}/task" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="taskId" value="${task.taskId}">
                            <button type="submit" class="btn btn-primary btn-sm">Edit</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/task" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="taskId" value="${task.taskId}">
                            <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure?');">Delete</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <span>View Only</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>