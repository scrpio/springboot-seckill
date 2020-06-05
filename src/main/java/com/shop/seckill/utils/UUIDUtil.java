package com.shop.seckill.utils;

import java.util.UUID;

/**
 * 唯一id生成类
 * @author scorpio
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
