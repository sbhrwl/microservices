package in.pwskills.nitin.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
public class Customer {

    @Id
    private String id;
    private String cname; // Customer name
    private String caddr; // Customer address
    private double billAmount; // Bill amount
    private String mobileNo; // Mobile number

    // Default constructor
    public Customer() {
    }

    // Parameterized constructor
    public Customer(String id, String cname, String caddr, double billAmount, String mobileNo) {
        this.id = id;
        this.cname = cname;
        this.caddr = caddr;
        this.billAmount = billAmount;
        this.mobileNo = mobileNo;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCaddr() {
        return caddr;
    }

    public void setCaddr(String caddr) {
        this.caddr = caddr;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", cname='" + cname + '\'' +
                ", caddr='" + caddr + '\'' +
                ", billAmount=" + billAmount +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }
}