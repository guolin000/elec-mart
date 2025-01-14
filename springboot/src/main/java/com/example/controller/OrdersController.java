package com.example.controller;

import com.example.common.Result;
import com.example.entity.Orders;
import com.example.service.OrdersService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 购物车前端操作接口
 **/
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) {
        String orderNo= ordersService.add(orders);
        return Result.success(orderNo);
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        ordersService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        ordersService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Orders orders = ordersService.selectById(id);
        return Result.success(orders);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Orders orders ) {
        List<Orders> list = ordersService.selectAll(orders);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Orders orders,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Orders> page = ordersService.selectPage(orders, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 查询指定日期范围内的订单总数和总额
     * @param startDate 起始日期，格式：yyyy-MM-dd
     * @param endDate 结束日期，格式：yyyy-MM-dd
     * @return 指定格式的订单数据
     */
    @GetMapping("/getOrderData")
    public Map<String, Object> getOrderData(
            @RequestParam("merchantId") Integer merchantId,  // 商家 ID
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        // 转换日期为 Date 对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start;
        Date end;
        try {
            start = sdf.parse(startDate);
            end = sdf.parse(endDate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
        }

        // 用来保存结果的数据
        List<String> dates = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        List<Double> sales = new ArrayList<>();

        // 遍历指定日期范围的每一天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String currentDate = sdf.format(calendar.getTime());

            // 从数据库中获取某商家在当前日期的订单数据
            int orderCount = ordersService.getOrderCountByMerchantIdAndDate(merchantId,currentDate);
            double totalSales = ordersService.getAmountByMerchantIdAndDate(merchantId,currentDate);


            // 添加数据到结果列表
            dates.add(currentDate);
            orders.add(orderCount);
            sales.add(totalSales);

            // 移动到下一天
            calendar.add(Calendar.DATE, 1);
        }
        for(int p = 0; p < dates.size();++p){
            System.out.println(dates.get(p) + "   " + orders.get(p));
        }
        // 构造返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("dates", dates);
        response.put("orders", orders);
        response.put("sales", sales);

        return response;
    }
    /**
     * 查询指定日期范围内的订单总数和总额
     * @param startDate 起始日期，格式：yyyy-MM-dd
     * @param endDate 结束日期，格式：yyyy-MM-dd
     * @return 指定格式的订单数据
     */
    @GetMapping("/getOrderTotalData")
    public Map<String, Object> getOrderTotalData(
            @RequestParam("merchantId") Integer merchantId,  // 商家 ID
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        // 获取指定日期范围内的订单总数
        int totalOrderCount = ordersService.getTotalOrderCountByMerchantIdAndDate(merchantId, startDate, endDate);

        // 获取指定日期范围内的订单总额
        Double totalAmount = ordersService.getTotalAmountByMerchantIdAndDate(merchantId, startDate, endDate);

        // 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("totalOrderCount", totalOrderCount);
        response.put("totalAmount", totalAmount);

        return response;
    }
}