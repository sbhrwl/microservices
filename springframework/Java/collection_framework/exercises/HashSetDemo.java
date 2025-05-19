import java.util.*;

public class HashSetDemo {
    public static void main(String[] args) {
        HashSet hs = new HashSet();
        hs.add(100);
        hs.add(20);
        hs.add(30);
        hs.add(40);
        System.out.println(hs);
    }
}
// Output
// [100, 20, 40, 30]