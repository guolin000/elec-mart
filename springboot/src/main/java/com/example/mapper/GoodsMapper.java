package com.example.mapper;

import com.example.entity.Goods;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 操作goods相关数据接口
*/
public interface GoodsMapper {

    /**
      * 新增
    */
    int insert(Goods goods);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Goods goods);

    /**
     * 根据id将数据库表中的goods_up字段改为“true"
     */
    int toggleById(Integer id);

    /**
      * 根据ID查询
    */
    Goods selectById(Integer id);

    /**
      * 查询所有
    */
    List<Goods> selectAll(Goods goods);

    @Select("select * from goods order by count desc limit 15")
    List<Goods> selectTop15();

    @Select("select * from goods where type_id = #{id}")
    List<Goods> selectByTypeId(Integer id);

    @Select("select * from goods where business_id = #{id}")
    List<Goods> selectByBusinessId(Integer id);

    @Select("select * from goods where name like concat('%', #{name}, '%')")
    List<Goods> selectByName(String name);

    @Update("UPDATE goods SET goods_up = #{goodsUp} WHERE id = #{id}")
    int updateGoodsUpById(@Param("id") Integer id, @Param("goodsUp") String goodsUp);

    @Update("<script>" +
            "UPDATE goods SET goods_up = #{goodsUp} " +
            "WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int updateGoodsUpByIds(@Param("ids") List<Integer> ids, @Param("goodsUp") String goodsUp);
}