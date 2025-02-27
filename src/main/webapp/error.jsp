<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Error</title>
</head>
<body>
    <%-- Display success or error messages --%>
    <% String message = (String) request.getAttribute("message"); %>
    <% String messageType = (String) request.getAttribute("messageType"); %>
    <% if (message != null && messageType != null) { %>
    <div class="alert alert-<%= "success".equals(messageType) ? "success" : "danger" %> align-content-center" role="alert">
        <%= message %>
    </div>
    <% } %>
</body>
</html>
