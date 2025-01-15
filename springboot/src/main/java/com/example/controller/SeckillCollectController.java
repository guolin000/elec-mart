package com.example.controller;

import com.example.common.Result;
import com.example.entity.SeckillCollect;
import com.example.service.SeckillCollectService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收藏前端操作接口
 **/
@RestController
@RequestMapping("/seckillcollect")
public class SeckillCollectController {

    @Autowired
    @Qualifier("seckillCollectService")
    private SeckillCollectService collectService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody SeckillCollect collect) {
        collectService.add(collect);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        collectService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        collectService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody SeckillCollect collect) {
        collectService.updateById(collect);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        SeckillCollect collect = collectService.selectById(id);
        return Result.success(collect);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(SeckillCollect collect ) {
        List<SeckillCollect> list = collectService.selectAll(collect);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(SeckillCollect collect,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<SeckillCollect> page = collectService.selectPage(collect, pageNum, pageSize);
        return Result.success(page);
    }

}