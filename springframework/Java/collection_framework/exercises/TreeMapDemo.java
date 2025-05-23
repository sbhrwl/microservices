import java.util.*;

public class TreeMapDemo {
    public static void main(String[] args) {
        Hashtable ht = new Hashtable();
        ht.put(1, "Rohit");
        // ht.put(2, "Rohan");
        ht.put(3, "Ramesh");
        // ht.put(null, "Hyder");
        ht.putIfAbsent(2, "Rohan");
        System.out.println(ht);

        TreeMap tm = new TreeMap();

        Integer i = new Integer(5);

        tm.put(i, "Rohit");
        tm.put(1, "Ramesh");
        tm.put(4, "Rahul");
        tm.put(3, "Rohsn");

        System.out.println(tm);
    }
}
// Output
// {3=Ramesh, 2=Rohan, 1=Rohit}
// {1=Ramesh, 3=Rohsn, 4=Rahul, 5=Rohit}
