package com.shop.seckill.dao;

import com.shop.seckill.entity.GoodsSecKill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
@Mapper
public interface GoodsSecKillMapper extends BaseMapper<GoodsSecKill> {

    /**
     * stock_count > 0 和 版本号实现乐观锁 防止超卖
     *
     * @param goodsSeckill
     * @return
     */
    int reduceStockByVersion(GoodsSecKill goodsSeckill);
}
