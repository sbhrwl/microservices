package in.pwskills.nitin.entity;

public class Address {
	private String country;
	private String state;
	private String city;

	public Address(String country, String state, String city) {
		super();
		this.country = country;
		this.state = state;
		this.city = city;
	}

	public String getCountry() {
		System.out.println("Address.getCountry()");
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
		System.out.println("Address.setCountry()");
	}

	public String getState() {
		System.out.println("Address.getState()");
		return state;
	}

	public void setState(String state) {
		this.state = state;
		System.out.println("Address.setState()");
	}

	public String getCity() {
		System.out.println("Address.getCity()");
		return city;
	}

	public void setCity(String city) {
		this.city = city;
		System.out.println("Address.setCity()");
	}

	@Override
	public String toString() {
		return "Address [country=" + country + ", state=" + state + ", city=" + city + "]";
	}

}
