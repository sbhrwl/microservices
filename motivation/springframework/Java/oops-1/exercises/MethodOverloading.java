class Calc {
	public int add(int n1, int n2) {
		int result = n1 + n2;
		return result;
	}

	public int add(int n1, int n2, int n3) {
		int result = n1 + n2 + n3;
		return result;
	}
}

public class MethodOverloading {
	public static void main(String[] agrs) {
		Calc obj = new Calc();
		int result1 = obj.add(4, 3);
		int result2 = obj.add(4, 3, 2);
		System.out.println(result1);
		System.out.println(result2);
	}
}
// Output
// 7
// 9
