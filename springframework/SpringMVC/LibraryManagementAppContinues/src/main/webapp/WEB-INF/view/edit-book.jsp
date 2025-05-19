<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

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
		<form:form method = "post" modelAttribute="book" action="${book.id}/update">
			<div>
				Id: ${book.id }
			</div>	
			<div>
				<form:label path="author">AUTHOR</form:label>
				<form:input path="author" type="text"/>
			</div>
			<div>
				<form:label path="name">NAME</form:label>
				<form:input path="name" type="text"/>
			</div>
			<div>
				<div>
					<input type='submit' value='Update User'>
				</div>
			</div>
		</form:form>
	</div>
</body>
</html>