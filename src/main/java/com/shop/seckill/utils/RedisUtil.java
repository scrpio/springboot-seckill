package com.shop.seckill.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.hash.BloomFilter;
import com.shop.seckill.entity.User;
import com.shop.seckill.redis.BloomFilterHelper;
import com.shop.seckill.redis.KeyPrefix;
import com.shop.seckill.redis.UserKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author scorpio
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ValueOperations<String, String> valueOperations;
    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    public static final String KEY_MUTEX = "mutex-key:";

    /**
     * 获取redis实例
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        // 对key增加前缀，即可用于分类，也避免key重复
        String realKey = prefix.getPrefix() + key;
        String str = valueOperations.get(realKey);
        return stringToBean(str, clazz);
    }

    /**
     * 存储对象
     */
    public <T> Boolean set(KeyPrefix prefix, String key, T value) {
        String str = beanToString(value);
        if (str == null || str.length() <= 0) {
            return false;
        }
        String realKey = prefix.getPrefix() + key;
        // 获取过期时间
        int seconds = prefix.expireSeconds();
        if (seconds <= 0) {
            valueOperations.set(realKey, str);
        } else {
            valueOperations.setIfAbsent(realKey, str, Duration.ofDays(seconds));
        }
        return true;
    }

    /**
     * 删除
     */
    public boolean delete(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.delete(realKey);
    }

    /**
     * 判断key是否存在
     */
    public <T> boolean exists(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        return redisTemplate.hasKey(realKey);
    }

    /**
     * 增加值
     * Redis Incr 命令将 key 中储存的数字值增一
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        return valueOperations.increment(realKey);
    }

    /**
     * 减少值
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
        //生成真正的key
        String realKey = prefix.getPrefix() + key;
        return valueOperations.decrement(realKey);
    }

    /**
     * Object转成JSON数据
     */
    public <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return String.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return String.valueOf(value);
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }

    }

    /**
     * JSON数据，转成Object
     */
    public <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 缓存穿透：缓存和数据库中都没有的数据，而用户不断发起请求，导致数据库压力过大，严重会击垮数据库。
     * 解决方案：
     * 1）对查询结果为空的情况也进行缓存，缓存时间设置短一点。
     * 2）布隆过滤，对所有可能查询的参数以hash形式存储，在控制层先进行校验，不符合则丢弃，从而避免了对底层存储系统的查询压力。
     */
    public String getValue(String key) {
        // 对查询结果为空的情况也进行缓存
        String value = valueOperations.get(key);
        if (value != null) {
            return value;
        } else {
            // 数据库查询
            // value = db.getValue;
            // if (value == null){
            //      value = string.Empty;
            // }
            // 如果发现为空，设置个默认值，也缓存起来
            valueOperations.set(key, value, 3000);
        }
        return value;
    }

    public <T> void addByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        // 根据给定的布隆过滤器添加值
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            redisTemplate.opsForValue().setBit(key, i, true);
        }
    }

    public <T> boolean includeByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        // 根据给定的布隆过滤器判断值是否存在
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            if (!redisTemplate.opsForValue().getBit(key, i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 缓存击穿：一个key对应的数据是个热点数据，每秒都有大量的请求访问他，当这个Key在失效的瞬间，持续的大并发就穿破缓存，直接请求数据库，可能会瞬间把数据库压垮。
     * 解决方案：
     * 1）设置热点数据永远不过期。缺点: 占空间，内存消耗大，不能保持数据最新
     * 2）加互斥锁(mutex key)。缺点: 代码复杂度增大，存在死锁和线程池阻塞的风险
     */
    public void persistKey(String key) {
        // 设置key永远不过期
        redisTemplate.persist(key);
    }

    public String get(String key) throws InterruptedException {
        // 加互斥锁
        String value = valueOperations.get(key);
        if (value == null) {
            if (valueOperations.setIfAbsent(KEY_MUTEX, value, Duration.ofDays(2))) {
                // value = db.getValue;
                valueOperations.set(key, value, Duration.ofDays(2));
                redisTemplate.delete(KEY_MUTEX);
                return value;
            } else {
                Thread.sleep(50);
                value = valueOperations.get(key);
            }
        }
        return value;
    }

    /**
     * 缓存雪崩：当缓存服务器重启或者大量缓存集中在某一个时间段失效，这样在失效的时候，会给后端系统带来很大压力，导致系统崩溃。
     * 解决方案：
     * 1）在缓存失效后，通过加锁或者队列来控制读数据库写缓存的线程数量。比如对某个key只允许一个线程查询数据和写缓存，其他线程等待。
     * 单机的话，可以使用synchronized或者lock来解决，如果是分布式环境，可以是用redis的setnx命令来解决。
     * 2）设置不同的过期时间，让缓存失效的时间点尽量均匀。
     */
    public String getV(String key) {
        String value = valueOperations.get(key);
        BloomFilterHelper<String> bloomFilterHelper = null;
        if (includeByBloomFilter(bloomFilterHelper, key, value)) {
            if (value != null) {
                return value;
            } else {
                // 双层检测加锁
                synchronized (this) {
                    value = valueOperations.get(key);
                    if (value == null) {
                        // 取数据库
                        // value = db.getValue;
                        if (value != null) {
                            // 把数据库查询出来的数据放入redis
                            valueOperations.set(key, value);
                        } else {
                            System.out.println("发生缓存穿透");
                        }
                    }
                }
            }
        } else {
            System.out.println("该用户不存在！");
        }
        return value;
    }

    public void expireKey(String key, long time, TimeUnit timeUnit) {
        // 设置key的生命周期
        redisTemplate.expire(key, time, timeUnit);
    }

    public void expireKeyAt(String key, Date date) {
        // 指定key在指定的日期过期
        redisTemplate.expireAt(key, date);
    }

    public long getKeyExpire(String key, TimeUnit timeUnit) {
        // 查询key的生命周期
        return redisTemplate.getExpire(key, timeUnit);
    }

}
