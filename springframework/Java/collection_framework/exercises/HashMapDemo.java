import java.util.*;

public class HashMapDemo {
    public static void main(String[] args) {
        HashMap hm1 = new HashMap();
        // Number as a key
        hm1.put(01, "Rohit");
        hm1.put(02, "Rohan");
        hm1.put(03, "Rohan");
        // Duplicate is allowed, but Hashmap stores only one entry
        // hm1.put(03, "Rohan");
        // Null is allowed
        // hm1.put(null, null);
        System.out.println(hm1);

        HashMap hm2 = new HashMap();
        // String as a key
        hm2.put("Virat", "Rohit");
        hm2.put("Rohit", "Rohan");
        hm2.put("Hyder", "Rohan");
        // Order is NOT preserved
        System.out.println(hm2);

        LinkedHashMap lhm = new LinkedHashMap();
        // Order is preserved
        lhm.put("Virat", "Rohit");
        lhm.put("Rohit", "Rohan");
        lhm.put("Hyder", "Rohan");
        System.out.println(lhm);
    }
}
// Output
// {1=Rohit, 2=Rohan, 3=Rohan}
// {Rohit=Rohan, Virat=Rohit, Hyder=Rohan}
// {Virat=Rohit, Rohit=Rohan, Hyder=Rohan}