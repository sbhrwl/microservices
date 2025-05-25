class MyThread implements Runnable {
    public void run() {
        try {
            for (int i = 0; i < 2; i++) {
                System.out.println("Focus is important");
                Thread.sleep(3000);
            }

        } catch (Exception e) {
            System.out.println("Some problem");
        }
    }
}

public class JoinIsAliveDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("Main Thread started");
        MyThread p = new MyThread();
        Thread t1 = new Thread(p);

        System.out.println(t1.isAlive());// false
        t1.start();
        System.out.println(t1.isAlive());// true
        t1.join();

        System.out.println("Main Thread Finised the work");
    }
}
// Output
// Main Thread started
// false
// true
// Child Thread
// Main Thread Finised the work