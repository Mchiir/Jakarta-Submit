<%@ page import="home.jakartasubmit.DTOs.UserDTO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
  HttpSession sessionobj = request.getSession(false);
  if (sessionobj == null || sessionobj.getAttribute("isLoggedIn") == null || !(boolean) sessionobj.getAttribute("isLoggedIn")) {
    response.sendRedirect("login.jsp");
    return;
  }
  UserDTO currentUser = (UserDTO) sessionobj.getAttribute("currentUser");
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
  <head>
    <title>Student Dashboard</title>
    <link
      href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css"
      rel="stylesheet"
      type="text/css"
    />
    <link
      href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css"
      rel="stylesheet"
      type="text/css"
    />
    <script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.bundle.min.js"></script>
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
      }
      .sidebar a {
        color: white;
        text-decoration: none;
        display: block;
        padding: 10px 20px;
      }
      .sidebar a:hover {
        background-color: #495057;
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
      <a href="#">Dashboard</a>
      <a href="/Jakarta-Submit-1.0-SNAPSHOT/task">Tasks</a>
      <a href="/Jakarta-Submit-1.0-SNAPSHOT/submission">Submissions</a>
      <a href="public/Profile.jsp">Profile</a>
    </div>

    <div class="container p-4 flex-grow-1 content">
      <c:if test="${not empty sessionScope.currentUser}">
        <h2>Welcome, ${sessionScope.currentUser.email}!</h2>
      </c:if>
      <p>Select an option from the sidebar.</p>
      <div class="dashboard-content">
        <a
          href="/Jakarta-Submit-1.0-SNAPSHOT/task"
          class="nav-link feature-box text-dark text-decoration-none"
        >
          <h4>Tasks</h4>
          <p>View and manage posted tasks.</p>
        </a>
        <a
          href="/Jakarta-Submit-1.0-SNAPSHOT/submission"
          class="nav-link feature-box text-dark text-decoration-none"
        >
          <h4>Submissions</h4>
          <p>Check your submission history.</p>
        </a>
        <a
          href="./public/Profile.jsp"
          class="feature-box text-dark text-decoration-none"
        >
          <h4>Profile</h4>
          <p>Update your personal information.</p>
        </a>
      </div>
    </div>
  </body>
</html>
