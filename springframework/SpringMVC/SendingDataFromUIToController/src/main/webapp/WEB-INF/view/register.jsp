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
	<h1>EMPLOYEE REGISTRATION PAGE</h1>
	<br>
	<form method="post" action="/emp/reg">
	
		<div class="form-group">
			<label for="EMPID">EMPID</label> <input type="text"
				class="form-control" id="empId" name="empId"
				aria-describedby="emailHelp" placeholder="Enter Employee ID">
		</div>
		
		<div class="form-group">
			<label for="EMPNAME">EMPNAME</label> <input type="text"
				class="form-control" id="empId" name="empName"
				placeholder="Enter Employee NAME">
		</div>
		<div class="form-group">
			<label for="EMPSAL">EMPSAL</label> <input type="text"
				class="form-control" id="empSal" name="empSal"
				placeholder="Enter Employee SALARY">
		</div>
		

		<button type="submit" class="btn btn-primary">Submit</button>
	</form>
</body>
</html>