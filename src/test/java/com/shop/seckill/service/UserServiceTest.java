package com.shop.seckill.service;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.shop.seckill.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    BloomFilter bloomFilter;

    @Test
    void getUserList() {
        List<User> list = userService.getUserList();
        System.out.println(list);
    }

    @Test
    public void redisTest() throws IllegalArgumentException {
        init();
        String key = "test";//UUID.randomUUID().toString();
        System.out.println("布隆过滤器判断值是否存在");
        if (bloomFilter.mightContain(key)) {
            createThread(key);
        } else {
            System.out.println("不存在该账号！");
        }
    }

    private String getMyUserName(String key) {
        String keyName = (String) redisTemplate.opsForValue().get(key);
        System.out.println("第一次从缓存中获取：" + keyName);
        // 双层检测锁
        if (StringUtils.isBlank(keyName)) {
            // 双层加锁
            synchronized (this) {
                keyName = (String) redisTemplate.opsForValue().get(key);
                System.out.println("第二次从缓存中获取：" + keyName);
                if (StringUtils.isBlank(keyName)) {
                    // 缓存为空，从数据库查询所有用户数据
                    User user = userService.getUserByName(key);
                    if (user != null && StringUtils.isNotBlank(user.getNickname())) {
                        // 把数据库查询出来的数据放入redis
                        redisTemplate.opsForValue().set(key, user.getNickname());
                    } else {
                        System.out.println("发生缓存穿透");
                    }
                }
            }
        } else {
            System.out.println("缓存中拿到数据：" + keyName + "，不会直接去访问数据库");
        }
        return keyName;
    }

    /**
     * 模拟 100个线程 同时访问登陆的方法  CyclicBarrier 用到JDK里这个的工具类
     */
    private void createThread(String key) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(100);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        System.out.println("模拟100个进程");
        for (int i = 0; i < 100; i++) {
            executorService.execute(() -> {
                getMyUserName(key);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
    }

    private void init() {
        System.out.println("初始化，从数据库里拿到的数据加载到布隆过滤器");
        // 从数据库查询所有用户数据
        List<User> list = userService.getUserList();
        // 从数据库里拿到的数据加载到布隆过滤器
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), list.size());
        // 把数据放到布隆过滤器里
        for (User user : list) {
            bloomFilter.put(user.getNickname());
        }
    }
}