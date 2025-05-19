package in.pwskills.nitin.service;

import java.util.List;

import in.pwskills.nitin.model.Book;

public interface IBookService {

	public List<Book> findAllBooks();

	public Book saveBook(Book book);

	public void deleteBookById(Integer id);

	public void findBookById(Integer id);

}
