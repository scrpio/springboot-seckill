package com.shop.seckill.thread;

/**
 * 如果使用ThreadLocal管理变量，则每一个使用该变量的线程都获得该变量的副本，
 * 副本之间相互独立，这样每一个线程都可以随意修改自己的变量副本，而不会对其他线程产生影响。
 * <p>
 * ThreadLocal() : 创建一个线程本地变量
 * get() : 返回此线程局部变量的当前线程副本中的值
 * initialValue() : 返回此线程局部变量的当前线程的"初始值"
 * set(T value) : 将此线程局部变量的当前线程副本中的值设置为value
 *
 * @author scorpio
 */
public class ThreadLocalTest {
    public static class Bank {
        /**
         * 使用ThreadLocal类管理共享变量account
         */
        private static ThreadLocal<Integer> account = ThreadLocal.withInitial(() -> 100);

        public void save(int money) {
            account.set(account.get() + money);
        }

        public int getAccount() {
            return account.get();
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
        ThreadLocalTest tt = new ThreadLocalTest();
        tt.useThread();
    }
}
