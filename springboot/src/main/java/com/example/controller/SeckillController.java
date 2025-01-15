package com.example.controller;

import com.example.common.Result;
import com.example.entity.Seckill;
import com.example.entity.SeckillGoods;
import com.example.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    // 获取所有秒杀商品
    @GetMapping("/products_s")
    public Result getAllSeckillProducts() {
        List<Seckill> res = seckillService.getAllSeckillProducts();
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
