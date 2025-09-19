<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>home</title>
</head>
<body>
    <h1>Add Some numbers</h1>
    <form method="get" action="${pageContext.request.contextPath}/calculate">
        <input type="hidden" name="action" value="add">

        <input type="number" name="num1">
        <input type="number" name="num2">
        <input type="submit" value="submit">
        <input type="reset" value="clear">
    </form>

    <div>
        <c:if test="${not empty sum}"><span>Result is : ${sum}</span></c:if>
    </div>
</body>
</html>
