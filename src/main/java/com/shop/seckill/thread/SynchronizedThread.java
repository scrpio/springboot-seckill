package com.shop.seckill.thread;

/**
 * @author scorpio
 */
public class SynchronizedThread {
    class Bank {
        private int account = 100;

        public int getAccount() {
            return account;
        }

        /**
         * 用同步方法实现
         * 由于java的每个对象都有一个内置锁，当用此关键字修饰方法时，内置锁会保护整个方法。
         * 在调用该方法前，需要获得内置锁，否则就处于阻塞状态。
         * 注： synchronized关键字也可以修饰静态方法，此时如果调用该静态方法，将会锁住整个类
         *
         * @param money
         */
        public synchronized void save(int money) {
            account += money;
        }

        /**
         * 用同步代码块实现
         * 被该关键字修饰的语句块会自动被加上内置锁，从而实现同步
         * 注：同步是一种高开销的操作，因此应该尽量减少同步的内容。
         *    通常没有必要同步整个方法，使用synchronized代码块同步关键代码即可。
         *
         * @param money
         */
        public void save1(int money) {
            synchronized (this) {
                account += money;
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
        SynchronizedThread st = new SynchronizedThread();
        st.useThread();
    }
}
