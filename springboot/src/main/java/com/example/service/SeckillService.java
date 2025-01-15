package com.example.service;

import cn.hutool.core.date.DateTime;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Goods;
import com.example.entity.Seckill;
import com.example.entity.SeckillGoods;
import com.example.mapper.SeckillMapper;
import com.example.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeckillService {

    @Autowired
    private SeckillMapper seckillMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final String PRODUCT_PREFIX = "seckill:product:";  // Redis key 前缀
    private final String LOCK_PREFIX = "seckill:lock:";        // 锁的前缀

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 设置 key 和 value 的序列化器
        template.setKeySerializer(new StringRedisSerializer());  // key 使用 StringRedisSerializer
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));  // value 使用 GenericToStringSerializer

        template.afterPropertiesSet();
        return template;
    }

    // 秒杀商品初始化库存的方法
    @PostConstruct
    public void initSeckillStock() {
        // 获取所有秒杀商品
        List<Seckill> seckillList = seckillMapper.getAllSeckillItems();

        // 遍历所有商品，并将库存初始化到 Redis
        for (Seckill seckill : seckillList) {
            // 获取每个商品的秒杀商品信息
            SeckillGoods seckillGoods = seckillMapper.getSeckillBySeckillId(seckill.getSeckillId());

            if (seckillGoods != null && seckillGoods.getSeckillNum() != null) {
                // 将商品库存放入 Redis
                String redisKey = PRODUCT_PREFIX + seckill.getSeckillId() + ":seckillNum";
                redisTemplate.opsForValue().set(redisKey, seckillGoods.getSeckillNum());
                System.out.println("初始化秒杀商品 " + seckill.getSeckillId() + " 库存为 " + seckillGoods.getSeckillNum());
            }
        }
    }

    // 秒杀业务逻辑
    public String seckill(Long seckillId, Integer userId) {
        initSeckillStock();
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("测试：" + currentTime);
        // 获取秒杀商品
        SeckillGoods seckill = seckillMapper.getSeckillBySeckillId(seckillId);
        if (seckill == null) {
            return "商品不存在";
        }
        // 校验秒杀状态：只有商品上架且在秒杀时间内才能进行秒杀
        if (!seckill.getSeckillStatus()) {
            return "商品未上架，无法秒杀";
        }
        // 校验秒杀时间是否有效
        if (currentTime.isBefore(seckill.getSeckillBegin().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())) {
            return "秒杀尚未开始";
        }
        if (currentTime.isAfter(seckill.getSeckillEnd().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())) {
            return "秒杀已结束";
        }

        // 使用分布式锁防止并发超卖
        String lockKey = LOCK_PREFIX + seckillId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED");
        if (locked == null || !locked) {
            return "系统繁忙，请稍后再试";
        }

        try {
            // 获取缓存中的库存（使用seckillNum字段作为库存）
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

            String stockString = (String) valueOperations.get(PRODUCT_PREFIX + seckillId + ":seckillNum");

            // 将 stockString 转换为 Integer
            Integer stock = (stockString != null) ? Integer.valueOf(stockString) : 0;

            if (stock == null || stock <= 0) {
                return "库存不足";
            }

            // 使用 decrement 扣减库存，这样 Redis 会直接操作整数值
            redisTemplate.opsForValue().decrement(PRODUCT_PREFIX + seckillId + ":seckillNum", 1);

            //Integer stock = (Integer) valueOperations.get(PRODUCT_PREFIX + seckillId + ":seckillNum");
            //System.out.println("测试：" + stock);

            //if (stock == null || stock <= 0) {
                //return "库存不足";
            //}
            // 扣减库存
            //String redisKey = PRODUCT_PREFIX + seckill.getSeckillId() + ":seckillNum";
            //redisTemplate.opsForValue().set(redisKey, stock-1);
            // 扣减库存（原子操作）
            //redisTemplate.opsForValue().decrement(redisKey, 1);
            //redisTemplate.opsForValue().decrement(PRODUCT_PREFIX + seckillId + ":seckillNum", 1);

            // 更新数据库库存
            int newStock = stock - 1;
            seckillMapper.updateStock(seckillId, newStock);

            // 生成秒杀订单
            // 这里可以根据需要生成订单逻辑
            // ...

            return "秒杀成功";
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    public String addSeckillProducts(Integer goodsId, DateTime seckillBegin, DateTime seckillEnd, Double seckillPrice, Integer seckillNum) {

        Seckill seckill = new Seckill();
        if (seckillMapper.selectById(goodsId) != null) {
            String res = "已上架秒杀！若要修改请先下架";
            return res;
        }
        seckill.setGoodsId(goodsId);
        seckill.setSeckillBegin(seckillBegin);
        seckill.setSeckillEnd(seckillEnd);
        seckill.setSeckillPrice(seckillPrice);
        seckill.setSeckillNum(seckillNum);
        // 执行插入操作
        seckillMapper.insert(seckill);
        return "上架秒杀成功!";
    }

    public String deleteById(Integer id) {

        if (seckillMapper.selectById(id) == null) {
            return "该商品还未上架秒杀！";
        }
        seckillMapper.delete(id);
        return "下架秒杀成功！";
    }

    // 获取所有秒杀商品
    public List<Seckill> getAllSeckillProducts() {
        return seckillMapper.getAllSeckillItems();
    }

    // 根据秒杀ID获取秒杀商品详细信息
    public SeckillGoods getSeckillById(Long seckillId) {
        return seckillMapper.getSeckillBySeckillId(seckillId);
    }

    public List<SeckillGoods> getAllSeckillItemsWithGoods() {
        return seckillMapper.getAllSeckillItemsWithGoods();
    }
}
