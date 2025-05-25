import java.util.*;

public class LinkedListDemo {
    public static void main(String[] args) {
        LinkedList ll1 = new LinkedList();

        // Add elements to list
        ll1.add(100);
        ll1.add(200);
        ll1.add(300);
        ll1.add(300);

        System.out.println(ll1);
        // check index of a particular element
        System.out.println(ll1.get(1));

        System.out.println("indexOf 300-" + ll1.indexOf(300));
        System.out.println("lastIndexOf 300-" + ll1.lastIndexOf(300));
        System.out.println("getFirst-" + ll1.getFirst());
        System.out.println("getLast-" + ll1.getLast());

        // push method
        ll1.push(10);
        System.out.println("After push" + ll1);

        // pop method
        System.out.println("After pop" + ll1.pop());

        // LinkedList ll2=new LinkedList();
        // ll2.add("PWskill");
        // ll2.add(1);
        // ll2.add('j');
        // System.out.println(ll2);

        // LinkedList ll3=new LinkedList();
        // ll3.add(10);
        // ll3.add(20);
        // ll3.add(30);

        // ll3.addFirst("PW");
        // ll3.addLast(100);

        // System.out.println(ll3);

        // ll3.add(2, 10);
        // System.out.println(ll3);

        // System.out.println(ll3.peek());
        // System.out.println(ll3);

        // System.out.println(ll3.poll());
        // System.out.println(ll3);
    }
}
// Output
// [100, 200, 300, 300]
// 200
// indexOf 300-2
// lastIndexOf 300-3
// getFirst-100
// getLast-300
// After push[10, 100, 200, 300, 300]
// After pop10