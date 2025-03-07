<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
</head>

<body class="d-flex flex-column justify-content-center align-items-center"
      style="height: 100vh; background-color: #f4f4f9;">

<%-- Display success or error messages --%>
<% String message = (String) request.getAttribute("message"); %>
<% String messageType = (String) request.getAttribute("messageType"); %>
<% if (message != null && messageType != null) { %>
<div style="text-align: center" class="alert alert-<%= "success".equals(messageType) ? "success" : "danger" %>" role="alert">
    <%= message %>
</div>
<% } %>

<!-- Container for Centered Content -->
<div class="container bg-white p-4 rounded shadow mt-1" style="width: 30%; padding-top: 20px; padding-bottom: 20px;">
    <p class="text-center mb-4" style="font-size: 2rem; font-weight: bold;">Online Submission System</p>

    <!-- Form  -->
    <form action="${pageContext.request.contextPath}/user" method="POST" id="form"> <!-- Adjust the action URL to match your servlet's URL pattern -->
        <input type="hidden" name="action" id="action" value="register"> <!-- Action is 'register' -->

        <!-- Full Name -->
        <div class="mb-3">
            <label for="fullName" class="form-label">Full Name</label>
            <input type="text" class="form-control" id="fullName" name="fullName" required>
        </div>

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

        <!-- Role -->
        <div class="mb-3">
            <label for="role" class="form-label">Role</label>
            <select class="form-select" id="role" name="role" required>
                <option value="STUDENT">Student</option>
                <option value="INSTRUCTOR">Instructor</option>
                <option value="ADMIN">Admin</option>
            </select>
        </div>

        <div class="d-flex justify-content-center">
            <button type="submit" class="btn btn-primary">Register</button>
        </div>

        <div class="d-flex justify-content-center">
            <a href="login.jsp">Login</a>
        </div>
    </form>
</div>

</body>
<!--
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            let form = document.getElementById("form");

            form.addEventListener('submit', async (e) => {
                e.preventDefault(); // Prevent form submission

                let fullName = document.getElementById("fullName").value;
                let email = document.getElementById("email").value;
                let password = document.getElementById("password").value;
                let role = document.getElementById("role").value;

                try {
                    // Check if the email exists before submitting
                    let emailCheckResponse = await fetch(`/Jakarta-Submit-1.0-SNAPSHOT/user?email=${email}`);
                    if (!emailCheckResponse.ok) {
                        throw new Error("Email check failed");
                    }
                    let emailExists = await emailCheckResponse.json();

                    if (emailExists.exists) {
                        console.log("email check response:"+ emailCheckResponse)
                        document.getElementById("error_space").innerText = "Email already in use. Please use another email.";
                        document.getElementById("error_space").style.color = "coral";
                        return;
                    }

                    // Proceed with registration if email is unique
                    const registerResponse =await fetch("/Jakarta-Submit-1.0-SNAPSHOT/user", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ action: "register", fullName, email, password, role })
                    });

                    // let result = await registerResponse.json();
                    let result = await registerResponse.json();
                    console.log("Registered response:", result);

                    if (registerResponse.ok) {
                        window.location.href = "reg-welcome.jsp"; // Redirect to welcome page
                    } else {
                        document.getElementById("error_space").innerText = result.message || "Registration failed.";
                        document.getElementById("error_space").style.color = "coral";
                    }
                } catch (error) {
                    console.error("Error:", error);
                    document.getElementById("error_space").innerText = "An error occurred. Please try again.";
                    document.getElementById("error_space").style.color = "coral";
                }
            });
        });
    </script> -->
</html>