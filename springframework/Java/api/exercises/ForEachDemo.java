import java.util.Arrays;
import java.util.*;

public class ForEachDemo {
    public static void main(String[] args) {
        // List<Integer> list1=new ArrayList<Integer>();
        // list1.add(2);
        // list1.add(4);
        // list1.add(5);
        // list1.add(8);
        // list1.add(9);
        // System.out.println(list1);

        // Better way to declare ArrayList
        List<Integer> list2 = Arrays.asList(2, 4, 5, 8, 9);
        System.out.println(list2);
        // for(Integer i:list2)
        // {
        // System.out.println(i);
        // }
        // Consumer<Integer> cons=i-> System.out.println(i);
        list2.forEach(i -> System.out.println(i));
    }
}
// Output
// [2, 4, 5, 8, 9]
// 2
// 4
// 5
// 8
// 9