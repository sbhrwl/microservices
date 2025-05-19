
import java.util.*;
import java.util.Map.*;

public class IteratingHashMapDemo {
    public static void main(String[] args) {
        Map map = new HashMap();
        map.put(1, "Rohan");
        map.put(2, "Rohit");
        map.put(3, "Rahul");
        map.put(4, "Ramesh");
        System.out.println("Hashmap- " + map);
        // Get value using key
        System.out.println("Value at key 4- " + map.get(4));

        // 1.1 Get all keys using method "keySet()", returns a "Set"
        Set keySet = map.keySet();
        // 1.2 Iterate over keys
        Iterator itr1 = keySet.iterator();
        while (itr1.hasNext()) {
            // System.out.println(itr1.next());
            Integer key = (Integer) itr1.next();
            System.out.println("Key- " + key);

        }
        // 2.1 Get all values using method "values()", returns a "Collection"
        Collection values = map.values();
        // 2.2 Iterate over values
        Iterator itr2 = values.iterator();
        while (itr2.hasNext()) {
            // System.out.println(itr2.next());
            String names = (String) itr2.next();
            System.out.println("Value- " + names);
        }

        // 3.1 Use method "entrySet()", returns a "Set"
        Set entrySet = map.entrySet();
        // 3.2 Iterate and get key as well as corresponding values
        Iterator itr3 = entrySet.iterator();
        while (itr3.hasNext()) {
            // System.out.println(itr3.next());
            // 3.3 Iterator returns an "Entry (key+value)"
            Map.Entry data = (Entry) itr3.next();
            System.out.println("Key- " + data.getKey() + " : " + "Value- " + data.getValue());
        }
    }
}
// Output
// Hashmap- {1=Rohan, 2=Rohit, 3=Rahul, 4=Ramesh}
// Value at key 4- Ramesh
// Key- 1
// Key- 2
// Key- 3
// Key- 4
// Value- Rohan
// Value- Rohit
// Value- Rahul
// Value- Ramesh
// Key- 1 : Value- Rohan
// Key- 2 : Value- Rohit
// Key- 3 : Value- Rahul
// Key- 4 : Value- Ramesh
