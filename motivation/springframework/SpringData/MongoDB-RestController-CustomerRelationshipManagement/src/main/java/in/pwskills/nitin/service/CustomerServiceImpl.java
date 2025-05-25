package in.pwskills.nitin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.pwskills.nitin.document.Customer;
import in.pwskills.nitin.repository.ICustomerRepo;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private ICustomerRepo repo;

    @Override
    public Customer registerCustomer(Customer customer) {
        // Performs both insert/update operation
        return repo.save(customer);
    }

    @Override
    public List<Customer> findAllCustomers() {
        return repo.findAll();
    }

    @Override
    public Customer findCustomerById(String id) {
        Optional<Customer> optional = repo.findById(id);
        return optional.orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
    }

    @Override
    public Customer updateCustomer(String id, Customer customer) {
        Customer existingCustomer = findCustomerById(id);
        existingCustomer.setCname(customer.getCname());
        existingCustomer.setCaddr(customer.getCaddr());
        existingCustomer.setBillAmount(customer.getBillAmount());
        existingCustomer.setMobileNo(customer.getMobileNo());
        return repo.save(existingCustomer);
    }

    @Override
    public String removeCustomer(String id) {
        Customer existingCustomer = findCustomerById(id);
        repo.delete(existingCustomer);
        return "Customer with ID " + id + " has been deleted.";
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