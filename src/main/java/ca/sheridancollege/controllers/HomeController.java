package ca.sheridancollege.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;
import ca.sheridancollege.database.DatabaseAccess;

/**
 * Application Controller
 * @author Simran Arora and Cory Bridgman
 *
 */
@Controller
public class HomeController{

	
	@Autowired
	@Lazy
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;
	
	@Autowired
	private DatabaseAccess database;
	
	public HomeController(DatabaseAccess database) {
		this.database = database;
	}

	/**
	 * Returns mapping to the login page 
	 * @return login
	 */
	@GetMapping({"/login","/logout"})
	public String login() {
		return "login";
	}
	
	
	/**
	 * Adds a book to the database 
	 * @param book 
	 * @param model
	 * @return index
	 */
	@PostMapping("/add-book")
	public String addbook(@ModelAttribute Book book, Model model){
		int returnValue = database.addBook(book);
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		model.addAttribute("register", 0);
		return "index";
	}
	
	
	
	/**
	 * Adds a book object to be defined by the admin
	 * @param model
	 * @return /admin/add-book
	 */
	@GetMapping("/admin/new-book")
	public String addbookpage(Model model) {
		model.addAttribute("book", new Book());
		return "/admin/add-book";
	}
	
	
	/**
	 * Takes the user to the root directory
	 * @param model
	 * @return index
	 */
	@GetMapping("/")
	public String index(Model model) {
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		model.addAttribute("register",0);	
		return "index";
	}
	
	
	/**
	 * Adds a review for the selected book
	 * @param id
	 * @param model 
	 * @return /user/add-review
	 */
	@GetMapping("/add-review-page/{id}")
	public String addReview(@PathVariable Long id, Model model) {
		model.addAttribute("review",new Review(null,id,null));
		return "/user/add-review";
	}
	

	/**
	 * Checks if review is unique. If yes, adds it to database.
	 * @param model
	 * @param bookId
	 * @param text
	 * @return view-book
	 */
	@PostMapping("/add-review")
	public String addReview(Model model, @RequestParam("bookId") Object bookId, 
							@RequestParam String text) { 
		Long id = Long.parseLong(bookId.toString());
		if(database.uniqueReview(text)) {				
			int taken = 1;
			model.addAttribute("review",new Review(null,id,null))
			.addAttribute("taken", taken);
			return "/user/add-review";
		}
		Review review = new Review(null, id, text); 
		int returnValue = database.addReview(review);
		List<Review> reviews = database.getReviews(review.getBookId());
		Book book = database.getBook(review.getBookId());
		model.addAttribute("reviews", reviews);
		model.addAttribute("book",book);
		return "view-book";
	}
	
	
	
	/**
	 * Shows the reviews for the selected book
	 * @param id
	 * @param model
	 * @return view-book
	 */
	@GetMapping("/view-book/{id}")
	public String viewReviews(@PathVariable Long id, Model model) {
		List<Review> reviews = database.getReviews(id);
		Book book = database.getBook(id);
		model.addAttribute("reviews", reviews);
		model.addAttribute("book",book);
		return "view-book";
	}
	
	
	
	/**
	 * Go to the register user page
	 * @param model
	 * @return register
	 */
	@GetMapping("/register")
	public String goRegister(Model model) {
		int taken = 0;
		model.addAttribute("taken", taken);
		return "register";
	}

	
	
	/**
	 * Checks for a unique username. If yes, adds the user to database
	 * @param model
	 * @param username
	 * @param password
	 * @return index
	 */
	@PostMapping("/register-user")
	public String addUser(Model model, @RequestParam("userName") String username, 
			@RequestParam("password") String password) {
		if(database.uniqueUser(username)) {
			int taken = 1;
			model.addAttribute("taken", taken);
			return "register";
		}	
		List<GrantedAuthority> userAuth = new ArrayList<>();
		userAuth.add(new SimpleGrantedAuthority(database.getUserAuthority()));	
		String encodedPassword = passwordEncoder.encode(password);	
		User user = new User(username, encodedPassword, userAuth);
		jdbcUserDetailsManager.createUser(user);
		model.addAttribute("register",1);
		List<Book> books = database.getBooks();
		model.addAttribute("books", books);
		return "index";
	}
	
	
	

	/**
	 * Rejects user with insufficient authorization
	 * @return /error/permission-denied
	 */
	@GetMapping("/permission-denied")
	public String permissionDenied() {
		return "/error/permission-denied";
	}
	
	
}
