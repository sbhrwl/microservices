
import java.util.*;

class MyTasks extends Thread {

    public void run() {
        String tName = Thread.currentThread().getName();
        if (tName.equals("CALCULATION")) {
            calculationTask();
        } else {
            messageTask();
        }
    }

    public void calculationTask() {
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
    }

    public void messageTask() {
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

public class MultiThreadWithSingleRun {
    public static void main(String[] args) {
        System.out.println("Main Thread started");

        MyTasks thread1 = new MyTasks();
        MyTasks thread2 = new MyTasks();

        thread1.setName("CALCULATION");
        thread2.setName("PRINT");

        thread1.start();
        thread2.start();
    }
}
// Output
// Main Thread started
// Calculation Task Started
// Displaying important message task
// Focus is important to master skills
// Please enter first number
// Focus is important to master skills
// Focus is important to master skills
// Displaying import message task ended