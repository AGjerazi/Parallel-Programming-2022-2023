/*
 * This example is a modification of the ThreadCooperation example.
 * The DepositTask thread is allowed to make a deposit only if the new balance
 * would not exceed the MAX_BALANCE, otherwise it will wait for some withdrawals
 * to be made.
 */


import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ThreadCooperationV3 {

    private static Account account = new Account();
    public final static String DepositColor = ThreadColors.ANSI_BLUE;
    public final static String WithdrawColor = ThreadColors.ANSI_RED;

    public static void main(String[] args) {
        System.out.println("Thread 1\t\tThread 2\t\tBalance");
        // Create a thread pool with two threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new DepositTask());
        executor.execute(new WithdrawTask());
        executor.shutdown();
    }

    public static class DepositTask implements Runnable {

        @Override // Keep adding an amount to the account
        public void run() {
            try { // Purposely delay it to let the withdraw method proceed
                while (true) {
                    account.deposit((int) (Math.random() * 45) + 1);
                    Thread.sleep((int)(1000*Math.random())); //intentional delay to make it more visible
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class WithdrawTask implements Runnable {

        @Override // Keep subtracting an amount from the account
        public void run() {
            while (true) {
                account.withdraw((int) (Math.random() * 45) + 1);
                try {
                    Thread.sleep((int)(1000*Math.random())); //intentional delay to make it more visible
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // An inner class for account
    private static class Account {
        // Create a new lock
        private static Lock lock = new ReentrantLock();
        private final int MAX_INSURED_BALANCE = 100;

        // Create a condition
        private static Condition newDeposit = lock.newCondition();
        private static Condition newWithdrawal = lock.newCondition();

        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        public void withdraw(int amount) {
            lock.lock(); // Acquire the lock
            try {
                while (balance < amount) {
                    System.out.println(WithdrawColor + "\t\t\tWait for a deposit (cannot withdraw " + amount + " )");
                    newDeposit.await();
                }

                balance -= amount;
                System.out.println(WithdrawColor + "\t\t\tWithdraw " + amount + "\t\t" + getBalance());
                newWithdrawal.signalAll();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock(); // Release the lock
            }
        }

        public void deposit(int amount) {
            lock.lock(); // Acquire the lock
            try {
                while( balance + amount > MAX_INSURED_BALANCE){
                    System.out.println(DepositColor + "Cannot deposit " + amount + ". Waiting for some withdrawals.");
                    newWithdrawal.await();
                }
                balance += amount;
                System.out.println(DepositColor + "Deposit " + amount + "\t\t\t\t\t" + getBalance());
                newDeposit.signalAll();
            } catch(Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock(); // Release the lock
            }
        }
    }
}

