package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author scorpio
 */
public class AtomicIntegerThread {
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
