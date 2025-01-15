package com.example.controller;

import com.example.common.Result;
import com.example.entity.SeckillCart;
import com.example.service.SeckillCartService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 购物车前端操作接口
 **/
@RestController
@RequestMapping("/seckillcart")
public class SeckillCartController {

    @Autowired
    @Qualifier("seckillCartService")
    private SeckillCartService cartService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody SeckillCart cart) {
        cartService.add(cart);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        cartService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        cartService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody SeckillCart cart) {
        cartService.updateById(cart);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        SeckillCart cart = cartService.selectById(id);
        return Result.success(cart);
    }

    /**
     * 根据商品ID查询
     */
    @GetMapping("/selectByGoodsId")
    public Result selectByGoodsId(@RequestParam Integer goodsId) {
        List<SeckillCart> list = cartService.selectByGoodsId(goodsId);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(SeckillCart cart ) {
        List<SeckillCart> list = cartService.selectAll(cart);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(SeckillCart cart,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<SeckillCart> page = cartService.selectPage(cart, pageNum, pageSize);
        return Result.success(page);
    }

}