package in.pwskills.nitin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "EMPLOYEE")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eid;

	@Column(name = "ename")
	private String ename;

	@Column(name = "eage")
	private Integer eage;

	@Column(name = "eaddress")
	private String eaddress;

	public Employee() {
		System.out.println("EMPLOYEE :: ZERO PARAM CONSTRUCTOR...");
	}

	public Integer getEid() {
		System.out.println("Employee.getEid()");
		return eid;
	}

	public void setEid(Integer eid) {
		this.eid = eid;
		System.out.println("Employee.setEid()");
	}

	public String getEname() {
		System.out.println("Employee.getEname()");
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
		System.out.println("Employee.setEname()");
	}

	public Integer getEage() {
		System.out.println("Employee.getEage()");
		return eage;
	}

	public void setEage(Integer eage) {
		System.out.println("Employee.setEage()");
		this.eage = eage;
	}

	public String getEaddress() {
		System.out.println("Employee.getEaddress()");
		return eaddress;
	}

	public void setEaddress(String eaddress) {
		this.eaddress = eaddress;
		System.out.println("Employee.setEaddress()");
	}

	@Override
	public String toString() {
		return "Employee [eid=" + eid + ", ename=" + ename + ", eage=" + eage + ", eaddress=" + eaddress + "]";
	}

}
