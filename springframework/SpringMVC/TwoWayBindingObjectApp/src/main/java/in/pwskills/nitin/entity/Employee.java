package in.pwskills.nitin.entity;

public class Employee {

	private Integer empId;
	private String empName;
	private Double empSal;

	public Employee() {
		System.out.println("Employee Object is created...");
	}

	public Integer getEmpId() {
		System.out.println("Employee.getEmpId()");
		return empId;
	}

	public void setEmpId(Integer empId) {
		this.empId = empId;
		System.out.println("Employee.setEmpId()");
	}

	public String getEmpName() {
		System.out.println("Employee.getEmpName()");
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
		System.out.println("Employee.setEmpName()");
	}

	public Double getEmpSal() {
		System.out.println("Employee.getEmpSal()");
		return empSal;
	}

	public void setEmpSal(Double empSal) {
		this.empSal = empSal;
		System.out.println("Employee.setEmpSal()");
	}

	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", empName=" + empName + ", empSal=" + empSal + "]";
	}

}
