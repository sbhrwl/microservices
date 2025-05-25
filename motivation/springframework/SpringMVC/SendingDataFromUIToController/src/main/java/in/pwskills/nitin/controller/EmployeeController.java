package in.pwskills.nitin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.pwskills.nitin.entity.Employee;

@Controller
@RequestMapping("/emp")
public class EmployeeController {

	@GetMapping("/")
	public String homePage() {
		return "register";
	}

	@PostMapping("/reg")
	public String register(@ModelAttribute Employee employee, Model model) {
		
		System.out.println("EmployeeController.register()");
		System.out.println(employee);
		model.addAttribute("employee", employee);
		return "emp";
	}

}
