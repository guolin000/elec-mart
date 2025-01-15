package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;

import com.example.mapper.GoodsMapper;
import com.example.mapper.SeckillCartMapper;
import com.example.mapper.SeckillMapper;
import com.example.mapper.SeckillOrdersMapper;
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
public class SeckillOrdersService {

    @Resource
    private SeckillOrdersMapper seckillordersMapper;
    @Resource
    private SeckillCartMapper seckillcartMapper;
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 新增
     */
    public String add(SeckillOrders orders) {
        orders.setOrderId(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        for (SeckillCart cart : orders.getCartData()) {
            SeckillOrders dbOrders = new SeckillOrders();
            BeanUtils.copyProperties(orders, dbOrders);
            dbOrders.setGoodsId(cart.getGoodsId());
            dbOrders.setBusinessId(cart.getBusinessId());
            dbOrders.setNum(cart.getNum());
            dbOrders.setPrice(cart.getNum() * cart.getGoodsPrice());
            seckillordersMapper.insert(dbOrders);
            // 把购物车里对应的商品删掉
            seckillcartMapper.deleteById(cart.getId());
            System.out.println(cart.getId());
//            System.out.println("删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车删除购物车");
            // 把商品销量增加一下
            Goods goods = goodsMapper.selectById(cart.getGoodsId());
            goods.setCount(goods.getCount() + cart.getNum());
            goodsMapper.updateById(goods);
        }

        return orders.getOrderId();
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        seckillordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            seckillordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(SeckillOrders orders) {
        seckillordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public SeckillOrders selectById(Integer id) {
        return seckillordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<SeckillOrders> selectAll(SeckillOrders orders) {
        return seckillordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<SeckillOrders> selectPage(SeckillOrders orders, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            orders.setUserId(currentUser.getId());
        }
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            orders.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillOrders> list = seckillordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

    /**
     * 根据商家id和指定日期获取订单总数
     */
    public int getOrderCountByMerchantIdAndDate(Integer merchantId, String date) {
        return seckillordersMapper.selectOrderCountByMerchantIdAndDate(merchantId, date);
    }

    public Double getAmountByMerchantIdAndDate(Integer merchantId, String date) {
        // 假设查询数据库或数据源时，如果没有找到数据，返回 0.0
        Double totalAmount = seckillordersMapper.selectAmountByMerchantIdAndDate(merchantId, date);

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
        return seckillordersMapper.getTotalOrderCountByMerchantIdAndDate(merchantId, startDate, endDate);
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
        Double totalAmount = seckillordersMapper.getTotalAmountByMerchantIdAndDate(merchantId, startDate, endDate);

        // 如果查询结果为 null，返回 0.0
        if (totalAmount == null) {
            totalAmount = 0.0;
        }
        return totalAmount;
    }
}