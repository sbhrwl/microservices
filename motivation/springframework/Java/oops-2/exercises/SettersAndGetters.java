class Student {
	private int age;
	private String name;

	public void setAge(int age) {
		this.age = age;

	}

	public int getAge() {
		return age;
	}

	public void setName(String name) {
		this.name = "Navin";

	}

	public String getName() {
		return name;
	}

	public void show() {
		System.out.println(age + " " + name);
	}
}

public class SettersAndGetters {
	public static void main(String[] args) {
		Student obj = new Student();
		Student obj1 = new Student();
		obj.setAge(18);
		obj1.setAge(25);
		obj.setName("Navin");
		obj1.setName("kiran");
		int stud1Age = obj.getAge();
		String stud1Name = obj.getName();
		System.out.println("Age: " + stud1Age);
	}
}
// Output
// Age: 18