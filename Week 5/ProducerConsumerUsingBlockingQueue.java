/*
 * In this example two threads (ProducerTask and ConsumerTask) cooperate adding
 * and removing data from a shared buffer which is implemented using blocking queues.
 * If the buffer is full and the "put" method is called, then the current thread
 * (ProducerTask) will have to wait.
 * Alternatively, if the buffer is empty and the "take "method is called, then
 * the current thread(ConsumerTask) will have to wait.
 */

import java.util.concurrent.*;

public class ProducerConsumerUsingBlockingQueue {
    private static ArrayBlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(3);

    public static void main(String[] args) {
        // Create a thread pool with two threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new ProducerTask());
        executor.execute(new ConsumerTask());
        executor.shutdown();
    }

    // A task for adding an int to the buffer
    private static class ProducerTask implements Runnable {
        public void run() {
            try {
                int i = 1;
                while (true) {
                    System.out.println("Producer writes " + i);
                    buffer.put(i++); // Add any value to the buffer, say, 1
                    // Put the thread into sleep
                    Thread.sleep((int)(Math.random() * 1000));
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // A task for reading and deleting an int from the buffer
    private static class ConsumerTask implements Runnable {
        public void run() {
            try {
                while (true) {
                    System.out.println("\t\t\tConsumer reads " + buffer.take());
                    // Put the thread into sleep
                    Thread.sleep((int)(Math.random() * 1000));
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}