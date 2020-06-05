package com.shop.seckill.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author scorpio
 */
public class AtomicIntegerThread {
    class Bank {
        /**
         * AtomicInteger 表可以用原子方式更新int的值，可用在应用程序中(如以原子方式增加的计数器)，
         * 但不能用于替换Integer；可扩展Number，允许那些处理机遇数字类的工具和实用工具进行统一访问。
         *
         * AtomicInteger(int initialValue) : 创建具有给定初始值的新的AtomicInteger
         * addAddGet(int dalta) : 以原子方式将给定值与当前值相加
         * get() : 获取当前值
         */
        private AtomicInteger account = new AtomicInteger(100);

        public AtomicInteger getAccount() {
            return account;
        }

        public void save(int money) {
            account.addAndGet(money);
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
        NewThread new_thread = new NewThread(bank);
        System.out.println("线程1");
        Thread thread1 = new Thread(new_thread);
        thread1.start();
        System.out.println("线程2");
        Thread thread2 = new Thread(new_thread);
        thread2.start();
    }

    public static void main(String[] args) {
        AtomicIntegerThread at = new AtomicIntegerThread();
        at.useThread();
    }
}
