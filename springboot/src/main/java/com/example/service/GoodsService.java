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
import java.util.concurrent.CompletableFuture;
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
            return new ArrayList<>();
        }

        // 获取商品、用户相关的行为数据（用HashMap代替List提高效率）
        Map<Integer, List<Collect>> collectMap = collectMapper.selectAll(null)
                .stream().collect(Collectors.groupingBy(Collect::getGoodsId));
        Map<Integer, List<Cart>> cartMap = cartMapper.selectAll(null)
                .stream().collect(Collectors.groupingBy(Cart::getGoodsId));
        Map<Integer, List<Orders>> ordersMap = ordersMapper.selectAllOKOrders()
                .stream().collect(Collectors.groupingBy(Orders::getGoodsId));
        Map<Integer, List<Comment>> commentMap = commentMapper.selectAll(null)
                .stream().collect(Collectors.groupingBy(Comment::getGoodsId));

        List<Goods> allGoods = goodsMapper.selectAll(null);
        List<User> allUsers = userMapper.selectAll(null);

        // 使用线程池执行推荐计算任务
        ExecutorService threadPool = Executors.newFixedThreadPool(10); // 限制线程池的大小
        List<RelateDTO> data = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(allGoods.size() * allUsers.size());

        // 使用CompletableFuture来管理异步任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 计算用户与商品之间的关系
        for (Goods goods : allGoods) {
            Integer goodsId = goods.getId();
            for (User user : allUsers) {
                futures.add(CompletableFuture.runAsync(() -> {
                    Integer userId = user.getId();
                    int index = 1;

                    // 判断用户行为并增加权重
                    if (collectMap.containsKey(goodsId) && collectMap.get(goodsId).stream()
                            .anyMatch(collect -> collect.getUserId().equals(userId))) {
                        index += 1;
                    }
                    if (cartMap.containsKey(goodsId) && cartMap.get(goodsId).stream()
                            .anyMatch(cart -> cart.getUserId().equals(userId))) {
                        index += 2;
                    }
                    if (ordersMap.containsKey(goodsId) && ordersMap.get(goodsId).stream()
                            .anyMatch(order -> order.getUserId().equals(userId))) {
                        index += 3;
                    }
                    if (commentMap.containsKey(goodsId) && commentMap.get(goodsId).stream()
                            .anyMatch(comment -> comment.getUserId().equals(userId))) {
                        index += 2;
                    }

                    // 记录有交互的商品-用户关系
                    if (index > 1) {
                        data.add(new RelateDTO(userId, goodsId, index));
                    }

                    countDownLatch.countDown();
                }, threadPool));
            }
        }

        try {
            // 通过CompletableFuture优化线程任务管理，减少线程池管理的复杂性，并加快异步任务的合并执行。
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            threadPool.shutdown();
            countDownLatch.await(); // 等待CountDownLatch的完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 使用UserCF推荐算法来获取推荐商品ID列表
        List<Integer> recommendedGoodsIds = UserCF.recommend(currentUser.getId(), data);

        // 将商品ID转换为商品对象，限制返回数量为10
        return recommendedGoodsIds.stream()
                .map(goodsId -> allGoods.stream()
                        .filter(goods -> goods.getId().equals(goodsId))
                        .findFirst().orElse(null))
                .limit(10)
                .collect(Collectors.toList());
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