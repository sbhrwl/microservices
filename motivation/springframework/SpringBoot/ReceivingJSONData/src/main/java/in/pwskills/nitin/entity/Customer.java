package in.pwskills.nitin.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Customer {
	private Integer cid;
	private String cname;
	private Float billAmount;

	private String[] favColours;
	private List<String> studies;
	private Set<Long> phoneNumber;
	private Map<String, Object> idDetails;

	private Address address;

	public Integer getCid() {
		System.out.println("Customer.getCid()");
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
		System.out.println("Customer.setCid()");
	}

	public String getCname() {
		System.out.println("Customer.getCname()");
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
		System.out.println("Customer.setCname()");
	}

	public Float getBillAmount() {
		System.out.println("Customer.getBillAmount()");
		return billAmount;
	}

	public void setBillAmount(Float billAmount) {
		this.billAmount = billAmount;
		System.out.println("Customer.setBillAmount()");
	}

	public String[] getFavColours() {
		System.out.println("Customer.getFavColours()");
		return favColours;
	}

	public void setFavColours(String[] favColours) {
		this.favColours = favColours;
		System.out.println("Customer.setFavColours()");
	}

	public List<String> getStudies() {
		System.out.println("Customer.getStudies()");
		return studies;
	}

	public void setStudies(List<String> studies) {
		this.studies = studies;
		System.out.println("Customer.setStudies()");
	}

	public Set<Long> getPhoneNumber() {
		System.out.println("Customer.getPhoneNumber()");
		return phoneNumber;
	}

	public void setPhoneNumber(Set<Long> phoneNumber) {
		this.phoneNumber = phoneNumber;
		System.out.println("Customer.setPhoneNumber()");
	}

	public Map<String, Object> getIdDetails() {
		System.out.println("Customer.getIdDetails()");
		return idDetails;
	}

	public void setIdDetails(Map<String, Object> idDetails) {
		this.idDetails = idDetails;
		System.out.println("Customer.setIdDetails()");
	}

	public Address getAddress() {
		System.out.println("Customer.getAddress()");
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
		System.out.println("Customer.setAddress()");
	}

	public Customer(Integer cid, String cname, Float billAmount, String[] favColours, List<String> studies,
			Set<Long> phoneNumber, Map<String, Object> idDetails, Address address) {
		super();
		this.cid = cid;
		this.cname = cname;
		this.billAmount = billAmount;
		this.favColours = favColours;
		this.studies = studies;
		this.phoneNumber = phoneNumber;
		this.idDetails = idDetails;
		this.address = address;
	}

	@Override
	public String toString() {
		return "Customer [cid=" + cid + ", cname=" + cname + ", billAmount=" + billAmount + ", favColours="
				+ Arrays.toString(favColours) + ", studies=" + studies + ", phoneNumber=" + phoneNumber + ", idDetails="
				+ idDetails + ", address=" + address + "]";
	}

}
