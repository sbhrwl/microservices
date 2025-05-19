import java.util.Scanner;

public class NeedForMultithreading {
    public static void main(String[] args) {
        // 3 Independent tasks
        System.out.println("Calculation Task Started");

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter first number");
        int num1 = sc.nextInt();

        System.out.println("Please enter 2nd number");
        int num2 = sc.nextInt();

        int res = num1 + num2;

        System.out.println(res);
        System.out.println("Calculation Task Ended");

        System.out.println("****************************************************");

        System.out.println("Printing * Task Started");

        for (int i = 0; i < 4; i++) {
            System.out.println("*");
        }

        System.out.println("Printing * Task Ended");

        System.out.println("****************************************************");

        System.out.println("Displaying important message task");
        for (int i = 0; i < 3; i++) {
            System.out.println("Focus is important to master skills");
        }

        System.out.println("Displaying import message task ended");
        System.out.println("****************************************************");
    }
}
// Output
// Calculation Task Started
// Please enter first number
// 5
// Please enter 2nd number
// 25
// 30
// Calculation Task Ended
// ****************************************************
// Printing * Task Started
// *
// *
// *
// *
// Printing * Task Ended
// ****************************************************
// Displaying important message task
// Focus is important to master skills
// Focus is important to master skills
// Focus is important to master skills
// Displaying import message task ended
// ****************************************************