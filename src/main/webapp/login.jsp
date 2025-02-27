<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
</head>

<body class="d-flex justify-content-center align-items-center"
      style="height: 100vh; background-color: #f4f4f9;">

<!-- Container for Centered Content -->
<div class="container bg-white p-4 rounded shadow" style="width: 30%; padding-top: 20px; padding-bottom: 20px;">
    <p class="text-center mb-4" style="font-size: 2rem; font-weight: bold;">Login to Your Account</p>

    <!-- Form -->
    <form action="/Jakarta-Submit-1.0-SNAPSHOT/user" method="POST">
        <input type="hidden" name="action" value="login"> <!-- Action is 'register' -->

        <!-- Email -->
        <div class="mb-3">
            <label for="email" class="form-label">Email</label>
            <input type="email" class="form-control" id="email" name="email" required>
        </div>

        <!-- Password -->
        <div class="mb-3">
            <label for="password" class="form-label">Password</label>
            <input type="password" class="form-control" id="password" name="password" required>
        </div>

        <div class="d-flex justify-content-center">
            <button type="submit" class="btn btn-primary">Login</button>
        </div>

        <%-- Display messages if available --%>
        <c:if test="${not empty message}">
            <p class="alert alert-${messageType} text-center" style="color: coral; font-size: 16px;" id="error_space">${message}</p> <!-- Error message space -->
        </c:if>

        <div class="d-flex justify-content-center">
            <a href="register.jsp">Register</a>
        </div>
    </form>
</div>

</body>

</html>