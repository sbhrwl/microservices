package in.pwskills.nitin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.pwskills.nitin.document.Customer;
import in.pwskills.nitin.repository.ICustomerRepo;

@Service("customerService")
public class CustomerServiceImpl implements ICustomerService {

	@Autowired
	private ICustomerRepo repo;

	@Override
	public String registerCustomer(Customer customer) {
		// performs both insert/update operation
		return "Customer is saved with the id :: " + repo.save(customer).getCid();
	}

	@Override
	public List<Customer> findAllCustomers() {
		List<Customer> customers = repo.findAll();
		return customers;
	}

	@Override
	public String removeCustomer(String id) {

		Optional<Customer> optional = repo.findById(id);
		if (optional.isPresent()) {
			repo.delete(optional.get());
			return "Document is deleted";
		}

		return "Document not found";
	}

	@Override
	public List<Customer> fetchCustomersByBillAmountRange(double start, double end) {
		return repo.findByBillAmountBetween(start, end);
	}

	@Override
	public List<Customer> fetchCustomerByUsingCaddrAndHavingMobileNot(String... address) {
		return repo.findByCaddrInAndMobileNoIsNotNull(address);
	}
}
