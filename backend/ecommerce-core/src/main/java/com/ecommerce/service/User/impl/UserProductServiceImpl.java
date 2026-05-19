package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.common.RedisKeys;
import com.ecommerce.service.User.UserProductService;
import com.ecommerce.utils.RedisCacheUtil;
import com.ecommerce.vo.ProductVO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户端商品服务实现类（只读）
 */
@Slf4j
@Service
public class UserProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements UserProductService {

    private final RedisCacheUtil redisCacheUtil;

    public UserProductServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    public List<ProductVO> listProducts() {
        log.debug("用户端查询商品列表");
        List<ProductVO> cached = redisCacheUtil.valueOps.get(RedisKeys.PRODUCT_LIST_ALL,
                new TypeReference<List<ProductVO>>() {});
        if (cached != null) {
            log.debug("商品列表缓存命中");
            return cached;
        }

        List<Product> products = query().list();
        log.debug("查询到商品数量: {}", products.size());
        List<ProductVO> result = products.stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        redisCacheUtil.valueOps.set(RedisKeys.PRODUCT_LIST_ALL, result, 5);
        log.debug("商品列表已缓存");
        return result;
    }

    @Override
    public ProductVO getProductById(Integer id) {
        log.debug("用户端查询商品详情: productId={}", id);
        String cacheKey = RedisKeys.PRODUCT_PREFIX + id;
        ProductVO cached = redisCacheUtil.valueOps.get(cacheKey, ProductVO.class);
        if (cached != null) {
            log.debug("商品详情缓存命中: productId={}", id);
            return cached;
        }

        Product product = query().eq("id", id).one();
        if (product == null) {
            log.warn("商品不存在: productId={}", id);
            return null;
        }

        ProductVO result = convertToVO(product);
        redisCacheUtil.valueOps.set(cacheKey, result, 5);
        log.debug("商品详情已缓存: productId={}", id);

        return result;
    }

    @Override
    public List<ProductVO> listProductsByCategoryId(Integer categoryId) {
        log.debug("用户端根据分类查询商品: categoryId={}", categoryId);
        List<Product> products = query().eq("category_id", categoryId).list();
        log.debug("分类{}下查询到商品数量: {}", categoryId, products.size());
        return products.stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 清除商品缓存（商家端修改/删除商品时调用）
     */
    public void clearProductCache(Integer id) {
        String cacheKey = RedisKeys.PRODUCT_PREFIX + id;
        redisCacheUtil.valueOps.delete(cacheKey);
        log.debug("商品缓存已清除: productId={}", id);
    }

    /**
     * 清除商品列表缓存（商家端新增/修改/删除商品时调用）
     */
    public void clearProductListCache() {
        redisCacheUtil.valueOps.delete(RedisKeys.PRODUCT_LIST_ALL);
        log.debug("商品列表缓存已清除");
    }

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
