class Calc {
	public int add(int nums[]) {
		int result = 0;
		for (int n : nums) {
			result = result + n;
		}
		return result;
	}
}

public class AnonymousArray {
	public static void main(String[] args) {
		Calc obj = new Calc();
		// int nums[] = { 5, 2, 3, 7, 8, 2 };
		// int result = obj.add(nums);
		int result = obj.add(new int[] { 5, 2, 3, 7, 8, 2 });
		System.out.println(result);
	}
}
// Output
// 27
// Works in online code editor but somehow fails here in VS code and throws
// Exception in thread "main" java.lang.NoSuchMethodError: 'int Calc.add(int[])'
// at AnonymousArray.main(AnonymousArray.java:16)