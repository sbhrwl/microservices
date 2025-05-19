package in.pwskills.nitin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Customer {
	private Integer cid;
	private String cname;

	private List<Company> compDetails;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dob;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime purchaseDate;

	private List<Map<String, Long>> familyDetails;

	public Customer() {
		System.out.println("Customer.Customer():: ZERO PARAM CONSTRUCTOR...");
	}

	public Integer getCid() {
		System.out.println("Customer.getCid()");
		return cid;
	}

	public void setCid(Integer cid) {
		System.out.println("Customer.setCid()");
		this.cid = cid;
	}

	public String getCname() {
		System.out.println("Customer.getCname()");
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
		System.out.println("Customer.setCname()");
	}

	public List<Company> getCompDetails() {
		System.out.println("Customer.getCompDetails()");
		return compDetails;
	}

	public void setCompDetails(List<Company> compDetails) {
		this.compDetails = compDetails;
		System.out.println("Customer.setCompDetails()");
	}

	public LocalDate getDob() {
		System.out.println("Customer.getDob()");
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
		System.out.println("Customer.setDob()");
	}

	public LocalDateTime getPurchaseDate() {
		System.out.println("Customer.getPurchaseDate()");
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
		System.out.println("Customer.setPurchaseDate()");
	}

	public List<Map<String, Long>> getFamilyDetails() {
		System.out.println("Customer.getFamilyDetails()");
		return familyDetails;
	}

	public void setFamilyDetails(List<Map<String, Long>> familyDetails) {
		this.familyDetails = familyDetails;
		System.out.println("Customer.setFamilyDetails()");
	}

	@Override
	public String toString() {
		return "Customer [cid=" + cid + ", cname=" + cname + ", compDetails=" + compDetails + ", dob=" + dob
				+ ", purchaseDate=" + purchaseDate + ", familyDetails=" + familyDetails + "]";
	}

}
