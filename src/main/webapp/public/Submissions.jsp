<%@ page import="home.jakartasubmit.DTOs.UserDTO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    HttpSession sessionobj = request.getSession(false);
    if (sessionobj == null || sessionobj.getAttribute("isLoggedIn") == null || !(boolean) sessionobj.getAttribute("isLoggedIn")) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submissions Manager</title>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
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
    <h2 class="text-center mb-4">Submissions Manager</h2>
</div>

<%-- Display success or error messages --%>
<% String message = (String) request.getAttribute("message"); %>
<% String messageType = (String) request.getAttribute("messageType"); %>
<% if (message != null && messageType != null) { %>
<div class="alert alert-<%= "success".equals(messageType) ? "success" : "danger" %>" role="alert">
    <%= message %>
</div>
<% } %>

<h3>All Submissions</h3>
<table class="table table-striped table-hover">
    <thead class="table-dark">
    <tr>
        <th>Student</th>
        <th>Task</th>
        <th>File</th>
        <th>Submitted At</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>

    <c:forEach items="${submissions}" var="submission">
        <tr>
            <c:choose>
                <c:when test="not empty submission">
                    <td>${submission.student.fullName}</td>
                    <td>${submission.task.courseName}</td>
                    <td><a href="${submission.filePath}" target="_blank">View File</a></td>
                    <td>${submission.submittedAt}</td>
                </c:when>
                <c:otherwise>
                    <td colspan="4">No submissions yet!</td>
                </c:otherwise>
            </c:choose>

            <td>
                <c:choose>
                    <c:when test="${sessionScope.currentUser.role == 'STUDENT' || sessionScope.currentUser.role == 'ADMIN'}">
                        <form action="/Jakarta-Submit-1.0-SNAPSHOT/submission" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="submissionId" value="${submission.submissionId}">
                            <button type="submit" class="btn btn-primary btn-sm">Edit</button>
                        </form>
                        <form action="/Jakarta-Submit-1.0-SNAPSHOT/submission" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="submissionId" value="${submission.submissionId}">
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

<h3>Add New Submission</h3>
<c:if test="${sessionScope.currentUser.role == 'STUDENT'}">
    <form action="/Jakarta-Submit-1.0-SNAPSHOT/submission" method="POST" class="border p-4 rounded shadow-sm bg-light">
        <input type="hidden" name="action" value="add">
        <div class="mb-3">
            <label class="form-label">Student:</label>
            <select name="studentId" class="form-control" required>
                <c:choose>
                    <c:when test="${not empty students}">
                        <c:forEach var="student" items="${students}">
                            <option value="${student.userId}">${student.fullName}</option>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <option disabled>No students available</option>
                    </c:otherwise>
                </c:choose>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Task:</label>
            <select name="taskId" class="form-control" required>
                <c:choose>
                    <c:when test="${not empty tasks}">
                        <c:forEach var="task" items="${tasks}">
                            <option value="${task.taskId}">${task.courseName}</option>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <option disabled>No tasks available</option>
                    </c:otherwise>
                </c:choose>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">File Path:</label>
            <input type="text" name="filePath" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-success">Add Submission</button>
    </form>
</c:if>

<script src="/WEB-INF/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>