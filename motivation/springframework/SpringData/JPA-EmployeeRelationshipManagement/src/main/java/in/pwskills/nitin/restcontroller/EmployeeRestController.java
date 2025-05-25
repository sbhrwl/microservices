package in.pwskills.nitin.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.pwskills.nitin.entity.Employee;
import in.pwskills.nitin.service.IEmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeRestController {

	@Autowired
	private IEmployeeService service;

	// RESTFUL API For Retrieval Operation
	@GetMapping("/all")
	public List<Employee> empList() {
		return service.listAllEmps();
	}

	// RESTFUL API FOR READING ONE RECORD
	@GetMapping("/find/{id}")
	public ResponseEntity<Employee> getRecord(@PathVariable Integer id) {
		try {
			Employee employee = service.getEmployeebyId(id);
			return new ResponseEntity<Employee>(employee, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<Employee>(HttpStatus.NOT_FOUND);
		}
	}

	// RESTFUL API FOR SAVING THE ENTITY
	@PostMapping("/save")
	public ResponseEntity<Employee> addRecord(@RequestBody Employee employee) {
		service.saveEmployee(employee);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}


	// RESTFUL API FOR DELETING A RECORD
	@DeleteMapping("/delete/{id}")
	public void deleteRecord(@PathVariable Integer id) {
		service.deleteEmployee(id);
	}

	// RESTFUL API FOR UPDATING THE RECORD
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateRecord(@RequestBody Employee employee, @PathVariable("id") Integer eid) {
		try {
			Employee existingEmployee = service.getEmployeebyId(eid);
			System.out.println(existingEmployee);

			service.saveEmployee(employee);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
