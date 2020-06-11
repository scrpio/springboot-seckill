package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

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
