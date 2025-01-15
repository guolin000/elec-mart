package com.example.mapper;

import com.example.entity.SeckillCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作collect相关数据接口
 */
public interface SeckillCollectMapper {

    /**
     * 新增
     */
    int insert(SeckillCollect seckillcollect);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(SeckillCollect seckillcollect);

    /**
     * 根据ID查询
     */
    SeckillCollect selectById(Integer id);

    /**
     * 查询所有
     */
    List<SeckillCollect> selectAll(SeckillCollect seckillcollect);

    @Select("select * from seckillcollect where user_id = #{userId} and goods_id = #{goodsId}")
    SeckillCollect selectByUserIdAndGoodsId(@Param("userId") Integer userId, @Param("goodsId") Integer goodsId);
}