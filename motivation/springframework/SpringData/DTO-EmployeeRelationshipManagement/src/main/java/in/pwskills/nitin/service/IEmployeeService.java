package in.pwskills.nitin.service;

import in.pwskills.nitin.dto.EmployeeDTO;

import java.util.List;

public interface IEmployeeService {
    List<EmployeeDTO> listAllEmps();  // Return DTOs
    EmployeeDTO getEmployeebyId(Integer id); // Return a DTO
    void saveEmployee(EmployeeDTO empDTO);  // Accept a DTO
    void deleteEmployee(Integer id);
}