import java.util.Scanner;

class CalculationTask extends Thread {

    public void run() {
        System.out.println("Calculation Task Started");

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter first number");
        int num1 = sc.nextInt();

        System.out.println("Please enter 2nd number");
        int num2 = sc.nextInt();

        int result = num1 + num2;

        System.out.println(result);
        System.out.println("Calculation Task Ended");
        System.out.println("****************************************************");
    }
}

class MessageTask extends Thread {

    public void run() {
        System.out.println("Displaying important message task");
        try {
            for (int i = 0; i < 3; i++) {
                System.out.println("Focus is important to master skills");
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            System.out.println("Some problem");
        }
        System.out.println("Displaying import message task ended");
    }
}

public class MultiThreadExample {
    public static void main(String[] args) {
        System.out.println("Main Thread started");
        CalculationTask calculationThreadObject = new CalculationTask();
        MessageTask messageTaskObject = new MessageTask();

        // Option 1
        // calculationThreadObject.start();
        // messageTaskObject.start();

        // Option 2: Preferred way
        Thread ct_thread = new Thread(calculationThreadObject);
        Thread mt_thread = new Thread(messageTaskObject);
        ct_thread.start();
        mt_thread.start();
    }
}
// Main Thread started
// Calculation Task Started
// Displaying important message task
// Focus is important to master skills
// Please enter first number
// Focus is important to master skills
// Focus is important to master skills
// Displaying import message task ended
