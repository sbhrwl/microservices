package in.pwskills.nitin.entity;

public class Customer {
	private Integer cid;
	private String cname;
	private Float billAmount;
	private String caddr;

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

	public Float getBillAmount() {
		System.out.println("Customer.getBillAmount()");
		return billAmount;
	}

	public void setBillAmount(Float billAmount) {
		this.billAmount = billAmount;
		System.out.println("Customer.setBillAmount()");
	}

	public String getCaddr() {
		System.out.println("Customer.getCaddr()");
		return caddr;
	}

	public void setCaddr(String caddr) {
		this.caddr = caddr;
		System.out.println("Customer.setCaddr()");
	}

	@Override
	public String toString() {
		return "Customer [cid=" + cid + ", cname=" + cname + ", billAmount=" + billAmount + ", caddr=" + caddr + "]";
	}

}
