package com.example.common;

public interface Constants {

    String TOKEN = "token";

    String USER_DEFAULT_PASSWORD = "123456";

    /**
     * 秒杀下单盐值
     */
    public static final String SECKILL_ORDER_SALT = "asdk23423@jjjas";

    public static final String REDIS_KEY_PREFIX = "newbee-mart-plus:";

    /**
     * 秒杀商品库存缓存
     */
    public static final String SECKILL_GOODS_STOCK_KEY = REDIS_KEY_PREFIX + "seckill_goods_stock:";

    /**
     * 秒杀商品缓存
     */
    public static final String SECKILL_KEY = REDIS_KEY_PREFIX + "seckill:";
    /**
     * 秒杀商品详情页面缓存
     */
    public static final String SECKILL_GOODS_DETAIL = REDIS_KEY_PREFIX + "seckill_goods_detail:";
    /**
     * 秒杀商品列表页面缓存
     */
    public static final String SECKILL_GOODS_LIST = REDIS_KEY_PREFIX + "seckill_goods_list";

    /**
     * 秒杀成功的用户set缓存
     */
    public static final String SECKILL_SUCCESS_USER_ID = REDIS_KEY_PREFIX + "seckill_success_user_id:";

}
