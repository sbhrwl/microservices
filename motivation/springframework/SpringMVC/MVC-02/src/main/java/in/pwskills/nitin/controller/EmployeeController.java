package in.pwskills.nitin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/emp")
public class EmployeeController {

	static {
		System.out.println("Emp.class file is loading...");
	}

	public EmployeeController() {
		System.out.println("EMP OBJECT IS INSTANTIATED...");
	}

	@GetMapping("/show")
	public String showPages(Model model) {
		model.addAttribute("msg", "Welcome to PWSKILLS :: " + new java.util.Date());
		return "home";
	}

}
