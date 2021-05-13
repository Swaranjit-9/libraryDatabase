package ca.sheridancollege.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;


@Repository
public class DatabaseAccess {

	
	@Autowired
	private NamedParameterJdbcTemplate jdbc;
	
	/**
	 * Adds a book to the database
	 * @param book
	 * @return
	 */
	public int addBook(Book book) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "INSERT INTO books (title,author)"
				+ "VALUES (:title, :author)";
		namedParameters
		.addValue("title",book.getTitle())
		.addValue("author", book.getAuthor());
		int returnValue = jdbc.update(query, namedParameters);
		return returnValue;
	}
	
	
	
	/**
	 * Returns a list of all the books
	 * @return list of books
	 */
	public List<Book> getBooks() {
		String query = "SELECT * from books";
		BeanPropertyRowMapper<Book> bookMapper = 
				new BeanPropertyRowMapper<Book>(Book.class);
		List<Book> books = jdbc.query(query, bookMapper);
		return books;
	}
	
	
	
	/**
	 * Returns all the reviews for the selected book
	 * @param id
	 * @return list of reviews
	 */
	public List<Review> getReviews(Long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();	
		String query = "SELECT text from reviews WHERE bookId = :id";
		namedParameters.addValue("id", id);
		BeanPropertyRowMapper<Review> bookMapper = 
				new BeanPropertyRowMapper<Review>(Review.class);
		List<Review> reviews = jdbc.query(query, namedParameters, bookMapper);
		return reviews;
	}
	
	
	
	/**
	 * Returns the book based on id
	 * @param id
	 * @return book
	 */
	public Book getBook(Long id) {
       MapSqlParameterSource namedParameters = new MapSqlParameterSource();	
		String query = "SELECT * from books WHERE id = :id";
		namedParameters.addValue("id", id);	
		BeanPropertyRowMapper<Book> bookMapper = 
				new BeanPropertyRowMapper<Book>(Book.class);
		Book book = jdbc.query(query, namedParameters, bookMapper).get(0);
		return book;
	}
	
	
	
	/**
	 * Adds a review to the database
	 * @param review
	 * @return 
	 */
	public int addReview(Review review) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();		
		String query = "INSERT INTO reviews (text,bookId)"
				+ "VALUES (:text, :bookId)";
		namedParameters
		.addValue("text",review.getText())
		.addValue("bookId", review.getBookId());		
		int returnValue = jdbc.update(query, namedParameters);
		return returnValue;
	}
	
	
	
	/**
	 * Checks for a unique username
	 * @param userName
	 * @return boolen
	 */
	public boolean uniqueUser(String userName) {
		boolean isTaken= false;		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT DISTINCT username FROM users";
		List<String> userList = jdbc.queryForList(query, namedParameters, String.class);		
		for(String n : userList) {
			if(n.equals(userName)) {
				isTaken = true;
				break;
			}
		}
		return isTaken;
	}
	
	
	
	/**
	 * Returns authority level 'USER'
	 * @return user authority
	 */
	public String getUserAuthority(){
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();		
		String query = "SELECT DISTINCT authority FROM authorities "
				+ "WHERE authority = 'ROLE_USER'";		
		String userAuth = jdbc.queryForList(query, namedParameters, String.class).get(0);		
		return userAuth;
	}
	
	
	/**
	 * Checks for a unique review
	 * @param review
	 * @return boolean
	 */
	public boolean uniqueReview(String review) {
		boolean isTaken= false;		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String query = "SELECT text FROM reviews";
		List<String> reviewList = jdbc.queryForList(query, namedParameters, String.class);		
		for(String n : reviewList) {
			if(n.equals(review)) {
				isTaken = true;
				break;
			}
		}
		return isTaken;
	}
	

	
}
