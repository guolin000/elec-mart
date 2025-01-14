package com.example.service;

import com.example.entity.SecondType;
import com.example.entity.Type;
import com.example.mapper.SecondTypeMapper;
import com.example.mapper.TypeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类信息二级表业务处理
 **/
@Service
public class SecondTypeService {

    @Resource
    private SecondTypeMapper secondTypeMapper;

    /**
     * 新增
     */
    public void add(SecondType secondType) {
        secondTypeMapper.insert(secondType);
        System.out.println(secondType);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        secondTypeMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            secondTypeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(SecondType secondType) {
        secondTypeMapper.updateById(secondType);
    }

    /**
     * 根据ID查询
     */
    public SecondType selectById(Integer id) {
        return secondTypeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<SecondType> selectAll(SecondType secondType) {
        return secondTypeMapper.selectAll(secondType);
    }

    public List<SecondType> selectByTypeId(Integer typeId) {
        return secondTypeMapper.selectByTypeId(typeId);
    }

    /**
     * 分页查询
     */
    public PageInfo<SecondType> selectPage(SecondType secondType, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SecondType> list = secondTypeMapper.selectAll(secondType);
        System.out.println(list);
        return PageInfo.of(list);
    }

}