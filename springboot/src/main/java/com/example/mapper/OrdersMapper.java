package com.example.mapper;

import com.example.entity.Orders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作orders相关数据接口
*/
public interface OrdersMapper {

    /**
      * 新增
    */
    int insert(Orders orders);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Orders orders);

    /**
      * 根据ID查询
    */
    Orders selectById(Integer id);

    /**
      * 查询所有
    */
    List<Orders> selectAll(Orders orders);

    @Select("select * from orders where status = '已完成' or status = '已评价'")
    List<Orders> selectAllOKOrders();
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

    @Select("select * from orders where order_id = #{orderNo}")
    List<Orders> selectByOrderNo(String orderNo);

}