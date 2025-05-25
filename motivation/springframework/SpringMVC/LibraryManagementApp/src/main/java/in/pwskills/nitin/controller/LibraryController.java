package in.pwskills.nitin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.pwskills.nitin.model.Book;
import in.pwskills.nitin.service.IBookService;

@Controller
public class LibraryController {

	@Autowired
	private IBookService service;
	
	@GetMapping("/")
	public String showPage() {
		return "redirect:new-book";
	}

	@GetMapping("/new-book")
	public String showBookCreationForm(Model model) {
		model.addAttribute("book", new Book());
		return "new-book";
	}

	@PostMapping("/add")
	public String addNewBook(@ModelAttribute Book book, Model model) {
		System.out.println(book);
		service.saveBook(book);
		model.addAttribute("books", service.findAllBooks());
		return "books";
	}

}
