// @FunctionalInterface
// interface Car
// { 
// 	void drive();
// }
// public class LambdaDemo{
// 	public static void main(String args[]){
// 		Car obj=()->System.out.println("Driving...");

// 		obj.drive();
// 	}
// } 

@FunctionalInterface
interface Car {
    void drive(int avgSpeed, int topSpeed);
}

public class LambdaDemo {
    public static void main(String[] args) {
        // Implement anonymous method
        Car obj = (avg, ts) -> System.out.println("Running with avg speed of " + avg);
        obj.drive(16, 140);
    }
}
// Output
// Running with avg speed of 16