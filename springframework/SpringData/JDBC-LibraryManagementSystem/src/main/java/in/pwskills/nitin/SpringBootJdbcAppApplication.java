package in.pwskills.nitin;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import in.pwskills.nitin.entity.Book;
import in.pwskills.nitin.repository.NamedParameterJdbcBookRepository;

@SpringBootApplication
public class SpringBootJdbcAppApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringBootJdbcAppApplication.class, args);

		NamedParameterJdbcBookRepository repository = context.getBean(NamedParameterJdbcBookRepository.class);

		Optional<Book> optional = repository.findById(2);
		if (optional.isPresent()) {
			System.out.println(optional.get());
		} else {
			System.out.println("No record found...");
		}

	}
}
