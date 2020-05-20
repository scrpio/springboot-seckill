package com.shop.seckill.service;

import com.shop.seckill.entity.Order;
import com.shop.seckill.entity.OrderSecKill;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.seckill.entity.User;
import com.shop.seckill.vo.GoodsVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
public interface OrderService extends IService<OrderSecKill> {
    OrderSecKill getOrderByUserIdGoodsId(long userId, long goodsId);

    Order getOrderById(long orderId);

    /**
     * 因为要同时分别在订单详情表和秒杀订单表都新增一条数据，所以要保证两个操作是一个事物
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    Order createOrder(User user, GoodsVo goods);
}
