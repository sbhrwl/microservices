<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Book Library</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" />
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" />
</head>
<body>
	<div class="card">
		<div class="card-header bg-info text-center text-white">
			<h3>NEW USER</h3>
		</div>
		<div class="card-body">
			<form:form action="/add" modelAttribute="book" method="post">
				
				<div class="row">
					<div class="col-2">
						<form:label path="author" for="author">
							<b>AUTHOR</b>
						</form:label>
					</div>
					<div class="col-4">
						<form:input path="author" class="form-control" />
					</div>
				</div>
				
				<div class="row">
					<div class="col-2">
						<form:label path= "name" for="name">
							<b>NAME</b>
						</form:label>
					</div>
					<div class="col-4">
						<form:input path="name"  class="form-control" />
					</div>
				</div>
				<br />
				<button type="submit" value='ADD USERS' class="btn btn-success">
					ADD USERS <i class="fa fa-plus-square" aria-hidden="true"></i>
				</button>
			</form:form>
		</div>
	</div>
</body>
</html>