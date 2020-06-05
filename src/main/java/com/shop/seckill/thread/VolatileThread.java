package com.shop.seckill.thread;

/**
 * @author scorpio
 */
public class VolatileThread {
    class Bank {
        // 需要同步的变量加上volatile
        private volatile int account = 100;

        public int getAccount() {
            return account;
        }

        /**
         * volatile关键字为域变量的访问提供了一种免锁机制，
         * 使用volatile修饰域相当于告诉虚拟机该域可能会被其他线程更新，
         * 因此每次使用该域就要重新计算，而不是使用寄存器中的值
         * volatile不会提供任何原子操作，它也不能用来修饰final类型的变量
         * 注：多线程中的非同步问题主要出现在对域的读写上，如果让域自身避免这个问题，则就不需要修改操作该域的方法。
         *     用final域，有锁保护的域和volatile域可以避免非同步的问题。
         *
         * @param money
         */
        public void save(int money) {
            account += money;
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
        VolatileThread vt = new VolatileThread();
        vt.useThread();
    }
}
