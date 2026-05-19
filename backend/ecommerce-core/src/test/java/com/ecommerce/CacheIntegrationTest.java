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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
        ProductVO cached = redisCacheUtil.get(cacheKey, ProductVO.class);
        assertNotNull(cached, "缓存应该已存入");
        assertEquals(first.getName(), cached.getName());
        System.out.println("商品缓存验证通过");

        // 3. 用户端第二次查询（应该走缓存）
        ProductVO second = userProductService.getProductById(productId);
        assertEquals(first.getName(), second.getName());
        System.out.println("用户端第二次查询走缓存，验证通过");

        // 4. 清除缓存（模拟商家端操作）
        userProductServiceImpl.clearProductCache(productId);
        ProductVO afterClear = redisCacheUtil.get(cacheKey, ProductVO.class);
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
        List<CategoryVO> cached = redisCacheUtil.get(RedisKeys.CATEGORIES_ALL,
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
        List<CategoryVO> afterClear = redisCacheUtil.get(RedisKeys.CATEGORIES_ALL,
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
        List<CouponVO> cached = redisCacheUtil.get(RedisKeys.COUPONS_ACTIVE,
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
        List<CouponVO> afterClear = redisCacheUtil.get(RedisKeys.COUPONS_ACTIVE,
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
        UserVO cached = redisCacheUtil.get(cacheKey, UserVO.class);
        assertNotNull(cached, "用户缓存应该已存入");
        assertEquals(first.getNickname(), cached.getNickname());
        System.out.println("用户缓存验证通过");

        // 3. 用户端第二次查询（应该走缓存）
        UserVO second = userService.getUserById(userId);
        assertEquals(first.getNickname(), second.getNickname());
        System.out.println("用户端第二次查询走缓存，验证通过");

        // 4. 清除缓存
        userServiceImpl.clearUserCache(userId);
        UserVO afterClear = redisCacheUtil.get(cacheKey, UserVO.class);
        assertNull(afterClear, "用户缓存应该已清除");
        System.out.println("用户缓存清除验证通过");
    }
}
