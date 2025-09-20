<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
</head>
<body class="d-flex justify-content-center align-items-center" style="height: 100vh; background-color: #f4f4f9;">

<!-- Container for Centered Content -->
<div class="container bg-white p-4 rounded shadow" style="max-width: 500px;">
    <p class="text-center mb-4" style="font-size: 2rem; font-weight: bold;">Online Submission System</p>

    <!-- Display Message -->
    <c:if test="${not empty message}">
        <div class="alert alert-${messageType == 'success' ? 'success' : 'danger'}">
                ${message}
        </div>
    </c:if>

    <!-- Conditional Button -->
    <div class="d-flex justify-content-center gap-3">
        <a href="${pageContext.request.contextPath}/auth/register.jsp">Register</a>
        <a href="${pageContext.request.contextPath}/auth/login.jsp">Login</a>
    </div>
</div>

</body>
</html>