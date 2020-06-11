package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 假如现在有20个人去售票厅窗口买票，但是窗口只有2个，那么同时能够买票的只能有2个人，
 * 当2个人中任意1个人买完票离开窗口之后，等待的18个人中又会有一个人可以占用窗口买票。
 * <p>
 * 真实需求：控制并发数为2
 * 拆解转化业务需求：
 * 人=线程
 * 2个窗口=资源
 * 在窗口买票=表示线程正在执行
 * 离开售票窗口=线程执行完毕
 * 等待买票=线程阻塞，不能执行
 * <p>
 * 解决方案：信号量 Semaphore
 * acquire()：获取信号量许可
 * release()：释放信号量许可证
 * 应用场景：用于流量控制，限流
 *
 * @author scorpio
 */
public class SemaphoreThread {
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

    static class MyTask implements Runnable {
        // 信号量
        private Semaphore semaphore;
        // 第几个用户
        private int user;

        public MyTask(Semaphore semaphore, int user) {
            this.semaphore = semaphore;
            this.user = user;
        }

        @Override
        public void run() {
            try {
                // 获取信号量许可，才能占用窗口
                semaphore.acquire();
                // 运行到这里说明获得了许可，可以去买票了
                System.out.println("用户" + user + "进入窗口，准备买票...");
                // 模拟买票时间
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("用户" + user + "买票完成，准备离开...");
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("用户" + user + "离开售票窗口...");
                // 释放信号量许可证
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2);
        // 线程池
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory);

        // 模拟20个用户
        for (int i = 1; i < 21; i++) {
            threadPool.execute(new MyTask(semaphore, i));
        }
        // 关闭线程池
        threadPool.shutdown();
    }
}
