package com.shop.seckill.dao;

import com.shop.seckill.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
