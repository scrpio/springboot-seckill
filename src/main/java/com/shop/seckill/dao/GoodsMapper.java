package com.shop.seckill.dao;

import com.shop.seckill.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> getGoodsVoList();

    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);
}
