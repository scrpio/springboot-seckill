package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author scorpio
 */
public class SynchronizedThread {
    /**
     * 线程池的基本大小，如果大于0，即使本地任务执行完也不会被销毁
     */
    static int corePoolSize = 10;
    /**
     * 线程池最大数量
     */
    static int maximumPoolSizeSize = 100;
    /**
     * 线程活动保持时间，当空闲时间达到该值时，线程会被销毁，只剩下 corePoolSize 个线程位置
     */
    static long keepAliveTime = 1;
    /**
     * 任务队列，当请求的线程数大于 corePoolSize 时，线程进入该阻塞队列
     */
    static LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1024);
    /**
     * 线程工厂，用来生产一组相同任务的线程，同时也可以通过它增加前缀名，虚拟机栈分析时更清晰
     */
    static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("thread-pool-%d").build();

    static class Bank {
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
        public synchronized void saveMethod(int money) {
            account += money;
        }

        /**
         * 用同步代码块实现
         * 被该关键字修饰的语句块会自动被加上内置锁，从而实现同步
         * 注：同步是一种高开销的操作，因此应该尽量减少同步的内容。
         * 通常没有必要同步整个方法，使用synchronized代码块同步关键代码即可。
         *
         * @param money
         */
        public void saveBlock(int money) {
            synchronized (this) {
                account += money;
            }
        }
    }

    static class NewThread implements Runnable {
        private Bank bank;

        public NewThread(Bank bank) {
            this.bank = bank;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                bank.saveMethod(10);
                System.out.println(i + "账户余额为：" + bank.getAccount());
            }
        }
    }

    public static void main(String[] args) {
        // 线程池
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory);
        Bank bank = new Bank();
        NewThread newThread = new NewThread(bank);
        System.out.println("线程1");
        threadPool.execute(newThread);
        System.out.println("线程2");
        threadPool.execute(newThread);
        threadPool.shutdown();
    }
}
