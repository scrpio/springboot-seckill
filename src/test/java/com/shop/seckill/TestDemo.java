package com.shop.seckill;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.shop.seckill.dao.UserMapper;
import com.shop.seckill.entity.User;
import com.shop.seckill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
public class TestDemo {
    @Resource
    private static UserService userService;

    private String getUserById(String keyId) {
        String id = "";
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        id = (String) redisTemplate.opsForValue().get(keyId);
        // 双层检测锁
        if (StringUtils.isBlank(id)) {
            // 双层加锁
            synchronized (this) {
                id = (String) redisTemplate.opsForValue().get(keyId);
                if (StringUtils.isBlank(id)) {
                    // 缓存为空，从数据库查询所有用户数据
                    User user = userService.getUserById(Long.parseLong(keyId));
                    if (user != null && StringUtils.isNotBlank(String.valueOf(user.getId()))) {
                        // 把数据库查询出来的数据放入redis
                        redisTemplate.opsForValue().set(keyId, user.getId());
                    } else {
                        System.out.println("发生缓存穿透");
                    }
                }
            }
        } else {
            System.out.println("缓存中拿到数据，不会直接去访问数据库");
            System.out.println(id);
        }
        return id;
    }

    private void createThread(String key) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(100);
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            threadPool.execute(() -> {
                getUserById(key);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Test
    public void redisTest() {
        // 从数据库查询所有用户数据
        List<User> list = userService.getUserList();
        // 从数据库里拿到的数据加载到布隆过滤器
        BloomFilter bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), list.size());
        // 把数据放到布隆过滤器里
        for (User user : list) {
            bloomFilter.put(user.getId());
        }
        String keyId = UUID.randomUUID().toString();
        TestDemo testDemo = new TestDemo();
        if (bloomFilter.mightContain(keyId)) {
            testDemo.createThread(keyId);
        } else {
            log.debug("不存在该账号！");
        }
    }

    @Test
    public void queryUserList() {
        List<User> list = userService.getUserList();
        System.out.println(list);
    }
}
