package in.pwskills.nitin.dao;

import org.springframework.data.repository.CrudRepository;

import in.pwskills.nitin.model.Book;

public interface IBookRepository extends CrudRepository<Book, Integer> {

}
