package com.shop.seckill.thread;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

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
                    System.out.println("启动线程0 -> 生产商品：" + b + "号");
                    try {
                        queue.put(b);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("启动线程0 -> 仓库中还有商品：" + queue.size() + "个");
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
                        System.out.println("启动线程1 -> 消费者买去了" + n + "号商品");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("启动线程1 -> 仓库中还有商品：" + queue.size() + "个");
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
        Thread thread1 = new Thread(lbt);
        Thread thread2 = new Thread(lbt);
        thread1.start();
        thread2.start();
    }
}
