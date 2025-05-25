package in.pwskills.nitin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

	static {
		System.out.println("UserController.class file is loading...");
	}

	public UserController() {
		System.out.println("UserController object is instantiated...");
	}

	@PostMapping("/login")
	public String validateCredentials(
			@RequestParam("username") String username,
			@RequestParam("userpassword") String password, 
			Model model) {
		
		System.out.println("UserController.validateCredentials()");
		System.out.println(username + " :: " + password);

		if (password.equals("admin")) {
			String msg = "Hello : " + username;
			model.addAttribute("message", msg);
			return "viewpage";

		} else {
			String msg = "Sorry " + username + " You hav entered incorrect password";
			model.addAttribute("message", msg);
			return "errorpage";
		}
	}
}
