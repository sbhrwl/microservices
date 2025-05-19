import java.io.*;

class Cricketer implements Serializable {
    String name;
    transient int age; // transient keyword, age will not participate in serialization
    int runs;

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

public class SelectiveSerializationDemo {
    public static void main(String[] args) throws Exception {
        Cricketer c = new Cricketer("Sachin", 44, 30000);
        c.disp();
        // Serialization
        FileOutputStream fos = new FileOutputStream("dir_demo/skill.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(c);
        oos.flush();
        oos.close();

        // De-Serialization
        FileInputStream fis = new FileInputStream("dir_demo/skill.txt");
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);

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
// 0, as age did not participated in serialization
// 30000