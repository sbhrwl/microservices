package in.pwskills.nitin.runner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import in.pwskills.nitin.document.Customer;
import in.pwskills.nitin.generator.IdGenerator;
import in.pwskills.nitin.service.ICustomerService;

@Component
public class MongoDbTestRunner implements CommandLineRunner {

	@Autowired
	private ICustomerService service;

	@Override
	public void run(String... args) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("WELCOME TO CUSTOMER RELATIONSHIP PROJECT");
			System.out.println("1. CREATE");
			System.out.println("2. READ");
			System.out.println("3. UPDATE");
			System.out.println("4. DELETE");
			System.out.println("5. EXIT");

			System.out.print("Enter ur choice :: ");
			String choice = reader.readLine();

			switch (choice) {
			case "1":
				insertDocument();
				break;

			case "2":
				selectDocument();
				break;

			case "3":
				updateDocument();
				break;

			case "4":
				deleteDocument();
				break;

			case "5":
				System.out.println("Thanks for using the application");
				System.exit(0);

			default:
				System.out.println("Invalid choice.. PLZ try again...");
				break;
			}
		}

	}

	@SuppressWarnings("unused")
	private void findCustomersByAddressWhoseMobileNoIsNotNull() {
		service.fetchCustomerByUsingCaddrAndHavingMobileNot("RCB", "CSK", "MI").forEach(System.out::println);
	}

	@SuppressWarnings("unused")
	private void findCustomersByBillRange() {
		service.fetchCustomersByBillAmountRange(2000.0, 10000.0).forEach(System.out::println);
	}

	private void deleteDocument() {
		String id = "e851df3c9a";
		System.out.println(service.removeCustomer(id));
	}

	private void updateDocument() {
		Customer customer = new Customer();
		String documentId = "e851df3c9a";
		customer.setCid(documentId);
		customer.setCno(9);
		customer.setCname("lara");
		customer.setCaddr("WI");
		customer.setBillAmount(4500.0);
		customer.setMobileNo(5556667778L);
		System.out.println(service.registerCustomer(customer));
	}

	private void selectDocument() {
		service.findAllCustomers().forEach(System.out::println);
	}

	private void insertDocument() {
		Customer customer1 = new Customer();
		customer1.setCid(IdGenerator.generateId());
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter the customer id :: ");
		customer1.setCno(scanner.nextInt());
		System.out.print("Enter the customer name :: ");
		customer1.setCname(scanner.next());
		System.out.print("Enter the customer address :: ");
		customer1.setCaddr(scanner.next());
		System.out.print("Enter the customer billAmount:: ");
		customer1.setBillAmount(scanner.nextDouble());
		System.out.print("Enter the customer mobileNo :: ");
		customer1.setMobileNo(scanner.nextLong());
		
		System.out.println(service.registerCustomer(customer1));
	}

}
