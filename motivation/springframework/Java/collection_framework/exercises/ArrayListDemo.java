import java.util.*;

public class ArrayListDemo {
    public static void main(String[] args) {
        ArrayList al1 = new ArrayList();
        // add elements
        al1.add(100);
        al1.add(200);
        al1.add(300);
        System.out.println(al1);
        // check index of a particular element
        System.out.println("Is 200 present in array list- " + al1.contains(200));

        int index = al1.indexOf(200);
        System.out.println("200 is at this index- " + index);
        System.out.println("What is the size of array list- " + al1.size());

        // Ensuring min capacity of array list
        al1.ensureCapacity(10);
        al1.trimToSize();
        System.out.println(al1.size());

        // Clear array list
        al1.clear();
        System.out.println(al1);

        // Array list as "Reference" for Parent "List" interface
        List al = new ArrayList();
        al.add(100);
        System.out.println(al);

        // Adding a collection to array list
        // System.out.println("****************************************");
        // ArrayList al2=new ArrayList();
        // al2.add("PW Skills");
        // al2.add(1);
        // al2.add('j');
        // al2.add(1.1);
        // System.out.println(al2);
        // System.out.println("****************************************");
        // al2.add(100);
        // System.out.println(al2);

        // System.out.println("****************************************");
        // ArrayList al3=new ArrayList();
        // al3.add(1);
        // al3.add(2);
        // al3.add(4);
        // System.out.println(al3);
        // System.out.println("After adding collection");
        // al3.addAll(al2);
        // System.out.println(al3);
        // al3.add(2, "PW");
        // System.out.println(al3);
        // al3.add(100);
        // System.out.println(al3);
    }
}[100,200,300]
// Is 200 present in array list- true
// 200 is at this index- 1
// What is the size of array list- 3
// 3
// []
// [100]