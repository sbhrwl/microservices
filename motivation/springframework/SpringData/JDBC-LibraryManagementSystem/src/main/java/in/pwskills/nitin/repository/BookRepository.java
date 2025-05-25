package in.pwskills.nitin.repository;

import java.util.List;
import java.util.Optional;

import in.pwskills.nitin.entity.Book;

public interface BookRepository {
	int save(Book book);

	int update(Book book);

	int deleteById(Integer id);
	
	Optional<Book> findById(Integer id);

	List<Book> findAll();
	
	int count();
	
	List<Book> findByNameAndPrice(String name,Integer price);
}
