import java.util.PriorityQueue;

public class PriorityQueueDemo {
    public static void main(String[] args) {
        PriorityQueue pq = new PriorityQueue();
        // Add elements
        pq.add(100);
        pq.add(50);
        pq.add(150);
        pq.add(25);
        pq.add(75);
        pq.add(125);
        pq.add(175);

        System.out.println(pq);// elements are sorted
        // duplicates allowed
        pq.add(25);
        System.out.println(pq);
    }
}
// Output
// [25, 50, 125, 100, 75, 150, 175]
// [25, 25, 125, 50, 75, 150, 175, 100]