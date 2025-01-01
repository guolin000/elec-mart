package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Cart;
import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.mapper.CartMapper;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrdersMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.relation.Role;
import java.util.Date;
import java.util.List;

/**
 * 收藏业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 新增
     */
    public void add(Orders orders) {
        orders.setOrderId(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        for (Cart cart : orders.getCartData()) {
            Orders dbOrders = new Orders();
            BeanUtils.copyProperties(orders, dbOrders);
            dbOrders.setGoodsId(cart.getGoodsId());
            dbOrders.setBusinessId(cart.getBusinessId());
            dbOrders.setNum(cart.getNum());
            dbOrders.setPrice(cart.getNum() * cart.getGoodsPrice());
            ordersMapper.insert(dbOrders);

            // 把购物车里对应的商品删掉
            cartMapper.deleteById(cart.getId());
            System.out.println(cart.getId());
            System.out.println("删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车");
            // 把商品销量增加一下
            Goods goods = goodsMapper.selectById(cart.getGoodsId());
            goods.setCount(goods.getCount() + cart.getNum());
            goodsMapper.updateById(goods);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            orders.setUserId(currentUser.getId());
        }
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            orders.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

    /**
     * 根据商家id和指定日期获取订单总数
     */
    public int getOrderCountByMerchantIdAndDate(Integer merchantId, String date) {
        return ordersMapper.selectOrderCountByMerchantIdAndDate(merchantId, date);
    }

    public Double getAmountByMerchantIdAndDate(Integer merchantId, String date) {
        // 假设查询数据库或数据源时，如果没有找到数据，返回 0.0
        Double totalAmount = ordersMapper.selectAmountByMerchantIdAndDate(merchantId, date);

        // 如果查询结果为 null，返回 0.0
        if (totalAmount == null) {
            totalAmount = 0.0;
        }
        return totalAmount;
    }

    /**
     * 根据商家id和指定日期范围内的订单总数
     * @param merchantId 商家id
     * @param startDate 起始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 订单总数
     */
    public int getTotalOrderCountByMerchantIdAndDate(Integer merchantId, String startDate, String endDate) {
        return ordersMapper.getTotalOrderCountByMerchantIdAndDate(merchantId, startDate, endDate);
    }

    /**
     * 根据商家id和指定日期范围内的订单总额
     * @param merchantId 商家id
     * @param startDate 起始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 订单总额
     */
    public Double getTotalAmountByMerchantIdAndDate(Integer merchantId, String startDate, String endDate) {
        // 获取订单总额
        Double totalAmount = ordersMapper.getTotalAmountByMerchantIdAndDate(merchantId, startDate, endDate);

        // 如果查询结果为 null，返回 0.0
        if (totalAmount == null) {
            totalAmount = 0.0;
        }
        return totalAmount;
    }
}