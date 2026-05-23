package com.ecommerce.runner;

import com.ecommerce.common.RedisKeys;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.utils.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动预热：将数据库中的销量数据加载到 Redis ZSet
 */
@Slf4j
@Component
public class SalesRankInitRunner implements CommandLineRunner {

    private final ProductMapper productMapper;
    private final RedisCacheUtil redisCacheUtil;

    public SalesRankInitRunner(ProductMapper productMapper, RedisCacheUtil redisCacheUtil) {
        this.productMapper = productMapper;
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    public void run(String... args) {
        log.info("开始预热销榜单数据...");

        // 1. 只查 id 和 sales，避免加载全部字段
        List<Product> products = productMapper.selectIdAndSales();
        if (products.isEmpty()) {
            log.info("没有商品数据，跳过预热");
            return;
        }

        // 2. 清空旧数据，重新加载
        redisCacheUtil.valueOps.delete(RedisKeys.SALES_RANK);

        // 3. 批量写入 ZSet
        int count = 0;
        for (Product p : products) {
            if (p.getSales() != null && p.getSales() > 0) {
                redisCacheUtil.zSetOps.incrementScore(
                        RedisKeys.SALES_RANK,
                        p.getId().toString(),
                        p.getSales()
                );
                count++;
            }
        }

        
        log.info("热销榜单预热完成，共加载 {} 个商品", count);
    }
}
