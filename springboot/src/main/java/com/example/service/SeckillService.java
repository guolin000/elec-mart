package com.example.service;

import com.example.entity.Seckill;
import com.example.entity.SeckillGoods;
import com.example.mapper.SeckillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 秒杀业务逻辑
    @Transactional
    public String seckill(Long seckillId, Integer userId) {
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("测试："+currentTime);
        // 获取秒杀商品
        SeckillGoods seckill = seckillMapper.getSeckillBySeckillId(seckillId);
        if (seckill == null) {
            return "商品不存在";
        }
        // 校验秒杀状态：只有商品上架且在秒杀时间内才能进行秒杀
        if (seckill.getSeckillStatus() == false) {
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
            Integer stock = (Integer) valueOperations.get(PRODUCT_PREFIX + seckillId + ":seckillNum");
            if (stock == null || stock <= 0) {
                return "库存不足";
            }

            // 扣减库存
            redisTemplate.opsForValue().decrement(PRODUCT_PREFIX + seckillId + ":seckillNum", 1);

            // 更新数据库库存
            int newStock = stock - 1;
            seckillMapper.updateStock(seckillId, newStock);

            // 生成秒杀订单
            // 这里可以根据需要生成订单逻辑
            // ...

            return "200";
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
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
