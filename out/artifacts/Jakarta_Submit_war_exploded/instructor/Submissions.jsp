<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib prefix="f" uri="http://example.com/functions" %>

<%
    HttpSession sessionobj = request.getSession(false);
    if (sessionobj == null || sessionobj.getAttribute("isLoggedIn") == null || !(boolean) sessionobj.getAttribute("isLoggedIn")) {
        response.sendRedirect("auth/login.jsp");
        return;
    }
%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submissions Manager</title>
    <link href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
</head>
<body class="container py-4">
<div class="position-relative p-3">
    <a href="${pageContext.request.contextPath}/instructor/instructor.jsp" class="btn btn-outline-primary position-absolute start-0">Back</a>


    <!-- Page Title (Centered) -->
    <h2 class="text-center mb-4">Submissions Manager</h2>
</div>

<%-- Display success or error messages --%>
<% String message = (String) request.getAttribute("message"); %>
<% String messageType = (String) request.getAttribute("messageType"); %>
<% if (message != null && messageType != null) { %>
<div style="text-align: center;" class="alert alert-<%= "success".equals(messageType) ? "success" : "danger" %>" role="alert">
    <%= message %>
</div>
<% } %>

<h3 style="text-align: center">Submissions</h3>
<table class="table table-striped table-hover">
    <thead class="table-dark">
    <tr>
        <th>In Course</th>
        <th>Student name</th>
        <th>Student email</th>
        <th>Submission</th>
        <th>File</th>
        <th>Submitted At</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${not empty submissions}"> <%-- EL --%>
            <c:forEach items="${submissions}" var="submission">

                <tr>
                    <td>${submission.task.courseName}</td>
                    <td>${submission.student.fullName}</td>
                    <td>${submission.student.email}</td>
                    <td>${f:formatFileName(submission.filePath)}</td>

                    <td><a href="${pageContext.request.contextPath}/submission?action=download_file&fileName=${submission.filePath}" target="_blank">Download</a></td>
                    <td>${f:formatLocalDateTime(submission.submittedAt, 'EEE dd/MM/yyyy HH:mm:ss')}</td>

                        <td>
                            <c:choose>
                                <c:when test="${sessionScope.currentUser.role == 'STUDENT' || sessionScope.currentUser.role == 'ADMIN'}">
                                    <form action="${pageContext.request.contextPath}/submission" method="POST" class="d-inline">
                                        <input type="hidden" name="action" value="edit">

                                        <input type="hidden" name="submissionId" value="${submission.submissionId}">
                                        <button type="submit" class="btn btn-primary btn-sm">Edit</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/submission" method="POST" class="d-inline">
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
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="5" style="text-align: center;">No submissions yet!</td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>
<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>