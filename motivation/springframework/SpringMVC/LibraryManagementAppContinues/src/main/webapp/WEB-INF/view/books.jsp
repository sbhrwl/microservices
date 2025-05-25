<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
</head>
<body>
	<div class="col-5">
		<div class="container">
			<div class="card">
				<div class="card-header bg-info text-center text-white">
					<h3>List Of Library Books</h3>
				</div>
				<div class="card-body">
					<table class="table table-hover" border='1'>
						<tr class="bg-dark text-white">
							<th>ID</th>
							<th>Author</th>
							<th>NAME</th>
							<th>Edit/Delete</th>

						</tr>
						<c:forEach var="book" items="${books }">
							<tr>
								<td>${book.id }</td>
								<td>${book.author }</td>
								<td>${book.name }</td>
								<td><a href='/${book.id}' class="btn btn-warning"> EDIT</a>
									<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
									<form action='/${book.id }/delete' method='post'>
										<input type='submit' value='DELETE' class="btn btn-danger" />
									</form></td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</div>
	</div>
</body>
</html>