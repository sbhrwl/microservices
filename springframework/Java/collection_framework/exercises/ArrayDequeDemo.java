import java.util.*;

public class ArrayDequeDemo {
    public static void main(String[] args) {
        ArrayDeque ad1 = new ArrayDeque();
        // add elements
        ad1.add(100);
        ad1.add(200);
        ad1.add(200);
        System.out.println(ad1);
        // Remeber its NOT a list but a Queue
        ad1.addFirst(10);
        ad1.addLast(20);

        System.out.println(ad1);
        ad1.add("PW");
        System.out.println(ad1);

        // Add element at the end of the queue
        ad1.offer(500);
        ad1.offerLast(10);
        // Add element at the start of the queue
        ad1.offerFirst(1);
        System.out.println(ad1);
    }
}
// Output
// [100, 200, 200]
// [10, 100, 200, 200, 20]
// [10, 100, 200, 200, 20, PW]
// [1, 10, 100, 200, 200, 20, PW, 500, 10]