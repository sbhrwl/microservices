package in.pwskills.nitin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.pwskills.nitin.entity.Employee;

@Controller
@RequestMapping("/emp")
public class EmployeeController {

	@GetMapping("/show")
	public String showMsg(Model model) {

		Employee employee = new Employee();
		employee.setEmpId(10);
		employee.setEmpName("sachin");
		employee.setEmpSal(3454.0);

		model.addAttribute("emp", employee);

		return "empData";

	}
}
