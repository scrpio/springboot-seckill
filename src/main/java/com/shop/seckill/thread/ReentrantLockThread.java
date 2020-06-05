package com.shop.seckill.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author scorpio
 */
public class ReentrantLockThread {
    class Bank {
        private int account = 100;
        // 需要声明这个锁
        private Lock lock = new ReentrantLock();

        public int getAccount() {
            return account;
        }

        /**
         * ReentrantLock类是可重入、互斥、实现了Lock接口的锁，
         * 它与使用synchronized方法和快具有相同的基本行为和语义，并且扩展了其能力
         * 注：ReentrantLock()还有一个可以创建公平锁的构造方法，
         * 但由于能大幅度降低程序运行效率，不推荐使用
         *
         * @param money
         */
        public void save(int money) {
            lock.lock();
            try {
                account += money;
            } finally {
                lock.unlock();
            }
        }
    }

    class NewThread implements Runnable {
        private Bank bank;

        public NewThread(Bank bank) {
            this.bank = bank;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                bank.save(10);
                System.out.println(i + "账户余额为：" + bank.getAccount());
            }
        }
    }

    /**
     * 建立线程，调用内部类
     */
    public void useThread() {
        Bank bank = new Bank();
        NewThread newThread = new NewThread(bank);
        System.out.println("线程1");
        Thread thread1 = new Thread(newThread);
        thread1.start();
        System.out.println("线程2");
        Thread thread2 = new Thread(newThread);
        thread2.start();
    }

    public static void main(String[] args) {
        ReentrantLockThread rt = new ReentrantLockThread();
        rt.useThread();
    }
}
