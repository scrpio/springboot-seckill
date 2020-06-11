package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * LinkedBlockingQueue() : 创建一个容量为Integer.MAX_VALUE的LinkedBlockingQueue
 * put(E e) : 在队尾添加一个元素，如果队列满则阻塞
 * size() : 返回队列中的元素个数
 * take() : 移除并返回队头元素，如果队列空则阻塞
 * <p>
 * 注：三种添加元素的方法，我们要多加注意，当队列满时
 * add()方法会抛出异常
 * offer()方法返回false
 * put()方法会阻塞
 *
 * @author scorpio
 */
public class BlockingSynchronizedThread {
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

    /**
     * 定义一个阻塞队列用来存储生产出来的商品
     */
    private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    /**
     * 定义生产商品个数
     */
    private static final int size = 10;
    /**
     * 定义启动线程的标志，为0时，启动生产商品的线程；为1时，启动消费商品的线程
     */
    private int flag = 0;

    private class LinkBlockThread implements Runnable {
        @Override
        public void run() {
            int newFlag = flag++;
            System.out.println("启动线程 " + newFlag);
            if (newFlag == 0) {
                for (int i = 0; i < size; i++) {
                    int b = new Random().nextInt(255);
                    System.out.println(newFlag + "启动线程 -> 生产商品：" + b + "号");
                    try {
                        queue.put(b);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(newFlag + "启动线程 -> 仓库中还有商品：" + queue.size() + "个");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int i = 0; i < size / 2; i++) {
                    try {
                        int n = queue.take();
                        System.out.println(newFlag + "启动线程 -> 消费者买去了" + n + "号商品");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(newFlag + "启动线程 -> 仓库中还有商品：" + queue.size() + "个");
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        BlockingSynchronizedThread bst = new BlockingSynchronizedThread();
        LinkBlockThread lbt = bst.new LinkBlockThread();
        // 线程池
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory);
        threadPool.execute(lbt);
        threadPool.execute(lbt);
        threadPool.shutdown();
    }
}
