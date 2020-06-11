package com.shop.seckill.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 假如A团伙绑了B，告诉B的家人C，需要1000万赎人，A与C达成一致意见到某一个地点交换人质，
 * 于是，A团伙和C同时到达交换地点，然后同时一手交钱一手交人质。
 * <p>
 * 解决方案：Exchanger 两个线程之间进行数据交换
 * <p>
 * 应用场景：用于两个线程之间交换数据，例如校对工作
 *
 * @author scorpio
 */
public class ExchangerThread {
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
        // 定义交换器，交换String类型的数据，当然是可以为任意类型
        Exchanger<String> exchanger = new Exchanger<>();
        // 线程池
        ExecutorService threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSizeSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory);

        // 绑架者A
        threadPool.execute(() -> {
            try {
                // 准备人质
                String person = "B";
                String money = exchanger.exchange(person);
                System.out.println("绑架者用B交换回：" + money);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 家属C
        threadPool.execute(() -> {
            try {
                // 准备1000万
                String money = "1000万";
                String person = exchanger.exchange(money);
                System.out.println("C用1000万交换回：" + person);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPool.shutdown();
    }
}
