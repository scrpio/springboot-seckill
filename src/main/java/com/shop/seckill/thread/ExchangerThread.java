package com.shop.seckill.thread;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public static void main(String[] args) {
        // 定义交换器，交换String类型的数据，当然是可以为任意类型
        Exchanger<String> exchanger = new Exchanger<>();
        // 定义线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();
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
