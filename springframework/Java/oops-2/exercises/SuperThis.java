class Student // extends Object
{
    private String name;
    private int age;

    public Student() {
        // super();
        this("Rohit", 19);
        System.out.println("Default Constructor is called");
        name = "Rohan";
        age = 18;
    }

    public Student(String name) {
        this();
        this.name = name;
        age = 19;
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void disp() {
        System.out.println(name);
        System.out.println(age);
    }
}

public class SuperThis {
    public static void main(String[] args) {
        Student st1 = new Student();
        st1.disp();

        Student st2 = new Student("Rahul");
        st2.disp();
    }
}
// Output
// Default Constructor is called
// Rohan
// 18
// Default Constructor is called
// Rahul
// 19