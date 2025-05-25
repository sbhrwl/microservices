package in.pwskills.nitin.entity;

public class Company {
	private String name;
	private String location;
	private Integer size;

	public String getName() {
		System.out.println("Company.getName()");
		return name;
	}

	public void setName(String name) {
		this.name = name;
		System.out.println("Company.setName()");
	}

	public String getLocation() {
		System.out.println("Company.getLocation()");
		return location;
	}

	public void setLocation(String location) {
		System.out.println("Company.setLocation()");
		this.location = location;
	}

	public Integer getSize() {
		System.out.println("Company.getSize()");
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
		System.out.println("Company.setSize()");
	}

	@Override
	public String toString() {
		return "Company [name=" + name + ", location=" + location + ", size=" + size + "]";
	}

}
