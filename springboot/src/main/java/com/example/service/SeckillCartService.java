package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.SeckillCart;
import com.example.mapper.SeckillCartMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏业务处理
 **/
@Service("seckillCartService")
public class SeckillCartService {

    @Resource
    private SeckillCartMapper seckillcartMapper;

    /**
     * 新增
     */
    public void add(SeckillCart seckillcart) {
        // 判断该用户对该商品有没有加入过购物车，如果加入过，那么只要更新一下该条记录的num（+1）
        SeckillCart dbCart = seckillcartMapper.selectByUserIdAndGoodsId(seckillcart.getUserId(), seckillcart.getGoodsId());
        if (ObjectUtil.isNotEmpty(dbCart)) {
            dbCart.setNum(dbCart.getNum() + 1);
            seckillcartMapper.updateById(dbCart);
        } else {
            seckillcartMapper.insert(seckillcart);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        seckillcartMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            seckillcartMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(SeckillCart cart) {
        seckillcartMapper.updateById(cart);
    }

    /**
     * 根据ID查询
     */
    public SeckillCart selectById(Integer id) {
        return seckillcartMapper.selectById(id);
    }

    /**
     * 根据商品ID查询
     */
    public List<SeckillCart> selectByGoodsId(Integer goodsId) {
        Account currentUser = TokenUtils.getCurrentUser();
        List<SeckillCart> goodsList=seckillcartMapper.selectByGoodsId(goodsId);
        for (int i=goodsList.size()-1; i >= 0; i--){
            if (goodsList.get(i).getUserId()!=currentUser.getId()){
                goodsList.remove(i);
            }
        }
        return goodsList;
    }


    /**
     * 查询所有
     */
    public List<SeckillCart> selectAll(SeckillCart cart) {
        return seckillcartMapper.selectAll(cart);
    }

    /**
     * 分页查询
     */
    public PageInfo<SeckillCart> selectPage(SeckillCart cart, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            cart.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillCart> list = seckillcartMapper.selectAll(cart);
        return PageInfo.of(list);
    }
}