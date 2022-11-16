/*
 * In this example, two threads (DepositTask and WithdrawTask) modify
 * together the balance of an account not only in a synchronized way,
 * but also in cooperation. So the WithdrawTask will not withdraw if
 * amount is larger than the current balance. In that case it will wait
 * on the condition that a new deposit must be done.
 */

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ThreadCooperation {

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
                    account.deposit((int) (Math.random() * 10) + 1);
                    Thread.sleep(1000); //intentional delay to make it more visible
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
                account.withdraw((int) (Math.random() * 20) + 1);
            }
        }
    }

    // An inner class for account
    private static class Account {
        // Create a new lock
        private static Lock lock = new ReentrantLock();

        // Create a condition
        private static Condition newDeposit = lock.newCondition();

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
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock(); // Release the lock
            }
        }

        public void deposit(int amount) {
            lock.lock(); // Acquire the lock
            try {
                balance += amount;
                System.out.println(DepositColor + "Deposit " + amount + "\t\t\t\t\t" + getBalance());
                newDeposit.signalAll();
            } finally {
                lock.unlock(); // Release the lock
            }
        }
    }
}

