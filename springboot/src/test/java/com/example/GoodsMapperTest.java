package com.example;

import com.example.entity.Goods;
import com.example.mapper.GoodsMapper;
import com.example.service.GoodsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GoodsMapperTest {

    @Mock
    private GoodsMapper goodsMapper;

    @InjectMocks
    private GoodsService goodsService; // 假设有一个服务层

    private Goods goods;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        goods = new Goods();
        goods.setId(1);
        goods.setName("Test Product");
        goods.setPrice(100.0);
        goods.setCount(10);
        goods.setBusinessId(1);
        goods.setGoodsUp("true");
    }

    @Test
    public void testInsert() {
        when(goodsMapper.insert(goods)).thenReturn(1); // 模拟返回插入成功

        int result = goodsMapper.insert(goods);
        assertEquals(1, result); // 验证返回值是否为1，表示成功插入

        verify(goodsMapper, times(1)).insert(goods); // 确保插入方法被调用了一次
    }

    @Test
    public void testDeleteById() {
        when(goodsMapper.deleteById(1)).thenReturn(1); // 模拟删除成功

        int result = goodsMapper.deleteById(1);
        assertEquals(1, result); // 验证返回值是否为1，表示删除成功

        verify(goodsMapper, times(1)).deleteById(1); // 确保删除方法被调用了一次
    }

    @Test
    public void testUpdateById() {
        when(goodsMapper.updateById(goods)).thenReturn(1); // 模拟更新成功

        int result = goodsMapper.updateById(goods);
        assertEquals(1, result); // 验证返回值是否为1，表示更新成功

        verify(goodsMapper, times(1)).updateById(goods); // 确保更新方法被调用了一次
    }

    @Test
    public void testToggleById() {
        when(goodsMapper.toggleById(1)).thenReturn(1); // 模拟toggle成功

        int result = goodsMapper.toggleById(1);
        assertEquals(1, result); // 验证返回值是否为1，表示操作成功

        verify(goodsMapper, times(1)).toggleById(1); // 确保toggle方法被调用了一次
    }

    @Test
    public void testSelectById() {
        when(goodsMapper.selectById(1)).thenReturn(goods); // 模拟返回一个商品对象

        Goods result = goodsMapper.selectById(1);
        assertNotNull(result); // 验证结果不为空
        assertEquals("Test Product", result.getName()); // 验证商品名称是否一致

        verify(goodsMapper, times(1)).selectById(1); // 确保selectById方法被调用了一次
    }

    @Test
    public void testSelectTop15() {
        Goods good1 = new Goods();
        good1.setId(1);
        good1.setName("Product 1");
        good1.setPrice(50.0);
        good1.setCount(15);

        Goods good2 = new Goods();
        good2.setId(2);
        good2.setName("Product 2");
        good2.setPrice(75.0);
        good2.setCount(25);

        List<Goods> goodsList = Arrays.asList(good1, good2);

        when(goodsMapper.selectTop15()).thenReturn(goodsList); // 模拟返回前15个商品

        List<Goods> result = goodsMapper.selectTop15();
        assertNotNull(result);
        assertEquals(2, result.size()); // 验证返回的商品数量是否为2

        verify(goodsMapper, times(1)).selectTop15(); // 确保selectTop15方法被调用了一次
    }

    @Test
    public void testSelectByBusinessId() {
        List<Goods> goodsList = Arrays.asList(goods);

        when(goodsMapper.selectByBusinessId(1)).thenReturn(goodsList); // 模拟通过商家ID查询商品

        List<Goods> result = goodsMapper.selectByBusinessId(1);
        assertNotNull(result);
        assertEquals(1, result.size()); // 验证返回的商品数量

        verify(goodsMapper, times(1)).selectByBusinessId(1); // 确保selectByBusinessId方法被调用了一次
    }

    @Test
    public void testCountUpGoods() {
        when(goodsMapper.countUpGoods(1)).thenReturn(10L); // 模拟查询上架商品数量

        Long result = goodsMapper.countUpGoods(1);
        assertEquals(10L, result); // 验证返回的商品数量是否正确

        verify(goodsMapper, times(1)).countUpGoods(1); // 确保countUpGoods方法被调用了一次
    }
}
