package in.pwskills.nitin.service;

import java.util.List;

import in.pwskills.nitin.document.Customer;

public interface ICustomerService {

	public String registerCustomer(Customer customer);

	public List<Customer> findAllCustomers();

	public String removeCustomer(String id);

	public List<Customer> fetchCustomersByBillAmountRange(double start, double end);

	public List<Customer> fetchCustomerByUsingCaddrAndHavingMobileNot(String... address);

}
