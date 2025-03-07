<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Error</title>
</head>
<body>
<%
    String message = (String) request.getAttribute("message");
    String messageType = (String) request.getAttribute("messageType");

    // Default message and messageType if not set
    if (message == null) {
        message = "No new updates.";  // Default message
    }
    if (messageType == null) {
        messageType = "info";  // Default message type
    }
%>

<% if (message != null && messageType != null) { %>
<div class="alert alert-<%= "success".equals(messageType) ? "success" :
                               ("danger".equals(messageType) ? "danger" : "info") %> align-content-center" role="alert">
    <%= message %>
</div>
<% } %>
</body>
</html>
