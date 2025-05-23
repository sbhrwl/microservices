// public class EnhancedForLoop {
// 	public static void main(String[] args) {

// 		int nums[] = { 5, 8, 4, 3 };
// 		for (int n : nums) {
// 			System.out.println(n);
// 		}
// 	}
// }
// Output
// 5
// 8
// 4
// 3
public class EnhancedForLoop {
	public static void main(String[] args) {

		int nums[][] = {
				{ 3, 9, 7, 5 },
				{ 1, 5, 6, 5 },
				{ 8, 4, 5, 6 }
		};
		for (int a[] : nums) {
			for (int b : a) {
				System.out.print(b + " ");
			}
			System.out.println();
		}
	}
}
// Output
// 3 9 7 5
// 1 5 6 5
// 8 4 5 6