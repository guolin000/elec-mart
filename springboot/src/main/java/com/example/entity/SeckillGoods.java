package com.example.entity;

import java.util.Date;

public class SeckillGoods {
    private Long seckillId;

    private Integer goodsId;

    private Double seckillPrice;

    private Integer seckillNum;

    private Boolean seckillStatus;

    private Date seckillBegin;

    private Date seckillEnd;

    private Date createTime;

    private Date updateTime;

    /** ID */
    private Integer id;
    /** 商品名称 */
    private String name;
    /** 商品描述 */
    private String description;
    /** 商品主图 */
    private String img;
    private Double price;
    private String unit;
    private Integer count;
    private Integer typeId;
    private Integer businessId;

    private String typeName;
    private String businessName;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
