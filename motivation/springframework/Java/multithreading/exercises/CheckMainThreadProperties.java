public class CheckMainThreadProperties {
    public static void main(String[] args) {
        System.out.println("Main Thread");
        System.out.println("Before changing");
        String name = Thread.currentThread().getName();
        System.out.println("The name of main thread is " + name);
        System.out.println("the priority of main thread is " + Thread.currentThread().getPriority());

        System.out.println("After changing");
        Thread t = Thread.currentThread(); // Current thread
        t.setName("PW"); // Current thread name
        t.setPriority(1); // Current thread priority

        String newName = Thread.currentThread().getName();
        System.out.println("The name of main thread is " + newName);
        System.out.println("the priority of main thread is " + Thread.currentThread().getPriority());
    }
}
// Output
// Main Thread
// Before changing
// The name of main thread is main
// the priority of main thread is 5
// After changing
// The name of main thread is PW
// the priority of main thread is 1