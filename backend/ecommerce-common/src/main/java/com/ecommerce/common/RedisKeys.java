package com.ecommerce.common;

/**
 * Redis 缓存 Key 常量定义
 */
public class RedisKeys {

    private RedisKeys() {} 

    // ==================== 分类 ====================
    public static final String CATEGORIES_ALL = "categories:all";

    // ==================== 商品 ====================
    public static final String PRODUCT_PREFIX = "product:";

    // ==================== 优惠券 ====================
    public static final String COUPONS_ACTIVE = "coupons:active";

    // ==================== Dashboard ====================
    public static final String DASHBOARD_SALES_TREND = "dashboard:salesTrend";
    public static final String DASHBOARD_ORDER_STATUS = "dashboard:orderStatus";
    public static final String DASHBOARD_TOP_PRODUCTS = "dashboard:topProducts";
}
