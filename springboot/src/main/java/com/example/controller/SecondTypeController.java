package com.example.controller;

import com.example.common.Result;
import com.example.entity.SecondType;
import com.example.entity.Type;
import com.example.service.SecondTypeService;
import com.example.service.TypeService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类二级表前端操作接口
 **/
@RestController
@RequestMapping("/secondType")
public class SecondTypeController {

    @Resource
    private SecondTypeService secondTypeService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody SecondType secondType) {
        secondTypeService.add(secondType);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        secondTypeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        secondTypeService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody SecondType secondType) {
        secondTypeService.updateById(secondType);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        SecondType secondType = secondTypeService.selectById(id);
        return Result.success(secondType);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(SecondType secondType ) {
        List<SecondType> list = secondTypeService.selectAll(secondType);
        return Result.success(list);
    }

    /**
     * 根据一级分类查询
     */
    @GetMapping("/selectByTypeId/{typeId}")
    public Result selectByTypeId(@PathVariable Integer typeId) {
        List<SecondType> list = secondTypeService.selectByTypeId(typeId);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(SecondType secondType,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<SecondType> page = secondTypeService.selectPage(secondType, pageNum, pageSize);
        return Result.success(page);
    }

}