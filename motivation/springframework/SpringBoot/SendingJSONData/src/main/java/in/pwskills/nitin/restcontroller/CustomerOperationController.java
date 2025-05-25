package in.pwskills.nitin.restcontroller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.pwskills.nitin.entity.Customer;

@RestController
@RequestMapping("/customer")
public class CustomerOperationController {

	@PostMapping("/register")
	public String registerCustomer(@RequestBody Customer customer) {
		return customer.toString();
	}
}
