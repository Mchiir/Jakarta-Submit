<%@ page import="home.jakartasubmit.DTOs.UserDTO" %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%
  HttpSession sessionobj = request.getSession(false);
  if (sessionobj == null || sessionobj.getAttribute("isLoggedIn") == null || !(boolean) sessionobj.getAttribute("isLoggedIn")) {
    response.sendRedirect("auth/login.jsp");
    return;
  }
  UserDTO currentUser = (UserDTO) sessionobj.getAttribute("currentUser");
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
  <head>
    <title>Student Dashboard</title>
    <link
      href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.css"
      rel="stylesheet"
      type="text/css"
    />
    <link
      href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css"
      rel="stylesheet"
      type="text/css"
    />
    <style>
      .sidebar {
        height: 100vh;
        width: 250px;
        background-color: #343a40;
        color: white;
        padding-top: 20px;
        position: fixed;
        left: 0;
        top: 50px;
        z-index: 999;

        display: flex;
        box-sizing: border-box;
        flex-direction: column;
        justify-content: space-between;
        padding-bottom: 70px; /* Adds spacing at the bottom */
      }

      .sidebar a {
        color: white;
        text-decoration: none;
        display: block;
        padding: 10px 20px;
        background-color: transparent;
        width: 100%;
      }

      .sidebar a:hover {
        background-color: #495057;
      }

      .logout-container {
        margin-top: auto;
      }

      .sidebar .logout-button {
        color: white;
        text-decoration: none;
        display: block;
        padding: 10px 20px;
        background-color: transparent;
        width: 100%;
        border: 1px solid #343a40;
        outline: 0;
        height: 44px;
      }
      .sidebar .logout-button:hover {
        background-color: white;
      }

      .sidebar {
        display: flex;
        justify-content: space-between;
      }

      .sidebar .logout-button:hover {
        background-color: #ccc;
        color: #343a40;
      }

      .header {
        background-color: #37424d;
        color: white;
        padding: 10px;
        text-align: center;
        font-size: 20px;
        font-weight: bold;
        position: fixed;
        top: 0;
        width: 100%;
        z-index: 1000;
      }
      .content {
        margin-top: 50px;
        margin-left: 250px;
        padding: 20px;
      }
      .feature-box {
        width: 30%;
        padding: 20px;
        margin: 10px;
        text-align: center;
        background-color: #ffffff;
        border-radius: 10px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        cursor: pointer;
      }
      .feature-box:hover {
        background-color: #ebeef2;
      }
      .dashboard-content {
        display: flex;
        justify-content: space-around;
        margin-top: 20px;
      }
    </style>
  </head>
  <body style="background-color: #f4f4f9">
    <div class="header">STUDENT</div>

    <div class="sidebar d-flex flex-column">
      <div>
        <a href="#">Dashboard</a>
        <a href="${pageContext.request.contextPath}/task">Tasks</a>
        <a href="${pageContext.request.contextPath}/submission">Submissions</a>
        <a href="./Profile.jsp">Profile</a>
      </div>

      <div class="logout-container">
        <form action="${pageContext.request.contextPath}/user" method="POST">
          <input type="hidden" name="action" value="logout">

          <input type="submit" value="LOGOUT" class="logout-button">
        </form>
      </div>
    </div>

    <div class="container p-4 flex-grow-1 content">
      <c:if test="${not empty sessionScope.currentUser}">
        <h2>Welcome, ${sessionScope.currentUser.email}!</h2>
      </c:if>
      <p>Select an option from the sidebar.</p>
      <div class="dashboard-content">
        <a
          href="${pageContext.request.contextPath}/task"
          class="nav-link feature-box text-dark text-decoration-none"
        >
          <h4>Tasks</h4>
          <p>View and manage posted tasks.</p>
        </a>
        <a
          href="${pageContext.request.contextPath}/submission"
          class="nav-link feature-box text-dark text-decoration-none"
        >
          <h4>Submissions</h4>
          <p>Check your submission history.</p>
        </a>
        <a
          href="./Profile.jsp"
          class="feature-box text-dark text-decoration-none"
        >
          <h4>Profile</h4>
          <p>Your personal information.</p>
        </a>
      </div>
    </div>

    <script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
