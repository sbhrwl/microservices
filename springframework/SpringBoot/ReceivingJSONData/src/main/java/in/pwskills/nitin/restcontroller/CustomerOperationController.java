package in.pwskills.nitin.restcontroller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.pwskills.nitin.entity.Address;
import in.pwskills.nitin.entity.Customer;

@RestController
@RequestMapping("/customer")
public class CustomerOperationController {

	@GetMapping("/showReport")
	public ResponseEntity<Customer> showReport() {

		Customer body = new Customer(10, "sachin", 2345.5f, new String[] { "blue", "black", "purple" },
				List.of("10", "10+2", "B.E"), Set.of(5454545L, 9998887776L, 23456728L),
				Map.of("adhar", 234567, "pan", 23456), new Address("INDIA", "Maharashtra", "Bombay"));

		HttpStatus status = HttpStatus.OK;
		ResponseEntity<Customer> entity = new ResponseEntity<>(body, status);
		return entity;
	}
}
