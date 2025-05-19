package in.pwskills.nitin.service;

import java.util.List;

import in.pwskills.nitin.document.Customer;

public interface ICustomerService {

    // Register a new customer (insert or update)
    public Customer registerCustomer(Customer customer);

    // Retrieve all customers
    public List<Customer> findAllCustomers();

    // Retrieve a customer by ID
    public Customer findCustomerById(String id);

    // Update an existing customer
    public Customer updateCustomer(String id, Customer customer);

    // Remove a customer by ID
    public String removeCustomer(String id);

    // Fetch customers by bill amount range
    public List<Customer> fetchCustomersByBillAmountRange(double start, double end);

    // Fetch customers by address and having a non-null mobile number
    public List<Customer> fetchCustomerByUsingCaddrAndHavingMobileNot(String... address);
}