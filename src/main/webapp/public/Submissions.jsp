<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submissions Manager</title>
    <link href="../bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
</head>
<body class="container py-4">
<h2 class="text-center mb-4">Submissions Manager</h2>

<%-- Display success or error messages --%>
<% String message = (String) request.getAttribute("message"); %>
<% if (message != null) { %>
<div class="alert alert-info" role="alert">
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
    <c:forEach var="submission" items="${submissions}">
        <tr>
            <td>${submission.student.name}</td>
            <td>${submission.task.courseName}</td>
            <td><a href="<%= submission.getFilePath() %>" target="_blank">View File</a></td>
            <td><fmt:formatDate value="${submission.submittedAt}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
            <td>
                    <%-- Only allow editing and deleting for instructors or admins --%>
                <c:if test="${userRole == 'INSTRUCTOR' || userRole == 'ADMIN'}">
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
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h3>Add New Submission</h3>
<form action="/Jakarta-Submit-1.0-SNAPSHOT/submission" method="POST" class="border p-4 rounded shadow-sm bg-light">
    <input type="hidden" name="action" value="add">
    <div class="mb-3">
        <label class="form-label">Student:</label>
        <select name="studentId" class="form-control" required>
            <c:forEach var="student" items="${students}">
                <option value="${student.userId}">${student.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="mb-3">
        <label class="form-label">Task:</label>
        <select name="taskId" class="form-control" required>
            <c:forEach var="task" items="${tasks}">
                <option value="${task.taskId}">${task.courseName}</option>
            </c:forEach>
        </select>
    </div>
    <div class="mb-3">
        <label class="form-label">File Path:</label>
        <input type="text" name="filePath" class="form-control" required>
    </div>
    <button type="submit" class="btn btn-success">Add Submission</button>
</form>

<script src="../bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>