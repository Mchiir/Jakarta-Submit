<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="home.jakartasubmit.models.Task" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Task Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</head>
<body class="container py-4">
<h2 class="text-center mb-4">Task Manager</h2>

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
    <%
        List<Task> tasks = (List<Task>) request.getAttribute("tasks");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String userRole = (String) session.getAttribute("role"); // Assuming user role is stored in session
        for (Task task : tasks) {
    %>
    <tr>
        <td><%= task.getCourseName() %></td>
        <td><%= task.getDescription() %></td>
        <td><%= task.getDeadline().format(formatter) %></td>
        <td>
            <% if ("INSTRUCTOR".equals(userRole) || "ADMIN".equals(userRole)) { %>
            <!-- Edit and Delete buttons only visible for INSTRUCTOR or ADMIN -->
            <form action="/Jakarta-Submit-1.0-SNAPSHOT/task" method="POST" class="d-inline">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="taskId" value="<%= task.getTaskId() %>">
                <button type="submit" class="btn btn-primary btn-sm">Edit</button>
            </form>
            <form action="/Jakarta-Submit-1.0-SNAPSHOT/task" method="POST" class="d-inline">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="taskId" value="<%= task.getTaskId() %>">
                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure?');">Delete</button>
            </form>
            <% } else { %>
            <!-- Display message for non-ADMIN/INSTRUCTOR users -->
            <span>View Only</span>
            <% } %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>

<% if ("INSTRUCTOR".equals(userRole) || "ADMIN".equals(userRole)) { %>
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

<script src="../bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>