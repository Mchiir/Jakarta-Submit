<%@ page import="home.jakartasubmit.DTOs.UserDTO" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%
    HttpSession sessionobj = request.getSession(false);
    if (sessionobj == null || sessionobj.getAttribute("isLoggedIn") == null || !(boolean) sessionobj.getAttribute("isLoggedIn")) {
        response.sendRedirect("login.jsp");
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
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.bundle.min.js"></script>
</head>
<body class="container py-4">
<div class="position-relative p-3">
    <!-- Back Button (Left-Aligned) -->
    <c:choose>
        <c:when test="${sessionScope.currentUser.role == 'STUDENT'}">
            <a href="student.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>
        </c:when>
        <c:when test="${sessionScope.currentUser.role == 'INSTRUCTOR'}">
            <a href="instructor.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>
        </c:when>
        <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
            <a href="admin.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>
        </c:when>
        <c:otherwise>
            <a href="error.jsp" class="btn btn-outline-secondary position-absolute start-0">Back</a>
        </c:otherwise>
    </c:choose>

    <!-- Page Title (Centered) -->
    <h2 class="text-center m-0 w-100">Task Manager</h2>
</div>

<% String message = (String) request.getAttribute("message"); %>
<% if (message != null) { %>
<div class="alert alert-info" role="alert">
    <%= message %>
</div>
<% } %>

<h3>All Tasks</h3>

<table class="table table-striped table-hover">
    <thead class="table-dark">
    <tr>
        <th>Course</th>
        <th>Description</th>
        <th>Deadline</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="task" items="${tasks}">
        <tr>
            <td>${task.courseName}</td>
            <td>${task.description}</td>
            <td>${task.deadline}</td>
            <td>
                <c:choose>
                    <c:when test="${sessionScope.currentUser.role == 'INSTRUCTOR' || sessionScope.currentUser.role == 'ADMIN'}">
                        <form action="/Jakarta-Submit-1.0-SNAPSHOT/task" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="taskId" value="${task.taskId}">
                            <button type="submit" class="btn btn-primary btn-sm">Edit</button>
                        </form>
                        <form action="/Jakarta-Submit-1.0-SNAPSHOT/task" method="POST" class="d-inline">
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

<% if ("INSTRUCTOR".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) { %>
<!-- Add New Task form only visible for INSTRUCTOR or ADMIN -->
<h3>Add New Task</h3>
<form action="/Jakarta-Submit-1.0-SNAPSHOT/task" method="POST" class="border p-4 rounded shadow-sm bg-light">
    <input type="hidden" name="action" value="add">
    <div class="mb-3">
        <label class="form-label">Course Name:</label>
        <input type="text" name="courseName" class="form-control" required>
    </div>
    <div class="mb-3">
        <label class="form-label">Description:</label>
        <textarea name="description" class="form-control" required></textarea>
    </div>
    <div class="mb-3">
        <label class="form-label">Deadline:</label>
        <input type="datetime-local" name="deadline" class="form-control" required>
    </div>
    <button type="submit" class="btn btn-success">Add Task</button>
</form>
<% } %>

<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>