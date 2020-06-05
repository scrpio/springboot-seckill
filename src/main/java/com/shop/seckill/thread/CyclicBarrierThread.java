package com.shop.seckill.thread;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        ExecutorService threadPool = Executors.newCachedThreadPool();
        // 模拟3个用户
        for (int i = 0; i < 3; i++) {
            final int user = i + 1;
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
