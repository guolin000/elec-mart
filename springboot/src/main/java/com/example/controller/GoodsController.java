package com.example.controller;

import com.example.common.Result;
import com.example.entity.Goods;
import com.example.service.GoodsService;
import com.example.utils.GoodsToggleBatchDTO;
import com.example.utils.GoodsToggleDTO;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 分类信息表前端操作接口
 **/
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        goodsService.add(goods);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        goodsService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        goodsService.deleteBatch(ids);
        return Result.success();
    }
    /**
     * 上下架
     */
    @PutMapping("/toggle")
    public Result toggleGoods(@RequestBody GoodsToggleDTO dto) {
        goodsService.toggleGoods(dto);
        return Result.success();
    }
    @PutMapping("/toggleUp/batch")
    public Result toggleUpBatch(@RequestBody GoodsToggleBatchDTO dto) {
        goodsService.toggleUpBatch(dto);
        return Result.success();
    }
    /**
     * 审核通过（单个商品）
     */
    @PutMapping("/approve/{id}")
    public Result approve(@PathVariable("id") Integer id) {
        goodsService.approve(id);
        return Result.success("审核通过");
    }

    /**
     * 批量审核通过
     */
    @PutMapping("/approveBatch")
    public Result approveBatch(@RequestBody List<Integer> ids) {
        goodsService.approveBatch(ids);
        return Result.success("批量审核通过");
    }

    /**
     * 审核拒绝（单个商品）
     */
    @PutMapping("/reject/{id}")
    public Result reject(@PathVariable("id") Integer id) {
        goodsService.reject(id);
        return Result.success("审核已拒绝");
    }

    /**
     * 批量审核拒绝
     */
    @PutMapping("/rejectBatch")
    public Result rejectBatch(@RequestBody List<Integer> ids) {
        goodsService.rejectBatch(ids);
        return Result.success("批量审核拒绝成功");
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Goods goods) {
        goodsService.updateById(goods);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById")
    public Result selectById(@RequestParam Integer id) {
        Goods goods = goodsService.selectById(id);
        return Result.success(goods);
    }

    @GetMapping("/selectTop15")
    public Result selectTop15() {
        List<Goods> list = goodsService.selectTop15();
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Goods goods ) {
        List<Goods> list = goodsService.selectAll(goods);
        return Result.success(list);
    }

    @GetMapping("/selectByTypeId")
    public Result selectByTypeId(@RequestParam Integer id) {
        List<Goods> list = goodsService.selectByTypeId(id);
        return Result.success(list);
    }

    @GetMapping("/selectByName")
    public Result selectByName(@RequestParam String name) {
        List<Goods> list = goodsService.selectByName(name);
        return Result.success(list);
    }

    @GetMapping("/selectByBusinessId")
    public Result selectByBusinessId(@RequestParam Integer id) {
        List<Goods> list = goodsService.selectByBusinessId(id);
        return Result.success(list);
    }

    /**
     * 基于用户的协同过滤算法
     * @return
     */
    @GetMapping("/recommend")
    public Result recommend() {
        List<Goods> list = goodsService.recommend();
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Goods goods,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Goods> page = goodsService.selectPage(goods, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取商品信息
     */
    @GetMapping("/getGoods")
    public Result getGoods(@RequestParam Integer Id){
        System.out.println("businessId" + Id);
        Map<String, Long> stats = goodsService.getGoodStats(Id);
//        System.out.println(stats);
        return Result.success(stats);
    }
}