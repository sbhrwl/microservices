<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Registration Page</title>
</head>
<body>
	<h1>EMPLOYEE REGISTRATION PAGE</h1>
	<br>
	<form:form method="POST" action="/emp/reg" modelAttribute="employee">
      <table>
          <tr>
              <td>EMPLOYEE ID:</td>
              <td><form:input path="empId" /></td>
          </tr>
          <tr>
              <td>EMPLOYEE NAME:</td>
              <td><form:input path="empName" /></td>
          </tr>
          <tr>
              <td>EMPLOYEE SALARY:</td>
              <td><form:input path="empSal" /></td>
          </tr>
          <tr>
              <td colspan="2">
                  <input type="submit" value="register" />
              </td>
          </tr>
      </table>
  </form:form>
</body>
</html>