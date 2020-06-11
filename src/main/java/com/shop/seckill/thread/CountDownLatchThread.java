package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 比如有一个任务A，他需要等待其他几个任务(BCD)都执行完毕之后才能执行这个任务
 * 解决方案：CountDownLatch 倒计时器
 * await()：阻塞当前线程，等待其他线程执行完成，直到计数器计数值减到0
 * countDown()：负责计数器的减一
 * 应用场景：可以用于模拟高并发
 * <p>
 * CountDownLatch 与 CyclicBarrier 区别：
 * 共同点：都能够实现线程之间的等待
 * 不同点：
 * CountDownLatch 一般用于某个线程A等待若干个其他线程执行完任务之后，它才能执行
 * CyclicBarrier 一般用于一组线程互相等待到某个状态，然后这一组线程在同时执行
 * CountDownLatch 是不能重用的，CyclicBarrier 可以重复使用
 *
 * @author scorpio
 */
public class CountDownLatchThread {
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

    public static void main(String[] args) {
        // 定义倒计时器
        final CountDownLatch latch = new CountDownLatch(3);
        // 线程池
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory);

        // 模拟一个子任务B
        threadPool.execute(() -> {
            try {
                // 模拟任务执行时间
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("子任务B" + Thread.currentThread().getName() + "正在执行");
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("子任务B" + Thread.currentThread().getName() + "执行完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 倒计时减掉1
                latch.countDown();
            }
        });
        // 模拟一个子任务C
        threadPool.execute(() -> {
            try {
                // 模拟任务执行时间
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("子任务C" + Thread.currentThread().getName() + "正在执行");
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("子任务C" + Thread.currentThread().getName() + "执行完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 倒计时减掉1
                latch.countDown();
            }
        });
        // 模拟一个子任务D
        threadPool.execute(() -> {
            try {
                // 模拟任务执行时间
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("子任务D" + Thread.currentThread().getName() + "正在执行");
                Thread.sleep(new Random().nextInt(1000));
                System.out.println("子任务D" + Thread.currentThread().getName() + "执行完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 倒计时减掉1
                latch.countDown();
            }
        });

        System.out.println("等待3个子任务执行完毕" + Thread.currentThread().getName() + "主任务才开始执行");
        try {
            // 等待子任务执行完毕，此时阻塞
            latch.await();
            System.out.println("说明BCD三个子任务已经执行完毕");
            // 继续执行主任务
            System.out.println("继续执行主任务：" + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }
}
