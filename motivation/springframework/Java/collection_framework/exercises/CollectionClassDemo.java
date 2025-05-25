import java.util.*;

public class CollectionClassDemo {
	public static void main(String[] args) {
		// Create a list of strings
		ArrayList<String> al = new ArrayList<String>();
		al.add("Geeks For Geeks");
		al.add("Friends");
		al.add("Dear");
		al.add("Is");
		al.add("Superb");

		System.out.println("List- " + al);
		/*
		 * Collections.sort method is sorting the
		 * elements of ArrayList in ascending order.
		 */
		Collections.sort(al);

		// Let us print the sorted list
		System.out.println("List after the use of" +
				" Collection.sort() :\n" + al);
	}
}
// Output
// List- [Geeks For Geeks, Friends, Dear, Is, Superb]
// List after the use of Collection.sort() :
// [Dear, Friends, Geeks For Geeks, Is, Superb]