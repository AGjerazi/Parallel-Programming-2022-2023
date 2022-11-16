/**
 * In this example is illustrated the unpleasant situation of "race condition"
 * A large number of threads are created and they modify a simple instance of
 * Account. As we are not taking care about synchronization, the final result
 * may vary from one execution to another.
 * The difference compared to the previous example is that ExecutorService is
 * not used, but threads .
 */
public class AccountWithoutSynchronization {

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

        System.out.println("What is balance? " + account.getBalance());
    }

    // A thread for adding a penny to the account
    private static class AddAPennyTask implements Runnable{

        public void run(){
            account.deposit(1);
        }
    }

    // An inner class for account
    private static class Account {

        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        public void deposit(int amount) {
            int newBalance = balance + amount;
            //Thread.sleep(1); //to make the anomaly even more evident
            balance = newBalance;
        }
    }
}