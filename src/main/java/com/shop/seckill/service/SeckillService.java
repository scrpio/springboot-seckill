package com.shop.seckill.service;

import com.shop.seckill.entity.Order;
import com.shop.seckill.entity.User;
import com.shop.seckill.vo.GoodsVo;

public interface SeckillService {
    /**
     * 开始秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);

    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return
     */
    long getSeckillResult(long userId, long goodsId);
}
