
class MyThread implements Runnable {
    public void run() {
        System.out.println("Child Thread Executing");
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Some problem");
        }
        System.out.println("Child thread task completed");
    }
}

public class MultiThreadWithRunnable {
    public static void main(String[] args) {
        System.out.println("Main Thread started");
        MyThread d = new MyThread();
        Thread t1 = new Thread(d);
        t1.start();
    }
}
// Output
// Main Thread started
// Child Thread