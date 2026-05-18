package com.ecommerce.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.common.RedisKeys;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.ProductService;
import com.ecommerce.utils.RedisCacheUtil;
import com.ecommerce.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final RedisCacheUtil redisCacheUtil;

    public ProductServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    /**
     * 查询商品列表
     * @return 商品列表
     */
    @Override
    public List<ProductVO> listProducts() {
        log.debug("查询所有商品列表");
        List<Product> products = query().list();
        log.debug("查询到商品数量: {}", products.size());
        return products.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID查询商品详情（带缓存）
     * @param id 商品ID
     * @return 商品详情
     */
    @Override
    public ProductVO getProductById(Integer id) {
        // 1. 先从 Redis 获取
        String cacheKey = RedisKeys.PRODUCT_PREFIX + id;
        ProductVO cached = redisCacheUtil.get(cacheKey, ProductVO.class);
        if (cached != null) {
            log.debug("商品详情缓存命中: productId={}", id);
            return cached;
        }

        // 2. 缓存没有，查数据库
        log.debug("查询商品详情: productId={}", id);
        Product product = query().eq("id", id).one();
        if (product == null) {
            log.warn("商品不存在: productId={}", id);
            return null;
        }

        ProductVO result = convertToVO(product);

        // 3. 存入 Redis，缓存 5 分钟
        redisCacheUtil.set(cacheKey, result, 5);
        log.debug("商品详情已缓存: productId={}", id);

        return result;
    }

    /**
     * 根据分类ID查询商品列表
     * @param categoryId 分类ID
     * @return 商品列表
     */
    @Override
    public List<ProductVO> listProductsByCategoryId(Integer categoryId) {
        log.debug("根据分类查询商品: categoryId={}", categoryId);
        List<Product> products = query().eq("category_id", categoryId).list();
        log.debug("分类{}下查询到商品数量: {}", categoryId, products.size());
        return products.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 清除商品缓存（商品变更时调用）
     * @param id 商品ID
     */
    public void clearProductCache(Integer id) {
        String cacheKey = RedisKeys.PRODUCT_PREFIX + id;
        redisCacheUtil.delete(cacheKey);
        log.debug("商品缓存已清除: productId={}", id);
    }

    /**
     * 将 Product 实体转换为 ProductVO
     * @param product 商品实体
     * @return 商品视图对象
     */
    private ProductVO convertToVO(Product product) {
        if (product == null) {
            return null;
        }
        return ProductVO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sales(product.getSales())
                .mainImage(product.getMainImage())
                .categoryId(product.getCategoryId())
                .build();
    }
}