<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GMAIL LOGIN</title>
</head>
<body>
	<h1 style='color:red;text-align: center;'>WELCOME TO PWSKILLS...</h1>
	<form method='post' action='./user/login'>
		<table align="center">
			<tr>
				<th>USERNAME</th>
				<td>
					<input type='text' name='username'/>
				</td>
			</tr>
			<tr>
				<th>PASSWORD</th>
				<td>
					<input type='password' name='userpassword'/>
				</td>
			</tr>
			<tr>
				<th></th>
				<td>
					<input type='submit' value='login'/>
				</td>
			</tr>
			
			
		</table>
	</form>
</body>
</html>