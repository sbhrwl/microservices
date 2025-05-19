import java.util.*;

public class TreeSetDemo {
    public static void main(String[] args) {
        TreeSet ts = new TreeSet();
        // add elements to Tree set
        ts.add(100);
        ts.add(50);
        ts.add(150);
        ts.add(25);
        ts.add(75);
        ts.add(125);
        ts.add(175);

        System.out.println("Treeset- " + ts);
        // ts.add(100);
        // System.out.println(ts);

        // Methods
        System.out.println("Higher than 50- " + ts.higher(50));
        System.out.println("Lower than 50- " + ts.lower(50));

        System.out.println("Ceiling of 40- " + ts.ceiling(40));
        System.out.println("Floor of 40- " + ts.floor(40));

        System.out.println("Ceiling- of 50" + ts.ceiling(50));
        System.out.println("Floor- of 50" + ts.floor(50));
    }
}
// Output
// Treeset- [25, 50, 75, 100, 125, 150, 175]
// Higher than 50- 75
// Lower than 50- 25
// Ceiling of 40- 50
// Floor of 40- 25
// Ceiling- of 5050
// Floor- of 5050