package com.example.mapper;

import com.example.entity.SeckillCart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作cart相关数据接口
 */
public interface SeckillCartMapper {

    /**
     * 新增
     */
    int insert(SeckillCart seckillcart);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(SeckillCart seckillcart);

    /**
     * 根据ID查询
     */
    SeckillCart selectById(Integer id);

    /**
     * 根据商品ID查询
     */
    List<SeckillCart> selectByGoodsId(Integer goodsId);

    /**
     * 查询所有
     */
    List<SeckillCart> selectAll(SeckillCart seckillcart);

    @Select("select * from seckillcart where user_id = #{userId} and goods_id = #{goodsId}")
    SeckillCart selectByUserIdAndGoodsId(@Param("userId") Integer userId, @Param("goodsId") Integer goodsId);
}