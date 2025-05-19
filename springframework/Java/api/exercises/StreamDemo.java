import java.util.Arrays;
import java.util.*;
import java.util.stream.*;

public class StreamDemo {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(8, 4, 2, 7, 5);
        Stream<Integer> streamData = list.stream();
        // long count=streamData.count();
        // System.out.println(count);

        // Stream<Integer> sortedStream=filData.sorted();
        // Stream<Integer> mapStream=sortedStream.map(n->n*2);

        // Function interface Vs Predicate

        // Function interface is an interface with only one abstract method
        // Function<T,R> where
        // T is the type of object type input and
        // R is the type of object returned
        // Stream interface has "map" abstract method
        // We provide implementation for map and use it like a Lambda function

        // Predicate<T> where
        // T is an object type input
        // The purpose of Predicate<T> is to decide if the object of type T passes some
        // test.
        // Predicate<Integer> pre=i -> i%2==0;
        // System.out.println(pre)

        Stream<Integer> finalStream = streamData.filter(n -> n % 2 == 0)
                .sorted()
                .map(n -> n * 2);

        finalStream.forEach(n -> System.out.println(n));
        // streamData.forEach(n->System.out.println(n));
    }
}
// Output
// 4
// 8
// 16