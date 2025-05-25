package in.pwskills.nitin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.pwskills.nitin.entity.Employee;
import in.pwskills.nitin.repository.IEmployeeRepository;

@Service("empService")
public class EmployeeServiceImpl implements IEmployeeService {

	@Autowired
	private IEmployeeRepository repo;

	@Override
	public List<Employee> listAllEmps() {
		return repo.findAll();
	}

	@Override
	public Employee getEmployeebyId(Integer id) {
		Optional<Employee> optional = repo.findById(id);
		return optional.get();
	}

	@Override
	public void saveEmployee(Employee emp) {
		repo.save(emp);
	}

	@Override
	public void deleteEmployee(Integer id) {
		repo.deleteById(id);
	}

}
