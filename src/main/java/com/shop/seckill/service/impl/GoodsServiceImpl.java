package com.shop.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.seckill.dao.GoodsSecKillMapper;
import com.shop.seckill.entity.Goods;
import com.shop.seckill.dao.GoodsMapper;
import com.shop.seckill.entity.GoodsSecKill;
import com.shop.seckill.service.GoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author scorpio
 * @since 2020-05-19
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSecKillMapper goodsSecKillMapper;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.getGoodsVoList();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo goods) {
        int numAttempts = 0;
        int ret = 0;
        GoodsSecKill sg = new GoodsSecKill();
        sg.setGoodsId(goods.getId());
        sg.setVersion(goods.getVersion());
        do {
            numAttempts++;
            try {
                QueryWrapper<GoodsSecKill> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("goods_id", goods.getId());
                sg.setVersion(goodsSecKillMapper.selectOne(queryWrapper).getVersion());
                ret = goodsSecKillMapper.reduceStockByVersion(sg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ret != 0)
                break;
        } while (numAttempts < DEFAULT_MAX_RETRIES);

        return ret > 0;
    }
}
