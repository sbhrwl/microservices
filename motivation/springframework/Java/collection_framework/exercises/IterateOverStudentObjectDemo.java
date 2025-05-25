import java.util.*;
import java.util.Map.*;

class Student {
    private String name;
    private int age;
    private String city;

    public Student(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String toString() {
        return name + " " + age + " " + city;
    }

}

public class IterateOverStudentObjectDemo {
    public static void main(String[] args) {
        Student st1 = new Student("Rohan", 18, "Bengaluru");
        Student st2 = new Student("Rohit", 19, "Delhi");
        Student st3 = new Student("Ramesh", 22, "Mysuru");

        // Create a hashmap for student objects
        Map map = new HashMap();
        map.put(1, st1);
        map.put(2, st2);
        map.put(3, st3);
        System.out.println("Map- " + map);

        // Iterate over student's data
        Set set = map.entrySet();
        Iterator itr = set.iterator();
        while (itr.hasNext()) {
            // System.out.println(itr.next());
            Map.Entry data = (Entry) itr.next();
            System.out.println("Key- " + data.getKey() + " and corresponding value including name, age and city- "
                    + data.getValue());
        }
    }
}
// Output
// Map- {1=Rohan 18 Bengaluru, 2=Rohit 19 Delhi, 3=Ramesh 22 Mysuru}
// Key- 1 and corresponding value including name, age and city- Rohan 18
// Bengaluru
// Key- 2 and corresponding value including name, age and city- Rohit 19 Delhi
// Key- 3 and corresponding value including name, age and city- Ramesh 22 Mysuru