package com.example.mapper;

import com.example.entity.Seckill;
import com.example.entity.SeckillGoods;
import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SeckillMapper {

    int insert(Seckill seckill);

    int delete(Integer id);


    // 根据秒杀ID获取秒杀商品信息
    SeckillGoods getSeckillBySeckillId(Long seckillId);

    Seckill selectById(Integer id);

    // 更新秒杀商品库存（使用seckillNum字段）
    void updateStock(Long seckillId, Integer seckillNum);

    // 根据秒杀ID查询秒杀商品
    @Select("SELECT * FROM seckill WHERE seckill_id = #{seckillId}")
    Seckill getSeckillById(Long seckillId);

    // 获取所有秒杀商品
    @Select("SELECT * FROM seckill")
    List<Seckill> getAllSeckillItems();

    List<SeckillGoods> getAllSeckillItemsWithGoods();

    // 根据秒杀状态查询秒杀商品
    @Select("SELECT * FROM seckill WHERE seckill_status = #{seckillStatus}")
    List<Seckill> getSeckillByStatus(Boolean seckillStatus);

}
