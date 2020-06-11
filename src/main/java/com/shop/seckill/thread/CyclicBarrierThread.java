package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 公司周末组织去聚餐，首先各自从家里出发到聚餐地点，当所有人全部到齐之后，才开始吃饭
 * 如果人员未到齐，到的人就只能等待在那里，直到所有人都到齐之后才能吃饭或者做后面的事情。
 * <p>
 * 解决方案：同步屏障 CyclicBarrier
 * <p>
 * 应用场景：用于多线程计算数据，最后合并计算结果，例如多个老师打分，最后合并算平均分
 *
 * @author scorpio
 */
public class CyclicBarrierThread {
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
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
            // 在吃饭之前做点别的事情
            System.out.println("人员全部到齐了，拍照留念...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 线程池
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory);

        // 模拟3个用户
        for (int i = 1; i <= 3; i++) {
            final int user = i;
            Runnable runnable = () -> {
                try {
                    // 模拟每个人来的时间各不一样
                    Thread.sleep(new Random().nextInt(1000));
                    System.out.println(user + "到达聚餐地点，当前已有" + (cyclicBarrier.getNumberWaiting() + 1) + "人到达");
                    // 设置屏障等待，只有当前线程都到达之后，才能往下走
                    cyclicBarrier.await();
                    if (user == 3) {
                        System.out.println("人员全部到齐，开始吃饭...");
                    }
                    Thread.sleep(new Random().nextInt(2000));
                    System.out.println(user + "吃晚饭了，准备回家...");
                    // CyclicBarrier 可以重复使用 doSomething
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            threadPool.execute(runnable);
        }
        threadPool.shutdown();
    }
}
