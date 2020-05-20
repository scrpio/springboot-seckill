package com.shop.seckill.service;

import com.shop.seckill.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
public interface GoodsService extends IService<Goods> {
    /**
     * 查询商品列表
     *
     * @return
     */
    List<GoodsVo> listGoodsVo();

    /**
     * 根据id查询指定商品
     *
     * @param goodsId
     * @return
     */
    GoodsVo getGoodsVoByGoodsId(long goodsId);

    /**
     * 减少库存，每次减一
     *
     * @param goods
     * @return
     */
    boolean reduceStock(GoodsVo goods);

}
