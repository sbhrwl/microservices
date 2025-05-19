package in.pwskills.nitin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

	static {
		System.out.println("Emp.class file is loading...");
	}
	
	public EmployeeController() {
		System.out.println("EMP OBJECT IS INSTANTIATED...");
	}
	
	@GetMapping("/show")
	public String showPages() {
		System.out.println("Hey u r executing me....");
		return "home";
	}

}

// jsp option not available in sts 
// vscode extension
