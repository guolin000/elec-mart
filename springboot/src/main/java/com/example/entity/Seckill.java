package com.example.entity;

import java.io.Serializable;

import java.util.Date;

public class Seckill implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seckillId;

    private Integer goodsId;

    private Double seckillPrice;

    private Integer seckillNum;

    private Boolean seckillStatus;

    private Date seckillBegin;

    private Date seckillEnd;

    private Date createTime;

    private Date updateTime;

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getSeckillNum() {
        return seckillNum;
    }

    public void setSeckillNum(Integer seckillNum) {
        this.seckillNum = seckillNum;
    }

    public Boolean getSeckillStatus() {
        return seckillStatus;
    }

    public void setSeckillStatus(Boolean seckillStatus) {
        this.seckillStatus = seckillStatus;
    }

    public Date getSeckillBegin() {
        return seckillBegin;
    }

    public void setSeckillBegin(Date seckillBegin) {
        this.seckillBegin = seckillBegin;
    }

    public Date getSeckillEnd() {
        return seckillEnd;
    }

    public void setSeckillEnd(Date seckillEnd) {
        this.seckillEnd = seckillEnd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
