/*
 * This example is an extension to the previous week example AccountWithoutSynch,
 * but ensuring synchronization, so the final balance will always be 100 .
 * Here synchronization is achieved using a synchronized block in the body of the
 * of the deposit method.
 */

public class AccountWithSync2 {

    private static Account account = new Account();

    public static void main(String[] args) {
        Thread threads[] = new Thread[100];

        // Create 100 threads
        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new AddAPennyTask());
        }

        // Start the 100 threads
        for (int i = 0; i < 100; i++) {
            threads[i].start();
        }

        for(int i=0; i < 100; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("What is the final balance? " + account.getBalance());
    }

    // A thread for adding a penny to the account
    private static class AddAPennyTask implements Runnable{

        public void run(){
            try {
                account.deposit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // An inner class for account
    private static class Account {

        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        public void deposit(int amount) throws InterruptedException {
            synchronized (this) {
                int newBalance = balance + amount;
                balance = newBalance;
            }
            //System.out.println("The balance now is: "+balance); //this is not synchronized
        }
    }
}
