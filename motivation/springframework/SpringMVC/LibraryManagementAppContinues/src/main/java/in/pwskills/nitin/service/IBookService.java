package in.pwskills.nitin.service;

import java.util.List;
import java.util.Optional;

import in.pwskills.nitin.model.Book;

public interface IBookService {

	public List<Book> findAllBooks();

	public Book saveBook(Book book);

	public void deleteBookById(Integer id);

	public Optional<Book> findBookById(Integer id);

}
