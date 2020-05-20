package com.shop.seckill.service;

import com.shop.seckill.entity.Order;
import com.shop.seckill.entity.User;
import com.shop.seckill.vo.GoodsVo;

public interface SeckillService {
    Order seckill(User user, GoodsVo goods);
    long getSeckillResult(long userId, long goodsId);
}
