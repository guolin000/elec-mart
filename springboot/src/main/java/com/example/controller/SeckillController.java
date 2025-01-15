package com.example.controller;

import cn.hutool.core.date.DateTime;
import com.example.common.Result;
import com.example.entity.Cart;
import com.example.entity.Seckill;
import com.example.entity.SeckillGoods;
import com.example.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    // 获取所有秒杀商品
    @PostMapping("/add")
    public Result addSeckillProducts(
            @RequestParam("goodsId") Integer goodsId,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") DateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") DateTime endDate,
            @RequestParam("price") Double price,
            @RequestParam("num") Integer num){

        System.out.println("goodsId: " + goodsId);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        String res = seckillService.addSeckillProducts(goodsId, startDate, endDate, price, num);
        return Result.success(res);
    }

    @GetMapping("/delete")
    public Result deleteById(@RequestParam("ids") Integer id) {
        String res = seckillService.deleteById(id);
        return Result.success(res);
    }

    // 获取所有秒杀商品
    @GetMapping("/products_d")
    public Result getAllSeckillItemsWithGoods() {
        List<SeckillGoods> res = seckillService.getAllSeckillItemsWithGoods();
        return Result.success(res);
    }

    // 获取某个秒杀商品的详细信息
    @GetMapping("/product")
    public Result getSeckillById(@RequestParam("id") Long seckillId) {
        SeckillGoods res = seckillService.getSeckillById(seckillId);
        return Result.success(res);
    }

    // 秒杀接口
    @PostMapping("/seckill_p")
    public Result seckill(@RequestParam Long seckillId, @RequestParam Integer userId) {
        String res = seckillService.seckill(seckillId, userId);
        if (res.equals("秒杀成功")){
            return Result.success(res);
        }
        return Result.error("500",res);
    }
}
