package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author scorpio
 */
public class VolatileThread {
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
         * 用final域，有锁保护的域和volatile域可以避免非同步的问题。
         *
         * @param money
         */
        public void save(int money) {
            account += money;
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
                bank.save(10);
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
