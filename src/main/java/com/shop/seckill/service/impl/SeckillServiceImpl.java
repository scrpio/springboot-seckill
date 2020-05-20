package com.shop.seckill.service.impl;

import com.shop.seckill.entity.Order;
import com.shop.seckill.entity.OrderSecKill;
import com.shop.seckill.entity.User;
import com.shop.seckill.redis.SeckillKey;
import com.shop.seckill.service.GoodsService;
import com.shop.seckill.service.OrderService;
import com.shop.seckill.service.SeckillService;
import com.shop.seckill.utils.RedisUtils;
import com.shop.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Order seckill(User user, GoodsVo goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            //下订单 写入秒杀订单
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    @Override
    public long getSeckillResult(long userId, long goodsId) {
        OrderSecKill order = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisUtils.set(SeckillKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisUtils.exists(SeckillKey.isGoodsOver, "" + goodsId);
    }
}
