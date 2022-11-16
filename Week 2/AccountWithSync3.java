/*
 * This example is an extension to the AccountWithSynchronization2 example.
 * Here synchronization related to the changes in the balance is still achieved
 * using a synchronized block in the body of the of the deposit method.
 * On the other hand after each change in the account balance, the "current"
 * balance is printed in an non-synchronized way (outside the synchronized block).
 * You may try putting the printing instruction inside the synchronized block and
 * notice the difference.
 */
public class AccountWithSync3 {

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
            System.out.println("The balance now is: "+balance); //this is not synchronized
        }
    }
}
