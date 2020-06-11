package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author scorpio
 */
public class ReentrantLockThread {
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
