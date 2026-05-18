package com.ecommerce;

import com.ecommerce.entity.Product;
import com.ecommerce.utils.RedisCacheUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisCacheUtilTest {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Test
    void testSetAndGetSimpleObject() {
        // 创建一个 Product 对象
        Product product = new Product();
        product.setId(1);
        product.setName("测试商品");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);

        // 存入 Redis
        redisCacheUtil.set("test:product:1", product, 5);

        // 读取出来
        Product cached = redisCacheUtil.get("test:product:1", Product.class);

        // 验证
        assertNotNull(cached);
        assertEquals(1, cached.getId());
        assertEquals("测试商品", cached.getName());
        assertEquals(new BigDecimal("99.99"), cached.getPrice());
        assertEquals(100, cached.getStock());

        System.out.println("简单对象测试通过：" + cached);
    }

    @Test
    void testSetAndGetComplexObject() {
        // 创建多个 Product 对象
        Product p1 = new Product();
        p1.setId(1);
        p1.setName("商品1");

        Product p2 = new Product();
        p2.setId(2);
        p2.setName("商品2");

        List<Product> productList = Arrays.asList(p1, p2);

        // 存入 Redis（List 类型）
        redisCacheUtil.set("test:product:list", productList, 5);

        // 使用 TypeReference 读取 List
        List<Product> cachedList = redisCacheUtil.get("test:product:list",
                new TypeReference<List<Product>>() {});

        // 验证
        assertNotNull(cachedList);
        assertEquals(2, cachedList.size());
        assertEquals("商品1", cachedList.get(0).getName());
        assertEquals("商品2", cachedList.get(1).getName());

        System.out.println("复杂对象测试通过：" + cachedList);
    }

    @Test
    void testGetNonExistentKey() {
        // 读取不存在的 key
        Product result = redisCacheUtil.get("test:notexist", Product.class);

        // 应该返回 null
        assertNull(result);
        System.out.println("不存在的 key 返回 null，测试通过");
    }
}
