package in.pwskills.nitin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.pwskills.nitin.entity.Employee;

public interface IEmployeeRepository extends JpaRepository<Employee, Integer>{
	
}
