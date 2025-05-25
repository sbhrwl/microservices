<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Registration Page</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
	integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
	crossorigin="anonymous">
</head>
<body>
	<h1>EMPLOYEE DATA</h1>
	<br>
	<table class="table table-striped">
		<thead>
			<tr>
				<th scope="col">EMPID</th>
				<th scope="col">EMPNAME</th>
				<th scope="col">EMPSAL</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>${employee.empId }</td>
				<td>${employee.empName }</td>
				<td>${employee.empSal }</td>
			</tr>
		</tbody>
	</table>
</body>
</html>