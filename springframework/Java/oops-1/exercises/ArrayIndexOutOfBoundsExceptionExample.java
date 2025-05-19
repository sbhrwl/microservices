// public class ArrayIndexOutOfBoundsExceptionExample {
// 	public static void main(String[] args) {

// 		int a[] = { 5, 6, 7, 8 };
// 		System.out.println(a[4]);
// 	}
// }

// Output: ArrayIndexOutOfBoundsException

public class ArrayIndexOutOfBoundsExceptionExample {
	public static void main(String[] args) {

		int a[] = { 5, 8, 4, 3 };
		System.out.println(a[a.length - 1]);
		System.out.println("Bye");
	}
}
// Output
// 3
// Bye