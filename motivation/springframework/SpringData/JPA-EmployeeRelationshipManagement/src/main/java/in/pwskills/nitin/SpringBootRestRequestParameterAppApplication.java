package in.pwskills.nitin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootRestRequestParameterAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestRequestParameterAppApplication.class, args);
	}

}

// 1. Get all employees
// request "GET" localhost:9999/employees/showData
// response array of JSON object containing list of all employees
// 2. Get specific employee using "Path variable"
// request "GET" localhost:9999/employees/showData/2
// response details of Employee with id as 2
// If "id" doesnot exists, "404 Not Found" is shown as response 
// 3. Save employee
// request "POST" localhost:9999/employees/save
// with request body as JSON payload
// {
// 	"ename" : "Sachin",
// 	"eage" : 50,
// 	"eaddress" : "IND"
// }
// response record created in database and response status as "201 created"
// 4. Updating the record
// request "PUT" localhost:9999/employees/update/10
// with request body as JSON payload
// {
//  "eid" : 10
// 	"ename" : "Tendulkar",
// 	"eage" : 50,
// 	"eaddress" : "IND"
// }
// response record created in database and response status as "200 OK"
// If "id" doesnot exists, "404 Not Found" is shown as response 
// 5. Deleting the record
// request "DELETE" localhost:9999/employees/delete/10
// If "id" exists, response status as "200 OK"
// If "id" doesnot exists, response status as "500 Internal Server Error"