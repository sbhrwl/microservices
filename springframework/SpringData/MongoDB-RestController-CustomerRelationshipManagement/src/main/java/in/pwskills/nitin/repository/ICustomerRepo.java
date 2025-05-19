package in.pwskills.nitin.repository;

import in.pwskills.nitin.document.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomerRepo extends MongoRepository<Customer, String> {

    // Custom query to find customers by bill amount range
    List<Customer> findByBillAmountBetween(double start, double end);

    // Custom query to find customers by address and mobile number not null
    List<Customer> findByCaddrInAndMobileNoIsNotNull(String... address);
}