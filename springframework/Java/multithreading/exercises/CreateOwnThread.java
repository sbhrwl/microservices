class MyThread extends Thread {
    public void run() {
        System.out.println("Child Thread");
    }
}

public class CreateOwnThread {
    public static void main(String[] args) {
        System.out.println("Main Thread");
        MyThread threadObject = new MyThread();
        threadObject.start();
        // start()
        // 1. Register thread to Thread scheduler
        // 2. Execute run() method
    }
}
// Output
// Main Thread
// Child Thread