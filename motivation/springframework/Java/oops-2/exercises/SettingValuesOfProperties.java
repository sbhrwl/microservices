// //Default values
// class Student {
// 	int age;
// 	String name;

// 	public void show() {
// 		System.out.println(age + " " + name);
// 	}
// }

// public class Demo1 {
// 	public static void main(String[] args) {
// 		Student obj = new Student();
// 		obj.show();
// 	}
// } // OUTPUT: 0 null

// // Initialise values
// class Student {
// 	int age;
// 	String name;

// 	public void show() {
// 		System.out.println(age + " " + name);
// 	}
// }

// public class Demo2 {
// 	public static void main(String[] args) {
// 		Student obj = new Student();
// 		obj.age = 18;
// 		obj.name = "John";
// 		obj.show();
// 	}
// } // OUTPUT: 18 John

class Student {
	private int age;
	private String name;

	public void setData() {
		age = 18;
		name = "Navin";
	}

	public void show() {
		System.out.println(age + " " + name);
	}
}

public class SettingValuesOfProperties {
	public static void main(String[] args) {
		Student obj = new Student();
		obj.setData();
		obj.show();
	}
}
// Output
// 18 Navin