package in.pwskills.nitin.service;

import in.pwskills.nitin.dto.EmployeeDTO;
import in.pwskills.nitin.entity.Employee;
import in.pwskills.nitin.repository.IEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("empService")
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private IEmployeeRepository repo;

    // Helper method to convert Employee entity to EmployeeDTO
    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEid(employee.getEid());
        dto.setEname(employee.getEname());
        dto.setEage(employee.getEage());
        dto.setEaddress(employee.getEaddress());
        return dto;
    }

    // Helper method to convert EmployeeDTO to Employee entity (for saving)
    private Employee convertToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        if (employeeDTO.getEid() != null) { // Check if ID is present
            employee.setEid(employeeDTO.getEid()); // Set ID if it exists (for updates)
        }
        employee.setEname(employeeDTO.getEname());
        employee.setEage(employeeDTO.getEage());
        employee.setEaddress(employeeDTO.getEaddress());
        return employee;
    }

    @Override
    public List<EmployeeDTO> listAllEmps() {
        List<Employee> employees = repo.findAll();
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeebyId(Integer id) {
        Optional<Employee> optional = repo.findById(id);
        if (optional.isPresent()) {
            return convertToDTO(optional.get());
        }
        return null; // Or throw an exception, depending on your error handling policy
    }

    @Override
    public void saveEmployee(EmployeeDTO empDTO) {
        Employee employee = convertToEntity(empDTO);
        repo.save(employee);
    }

    @Override
    public void deleteEmployee(Integer id) {
        repo.deleteById(id);
    }
}