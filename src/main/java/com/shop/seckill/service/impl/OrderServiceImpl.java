package com.shop.seckill.service.impl;

import com.shop.seckill.dao.OrderMapper;
import com.shop.seckill.entity.Order;
import com.shop.seckill.entity.OrderSecKill;
import com.shop.seckill.dao.OrderSecKillMapper;
import com.shop.seckill.entity.User;
import com.shop.seckill.redis.OrderKey;
import com.shop.seckill.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.seckill.utils.RedisUtil;
import com.shop.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderSecKillMapper, OrderSecKill> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderSecKillMapper orderSecKillMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public OrderSecKill getOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisUtil.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, OrderSecKill.class);
    }

    @Override
    public Order getOrderById(long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public Order createOrder(User user, GoodsVo goods) {
        Order order = new Order();
        order.setCreateDate(new Date());
        order.setDeliveryAddrId(0L);
        order.setGoodsCount(1);
        order.setGoodsId(goods.getId());
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsPrice(goods.getGoodsPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setUserId(user.getId());
        orderMapper.insert(order);

        OrderSecKill orderSecKill = new OrderSecKill();
        orderSecKill.setGoodsId(goods.getId());
        orderSecKill.setOrderId(order.getId());
        orderSecKill.setUserId(user.getId());
        orderSecKillMapper.insert(orderSecKill);

        redisUtil.set(OrderKey.getSeckillOrderByUidGid, "" + user.getId() + "_" + goods.getId(), orderSecKill);

        return order;
    }
}
