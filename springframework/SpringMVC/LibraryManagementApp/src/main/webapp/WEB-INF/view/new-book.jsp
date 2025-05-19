<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Book Library</title>
</head>
<body>
	<div>
		<h2> NEW USER</h2>
	</div>
	<div>
		<form:form action ="/add" modelAttribute="book" method="post">
			<div>
				<div>
					<form:label path="author">AUTHOR</form:label>
					<form:input path="author"/>
				</div>
				<div>
					<form:label path="name">NAME</form:label>
					<form:input path="name"/>
				</div>
				<div>
					<input type='submit' value='ADD USERS'>
				</div>
			</div>
		
		</form:form>
	
	</div>
</body>
</html>