package com.shop.seckill.vo;

import com.shop.seckill.entity.Goods;

import java.util.Date;

public class GoodsVo extends Goods {
    private Double seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    private Integer version;

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}

