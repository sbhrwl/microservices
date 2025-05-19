import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Class MUST implement Serializable
// Serializable is a marker interface, no need to implement any method of this interface
class Cricketer implements Serializable {
    private String name;
    private int age;
    private int runs;

    public Cricketer(String name, int age, int runs) {
        this.name = name;
        this.age = age;
        this.runs = runs;
    }

    public void disp() {
        System.out.println(name);
        System.out.println(age);
        System.out.println(runs);
    }
}

public class SerializationDeSerializationDemo {
    public static void main(String[] args) throws Exception {
        // Serialization
        // 1. Object to serialize
        Cricketer c = new Cricketer("Sachin", 44, 30000);
        c.disp();

        FileOutputStream fos = new FileOutputStream("dir_demo/skill.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        // 2. Write serialized object to file
        oos.writeObject(c);
        oos.flush();
        oos.close();

        // DeSerialization
        // 1. Read from file
        FileInputStream fis = new FileInputStream("dir_demo/skill.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        // 2. Object to de-serialize
        Cricketer cr = (Cricketer) ois.readObject();
        cr.disp();
        ois.close();
    }
}
// Output
// Sachin
// 44
// 30000
// Sachin
// 44
// 30000