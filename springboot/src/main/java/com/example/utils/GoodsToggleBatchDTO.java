package com.example.utils;

import java.util.List;

public class GoodsToggleBatchDTO {
    private List<Integer> ids;
    private String goodsUp;

    // Getters and Setters
    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public String getGoodsUp() {
        return goodsUp;
    }

    public void setGoodsUp(String goodsUp) {
        this.goodsUp = goodsUp;
    }
}