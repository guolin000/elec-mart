package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.SeckillCollect;
import com.example.exception.CustomException;
import com.example.mapper.CollectMapper;
import com.example.mapper.SeckillCollectMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收藏业务处理
 **/
@Service("seckillCollectService")
public class SeckillCollectService {

    @Resource
    private SeckillCollectMapper seckillcollectMapper;

    /**
     * 新增
     */
    public void add(SeckillCollect collect) {
        // 判断一下该用户有没有收藏过该商品，如果有，就要提示用户不能重复收藏
        SeckillCollect dbCollect = seckillcollectMapper.selectByUserIdAndGoodsId(collect.getUserId(), collect.getGoodsId());
        if (ObjectUtil.isNotEmpty(dbCollect)) {
            throw new CustomException(ResultCodeEnum.COLLECT_ALREADY_ERROR);
        }
        seckillcollectMapper.insert(collect);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        seckillcollectMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            seckillcollectMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(SeckillCollect collect) {
        seckillcollectMapper.updateById(collect);
    }

    /**
     * 根据ID查询
     */
    public SeckillCollect selectById(Integer id) {
        return seckillcollectMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<SeckillCollect> selectAll(SeckillCollect collect) {
        return seckillcollectMapper.selectAll(collect);
    }

    /**
     * 分页查询
     */
    public PageInfo<SeckillCollect> selectPage(SeckillCollect collect, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            collect.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillCollect> list = seckillcollectMapper.selectAll(collect);
        return PageInfo.of(list);
    }
}