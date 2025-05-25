package in.pwskills.nitin.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.pwskills.nitin.document.Customer;

public interface ICustomerRepo extends MongoRepository<Customer, String> {

	// syntax: <ReturnType> findBy<properties><Condition>(parameters...)
	public List<Customer> findByBillAmountBetween(double start, double end);

	// syntax: <ReturnType> findBy<properties><Condition>(parameters...)
	public List<Customer> findByCaddrInAndMobileNoIsNotNull(String... address);

}
