package com.ecommerce.common;

/**
 * Redis 缓存 Key 常量定义
 */
public class RedisKeys {

    private RedisKeys() {} 
    

    // ==================== 分类 ====================
    public static final String CATEGORIES_ALL = "categories:all";

    // ==================== 商品 ====================
    public static final String PRODUCT_LIST_ALL = "products:all";
    public static final String PRODUCT_PREFIX = "product:";

    // ==================== 优惠券 ====================
    public static final String COUPONS_ACTIVE = "coupons:active";
    public static final String USER_PREFIX = "user:";

    // ==================== 热销榜单 ====================
    public static final String SALES_RANK = "product:sales_rank";
    public static final String HOT_SALES_CACHE = "cache:hot_sales:top10";

    // ==================== Dashboard ====================
    public static final String DASHBOARD_SALES_TREND = "dashboard:salesTrend";
    public static final String DASHBOARD_ORDER_STATUS = "dashboard:orderStatus";
    public static final String DASHBOARD_TOP_PRODUCTS = "dashboard:topProducts";
}
