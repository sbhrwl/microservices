import java.io.*;

class Student {
    private String name;
    private int age;
    private String city;

    public Student(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    // Override toString method
    public String toString() {
        return "Name : " + name + " | Age : " + age + " | City : " + city;
    }
}

public class OverridingToStringMethod {
    public static void main(String[] args) throws Exception {
        Student st1 = new Student("Virat", 34, "Delhi");
        System.out.println("Student object for Virat: " + st1);

        Student st2 = new Student("Sachin", 44, "Mumbai");
        System.out.println("Student object for Sachin: " + st2);
        // jvm shut down
    }
}
// Output
// Student object for Virat: Student@4617c264
// Student object for Sachin: Student@36baf30c
// Now un-comment toString and run again
// Student object for Virat: Name : Virat | Age : 34 | City : Delhi
// Student object for Sachin: Name : Sachin | Age : 44 | City : Mumbai