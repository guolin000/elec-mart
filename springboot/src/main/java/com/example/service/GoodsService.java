package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.mapper.*;
import com.example.utils.GoodsToggleBatchDTO;
import com.example.utils.GoodsToggleDTO;
import com.example.utils.TokenUtils;
import com.example.utils.UserCF;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 商品信息表业务处理
 **/
@Service
public class GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CollectMapper collectMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private OrdersMapper ordersMapper;

    /**
     * 新增
     */
    public void add(Goods goods) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            goods.setBusinessId(currentUser.getId());
        }
        goodsMapper.insert(goods);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        goodsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            goodsMapper.deleteById(id);
        }
    }
    /**
     * 上架
     */
    public void toggleById(Integer id){
        goodsMapper.toggleById(id);
    }
    @Transactional
    public void toggleGoods(GoodsToggleDTO dto) {
        // 更新商品的上下架状态
        goodsMapper.updateGoodsUpById(dto.getId(), dto.getGoodsUp());
    }
    /**
     * 批量上架
     */
    public  void toggleBatch(List<Integer> ids){
        for (Integer id : ids){
            goodsMapper.toggleById(id);
        }
    }
    @Transactional
    public void toggleUpBatch(GoodsToggleBatchDTO dto) {
        // 更新商品的上下架状态
        goodsMapper.updateGoodsUpByIds(dto.getIds(), dto.getGoodsUp());
    }

    /**
     * 修改
     */
    public void updateById(Goods goods) {
        goodsMapper.updateById(goods);
    }

    /**
     * 根据ID查询
     */
    public Goods selectById(Integer id) {
        return goodsMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Goods> selectAll(Goods goods) {
        return goodsMapper.selectAll(goods);
    }

    /**
     * 分页查询
     */
    public PageInfo<Goods> selectPage(Goods goods, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            goods.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectAll(goods);
        return PageInfo.of(list);
    }

    public List<Goods> selectTop15() {
        return goodsMapper.selectTop15();
    }

    public List<Goods> selectByTypeId(Integer id) {
        return goodsMapper.selectByTypeId(id);
    }

    public List<Goods> selectByBusinessId(Integer id) {
        return goodsMapper.selectByBusinessId(id);
    }

    public List<Goods> selectByName(String name) {
        return goodsMapper.selectByName(name);
    }

    public List<Goods> recommend() {
        Account currentUser = TokenUtils.getCurrentUser();
        if (ObjectUtil.isEmpty(currentUser)) {
            // 没有用户登录
            return new ArrayList<>();
        }
        // 用户的哪些行为可以认为他跟商品产生了关系？收藏、加入购物车、下单、评论
        // 1. 获取所有的收藏信息
        List<Collect> allCollects = collectMapper.selectAll(null);
        // 2. 获取所有的购物车信息
        List<Cart> allCarts = cartMapper.selectAll(null);
        // 3. 获取所有的订单信息
        List<Orders> allOrders = ordersMapper.selectAllOKOrders();
        // 4. 获取所有的评论信息
        List<Comment> allComments = commentMapper.selectAll(null);
        // 5. 获取所有的用户信息
        List<User> allUsers = userMapper.selectAll(null);
        // 6. 获取所有的商品信息
        List<Goods> allGoods = goodsMapper.selectAll(null);

        // 定义一个存储每个商品和每个用户关系的List
        List<RelateDTO> data = new ArrayList<>();
        // 定义一个存储最后返回给前端的商品List
        List<Goods> recommendResult;

        // 使用多线程和线程池 (ExecutorService)，程序并行计算每个商品和每个用户之间的交互权重
        // 创建一个栅栏，等待所有的异步处理都结束后，再往下走
        CountDownLatch countDownLatch = new CountDownLatch(allGoods.size() * allUsers.size());
        // 创建一个线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();

        // 开始计算每个商品和每个用户之间的关系数据
        for (Goods goods : allGoods) {
            Integer goodsId = goods.getId();
            for (User user : allUsers) {
                threadPool.execute(() -> {
                    Integer userId = user.getId();
                    int index = 1;
                    // 1. 判断该用户有没有收藏该商品，收藏的权重我们给 1
                    Optional<Collect> collectOptional = allCollects.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                    if (collectOptional.isPresent()) {
                        index += 1;
                    }
                    // 2. 判断该用户有没有给该商品加入购物车，加入购物车的权重我们给 2
                    Optional<Cart> cartOptional = allCarts.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                    if (cartOptional.isPresent()) {
                        index += 2;
                    }
                    // 3. 判断该用户有没有对该商品下过单（已完成的订单），订单的权重我们给 3
                    Optional<Orders> ordersOptional = allOrders.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                    if (ordersOptional.isPresent()) {
                        index += 3;
                    }
                    // 4. 判断该用户有没有对该商品评论过，评论的权重我们给 2
                    Optional<Comment> commentOptional = allComments.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                    if (commentOptional.isPresent()) {
                        index += 2;
                    }
                    // // 如果用户与商品之间有任何交互（即权重大于1），就记录下来
                    if (index > 1) {
                        RelateDTO relateDTO = new RelateDTO(userId, goodsId, index);
                        data.add(relateDTO);
                    }
                    countDownLatch.countDown(); //任务完成，计数器减一
                });
            }
        }

        try {
            countDownLatch.await(); // 等待所有异步任务完成
            threadPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 数据准备结束后，就把这些数据一起喂给这个推荐算法
            List<Integer> goodsIds = UserCF.recommend(currentUser.getId(), data);
            // 把商品id转换成商品
            recommendResult = goodsIds.stream().map(goodsId -> allGoods.stream()
                    .filter(x -> x.getId().equals(goodsId)).findFirst().orElse(null))
                    .limit(10).collect(Collectors.toList());
        }

        return recommendResult;
    }

    private List<Goods> getRandomGoods(int num) {
        List<Goods> list = new ArrayList<>(num);
        List<Goods> goods = goodsMapper.selectAll(null);
        for (int i = 0; i < num; i++) {
            int index = new Random().nextInt(goods.size());
            list.add(goods.get(index));
        }
        return list;
    }

    public Map<String, Long> getGoodStats(int id) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("a1", goodsMapper.countUpGoods(id));  // 已上架
        stats.put("a2", goodsMapper.countDownGoods(id));  // 已下架
        stats.put("a3", goodsMapper.countNeverseGoods(id));  // 库存紧张
        stats.put("a4", goodsMapper.count(id));  // 全部商品
        return stats;
    }

    // 审核通过（单个商品）
    public void approve(Integer id) {
        goodsMapper.updateGoodsStatus(id, "审核通过");
    }

    // 批量审核通过
    public void approveBatch(List<Integer> ids) {
        goodsMapper.updateGoodsStatusBatch(ids, "审核通过");
    }

    // 审核拒绝（单个商品）
    public void reject(Integer id) {
        goodsMapper.updateGoodsStatus(id, "审核拒绝");
    }

    // 批量审核拒绝
    public void rejectBatch(List<Integer> ids) {
        goodsMapper.updateGoodsStatusBatch(ids, "审核拒绝");
    }
}