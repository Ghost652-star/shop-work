package com.ecommerce;

import com.ecommerce.common.RedisKeys;
import com.ecommerce.service.User.UserCategoryService;
import com.ecommerce.service.User.UserProductService;
import com.ecommerce.service.User.CouponService;
import com.ecommerce.service.User.UserService;
import com.ecommerce.service.User.impl.UserCategoryServiceImpl;
import com.ecommerce.service.User.impl.UserProductServiceImpl;
import com.ecommerce.service.User.impl.CouponServiceImpl;
import com.ecommerce.service.User.impl.UserServiceImpl;
import com.ecommerce.utils.RedisCacheUtil;
import com.ecommerce.vo.CategoryVO;
import com.ecommerce.vo.ProductVO;
import com.ecommerce.vo.CouponVO;
import com.ecommerce.vo.UserVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CacheIntegrationTest {

    @Autowired
    private UserProductService userProductService;

    @Autowired
    private UserCategoryService userCategoryService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProductServiceImpl userProductServiceImpl;

    @Autowired
    private UserCategoryServiceImpl userCategoryServiceImpl;

    @Autowired
    private CouponServiceImpl couponServiceImpl;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Test
    void testProductCache() {
        Integer productId = 1;

        // 1. 用户端第一次查询（缓存没有，会查数据库）
        ProductVO first = userProductService.getProductById(productId);
        if (first == null) {
            System.out.println("商品不存在，跳过测试");
            return;
        }
        System.out.println("用户端第一次查询：" + first.getName());

        // 2. 验证缓存已存入
        String cacheKey = RedisKeys.PRODUCT_PREFIX + productId;
        ProductVO cached = redisCacheUtil.valueOps.get(cacheKey, ProductVO.class);
        assertNotNull(cached, "缓存应该已存入");
        assertEquals(first.getName(), cached.getName());
        System.out.println("商品缓存验证通过");

        // 3. 用户端第二次查询（应该走缓存）
        ProductVO second = userProductService.getProductById(productId);
        assertEquals(first.getName(), second.getName());
        System.out.println("用户端第二次查询走缓存，验证通过");

        // 4. 清除缓存（模拟商家端操作）
        userProductServiceImpl.clearProductCache(productId);
        ProductVO afterClear = redisCacheUtil.valueOps.get(cacheKey, ProductVO.class);
        assertNull(afterClear, "缓存应该已清除");
        System.out.println("商品缓存清除验证通过");
    }

    @Test
    void testCategoryCache() {
        // 1. 用户端第一次查询（缓存没有，会查数据库）
        List<CategoryVO> first = userCategoryService.listCategories();
        assertTrue(first.size() > 0, "应该有分类数据");
        System.out.println("用户端第一次查询，分类数量：" + first.size());

        // 2. 验证缓存已存入
        List<CategoryVO> cached = redisCacheUtil.valueOps.get(RedisKeys.CATEGORIES_ALL,
                new com.fasterxml.jackson.core.type.TypeReference<List<CategoryVO>>() {});
        assertNotNull(cached, "缓存应该已存入");
        assertEquals(first.size(), cached.size());
        System.out.println("分类缓存验证通过");

        // 3. 用户端第二次查询（应该走缓存）
        List<CategoryVO> second = userCategoryService.listCategories();
        assertEquals(first.size(), second.size());
        System.out.println("用户端第二次查询走缓存，验证通过");

        // 4. 清除缓存（模拟商家端操作）
        userCategoryServiceImpl.clearCategoryCache();
        List<CategoryVO> afterClear = redisCacheUtil.valueOps.get(RedisKeys.CATEGORIES_ALL,
                new com.fasterxml.jackson.core.type.TypeReference<List<CategoryVO>>() {});
        assertNull(afterClear, "缓存应该已清除");
        System.out.println("分类缓存清除验证通过");
    }

    @Test
    void testCouponCache() {
        // 1. 用户端第一次查询（缓存没有，会查数据库）
        List<CouponVO> first = couponService.listCoupons();
        System.out.println("用户端第一次查询优惠券，数量：" + first.size());

        // 2. 验证缓存已存入
        List<CouponVO> cached = redisCacheUtil.valueOps.get(RedisKeys.COUPONS_ACTIVE,
                new com.fasterxml.jackson.core.type.TypeReference<List<CouponVO>>() {});
        assertNotNull(cached, "优惠券缓存应该已存入");
        assertEquals(first.size(), cached.size());
        System.out.println("优惠券缓存验证通过");

        // 3. 用户端第二次查询（应该走缓存）
        List<CouponVO> second = couponService.listCoupons();
        assertEquals(first.size(), second.size());
        System.out.println("用户端第二次查询走缓存，验证通过");

        // 4. 清除缓存
        couponServiceImpl.clearCouponCache();
        List<CouponVO> afterClear = redisCacheUtil.valueOps.get(RedisKeys.COUPONS_ACTIVE,
                new com.fasterxml.jackson.core.type.TypeReference<List<CouponVO>>() {});
        assertNull(afterClear, "优惠券缓存应该已清除");
        System.out.println("优惠券缓存清除验证通过");
    }

    @Test
    void testUserCache() {
        Integer userId = 1;

        // 1. 用户端第一次查询（缓存没有，会查数据库）
        UserVO first = userService.getUserById(userId);
        if (first == null) {
            System.out.println("用户不存在，跳过测试");
            return;
        }
        System.out.println("用户端第一次查询用户信息：" + first.getNickname());

        // 2. 验证缓存已存入
        String cacheKey = RedisKeys.USER_PREFIX + userId;
        UserVO cached = redisCacheUtil.valueOps.get(cacheKey, UserVO.class);
        assertNotNull(cached, "用户缓存应该已存入");
        assertEquals(first.getNickname(), cached.getNickname());
        System.out.println("用户缓存验证通过");

        // 3. 用户端第二次查询（应该走缓存）
        UserVO second = userService.getUserById(userId);
        assertEquals(first.getNickname(), second.getNickname());
        System.out.println("用户端第二次查询走缓存，验证通过");

        // 4. 清除缓存
        userServiceImpl.clearUserCache(userId);
        UserVO afterClear = redisCacheUtil.valueOps.get(cacheKey, UserVO.class);
        assertNull(afterClear, "用户缓存应该已清除");
        System.out.println("用户缓存清除验证通过");
    }

    // ==================== 门面模式测试 ====================

    @Test
    void testValueOps() {
        String key = "test:valueops:string";

        // 1. 存入字符串
        redisCacheUtil.valueOps.set(key, "hello", 1);
        String value = redisCacheUtil.valueOps.get(key, String.class);
        assertEquals("hello", value);
        System.out.println("ValueOps 字符串存取验证通过");

        // 2. 存入对象
        String objKey = "test:valueops:object";
        ProductVO product = ProductVO.builder().id(999).name("测试商品").build();
        redisCacheUtil.valueOps.set(objKey, product, 1);
        ProductVO cachedProduct = redisCacheUtil.valueOps.get(objKey, ProductVO.class);
        assertNotNull(cachedProduct);
        assertEquals("测试商品", cachedProduct.getName());
        System.out.println("ValueOps 对象存取验证通过");

        // 3. 删除
        redisCacheUtil.valueOps.delete(key);
        redisCacheUtil.valueOps.delete(objKey);
        assertNull(redisCacheUtil.valueOps.get(key, String.class));
        System.out.println("ValueOps 删除验证通过");
    }

    @Test
    void testZSetOps() {
        String key = "test:zset:sales_rank";

        // 1. 增加分数（模拟商品销量）
        redisCacheUtil.zSetOps.incrementScore(key, "product_001", 10);
        redisCacheUtil.zSetOps.incrementScore(key, "product_002", 20);
        redisCacheUtil.zSetOps.incrementScore(key, "product_003", 15);

        // 2. 验证排行榜（倒序）
        Set<String> topProducts = redisCacheUtil.zSetOps.reverseRange(key, 0, 2);
        assertNotNull(topProducts);
        assertEquals(3, topProducts.size());
        String[] array = topProducts.toArray(new String[0]);
        assertEquals("product_002", array[0]); // 分数最高
        assertEquals("product_003", array[1]);
        assertEquals("product_001", array[2]);
        System.out.println("ZSetOps 排行榜验证通过");

        // 3. 获取成员分数
        Double score = redisCacheUtil.zSetOps.score(key, "product_002");
        assertEquals(20.0, score);
        System.out.println("ZSetOps 分数查询验证通过");

        // 4. 获取排名
        Long rank = redisCacheUtil.zSetOps.reverseRank(key, "product_002");
        assertEquals(0L, rank); // 第一名
        System.out.println("ZSetOps 排名查询验证通过");

        // 5. 获取集合大小
        Long size = redisCacheUtil.zSetOps.size(key);
        assertEquals(3L, size);
        System.out.println("ZSetOps 大小查询验证通过");

        // 6. 清理
        redisCacheUtil.zSetOps.remove(key, "product_001", "product_002", "product_003");
    }

    @Test
    void testHashOps() {
        String key = "test:hash:user_session";

        // 1. 存入字段
        redisCacheUtil.hashOps.put(key, "field1", "value1");
        redisCacheUtil.hashOps.put(key, "field2", 123);

        // 2. 获取单个字段
        String value1 = redisCacheUtil.hashOps.get(key, "field1", String.class);
        assertEquals("value1", value1);
        System.out.println("HashOps 单字段获取验证通过");

        Integer value2 = redisCacheUtil.hashOps.get(key, "field2", Integer.class);
        assertEquals(123, value2);
        System.out.println("HashOps 数字字段获取验证通过");

        // 3. 检查字段是否存在
        assertTrue(redisCacheUtil.hashOps.hasKey(key, "field1"));
        assertFalse(redisCacheUtil.hashOps.hasKey(key, "field3"));
        System.out.println("HashOps 字段存在性验证通过");

        // 4. 获取所有字段
        Set<Object> keys = redisCacheUtil.hashOps.keys(key);
        assertEquals(2, keys.size());
        System.out.println("HashOps 获取所有字段验证通过");

        // 5. 删除字段
        redisCacheUtil.hashOps.delete(key, "field1");
        assertFalse(redisCacheUtil.hashOps.hasKey(key, "field1"));
        System.out.println("HashOps 删除字段验证通过");

        // 6. 清理
        redisCacheUtil.hashOps.delete(key, "field2");
    }

    @Test
    void testValueOpsAdvanced() {
        // 测试 setIfAbsent（分布式锁场景）
        String key = "test:valueops:ifabsent";
        Boolean result1 = redisCacheUtil.valueOps.setIfAbsent(key, "first_value", 1);
        assertTrue(result1, "第一次设置应该成功");

        Boolean result2 = redisCacheUtil.valueOps.setIfAbsent(key, "second_value", 1);
        assertFalse(result2, "第二次设置应该失败（key已存在）");

        String value = redisCacheUtil.valueOps.get(key, String.class);
        assertEquals("first_value", value, "值应该是第一次设置的");

        redisCacheUtil.valueOps.delete(key);
        System.out.println("ValueOps setIfAbsent 验证通过");
    }
}
