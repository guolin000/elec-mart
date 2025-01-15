package com.example.mapper;

import com.example.entity.SeckillOrders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作orders相关数据接口
 */
public interface SeckillOrdersMapper {

    /**
     * 新增
     */
    int insert(SeckillOrders seckillorders);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(SeckillOrders seckillorders);

    /**
     * 根据ID查询
     */
    SeckillOrders selectById(Integer id);

    /**
     * 查询所有
     */
    List<SeckillOrders> selectAll(SeckillOrders seckillorders);

    @Select("select * from seckillorders where status = '已完成' or status = '已评价'")
    List<SeckillOrders> selectAllOKOrders();
    /**
     * 根据商家id和指定日期查询订单总数
     */
    int selectOrderCountByMerchantIdAndDate(Integer merchantId, String date);
    /**
     * 根据商家id和指定日期查询订单总额
     */
    Double selectAmountByMerchantIdAndDate(Integer merchantId, String date);

    int getTotalOrderCountByMerchantIdAndDate(Integer merchantId, String startDate, String endDate);

    Double getTotalAmountByMerchantIdAndDate(Integer merchantId, String startDate, String endDate);

    /**
     * 根据订单号更新支付状态
     */
    int updateStatusById(int id);

    @Select("select * from seckillorders where order_id = #{orderNo}")
    List<SeckillOrders> selectByOrderNo(String orderNo);

}