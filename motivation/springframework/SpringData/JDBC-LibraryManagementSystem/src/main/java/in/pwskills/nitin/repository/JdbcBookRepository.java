package in.pwskills.nitin.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import in.pwskills.nitin.entity.Book;

@Repository
public class JdbcBookRepository implements BookRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int save(Book book) {
		int rowAffected = jdbcTemplate.update("insert into books(name,cost)values(?,?)", book.getName(),
				book.getCost());
		return rowAffected;
	}

	@Override
	public int update(Book book) {
		int rowAffected = jdbcTemplate.update("update books set cost=? where id=?", book.getCost(), book.getId());
		return rowAffected;
	}

	@Override
	public int deleteById(Integer id) {
		return jdbcTemplate.update("delete from books where id = ?", id);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Optional<Book> findById(Integer id) {
		return jdbcTemplate.queryForObject("select id,name,cost from books where id = ?", new Object[] { id },

				(rs, rowNum) -> Optional.of(new Book(rs.getInt(1), rs.getString(2), rs.getInt(3))));

	}

	@Override
	public List<Book> findAll() {

		return jdbcTemplate.query("select * from books",

				new RowMapper<Book>() {

					@Override
					public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
						System.out.println(rowNum);
						return new Book(rs.getInt(1), rs.getString(2), rs.getInt(3));
					}
				}

		);
	}

	@Override
	public int count() {
		return jdbcTemplate.queryForObject("select count(*) from books", Integer.class);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Book> findByNameAndPrice(String name, Integer price) {

		return jdbcTemplate.query("select * from books where name like ? and cost<=?", 
				new Object[] {"%"+name+"%",price}, (rs, rowNum) -> new Book(rs.getInt(1), rs.getString(2), rs.getInt(3)));

	}

}
