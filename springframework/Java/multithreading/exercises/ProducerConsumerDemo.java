class Queue {
    int x;
    boolean valueInX = false;

    synchronized public void put(int j) {
        try {
            if (valueInX == true) {
                wait();
            }

            else {
                x = j;
                System.out.println("I have Produced value x  " + x);
                valueInX = true;

                notify();
            }

        } catch (Exception e) {

            System.out.println("Kuch problem hogaya");
        }
    }

    synchronized public void get() {
        try {
            if (valueInX == false) {
                wait();
            } else {
                System.out.println("I have Consumed value   " + x);
                valueInX = false;
                notify();
            }

        } catch (Exception e) {
            System.out.println("Some problem");
        }
    }
}

class Producer extends Thread {
    Queue q;

    Producer(Queue b) {
        q = b;
    }

    public void run() {
        int i = 1;
        while (true) {
            q.put(i++);
        }

    }

}

class Consumer extends Thread {
    Queue q;

    Consumer(Queue a) {
        q = a;
    }

    public void run() {
        while (true) {
            q.get();
        }
    }
}

public class ProducerConsumerDemo {
    public static void main(String[] args) {
        Queue q = new Queue();

        Producer p = new Producer(q);
        Consumer c = new Consumer(q);

        p.start();
        c.start();
    }
}
// Output
// I have Produced value x 9053
// I have Consumed value 9053
// I have Produced value x 9055
// I have Consumed value 9055
// I have Produced value x 9056
// I have Consumed value 9056
// I have Produced value x 9058
// I have Consumed value 9058