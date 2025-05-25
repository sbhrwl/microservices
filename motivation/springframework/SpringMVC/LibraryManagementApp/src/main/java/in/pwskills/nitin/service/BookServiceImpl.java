package in.pwskills.nitin.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.pwskills.nitin.dao.IBookRepository;
import in.pwskills.nitin.model.Book;

@Service
public class BookServiceImpl implements IBookService {

	@Autowired
	private IBookRepository repo;

	@Override
	public List<Book> findAllBooks() {
		return StreamSupport.stream(
				repo.findAll().spliterator(), false).collect(Collectors.toList());
	}

	@Override
	public Book saveBook(Book book) {
		return repo.save(book);
	}

	@Override
	public void deleteBookById(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void findBookById(Integer id) {
		// TODO Auto-generated method stub

	}

}
