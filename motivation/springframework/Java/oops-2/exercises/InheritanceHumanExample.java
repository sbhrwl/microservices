class Human // Super class // Base class// Parent class
{
    private String name;
    int age;

    Human() {
        System.out.println("Human class Constructor");
    }

    void sleep() {
        age = 18;
        System.out.println("Human needs good sleep");
        System.out.println(age);
    }
}

class Student extends Human // Child class// sub class // derived class
{
    // public Student()
    // {
    // super(); // Call Parent class constructor
    // }
    void disp() {
        System.out.println(" The age is : " + age);
        // System.out.println(" The Name is : " + name);
    }
}

public class InheritanceHumanExample {
    public static void main(String[] args) {
        Student st_obj = new Student();
        st_obj.sleep();
        st_obj.disp();
    }
}
// Output
// Human class Constructor
// Human needs good sleep
// 18
// The age is : 18