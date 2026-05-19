package com.ecommerce.runner;

import com.ecommerce.common.RedisKeys;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 定时刷新热销榜单缓存
 * 每 30 秒从 ZSet 读取 Top10，存入 String 缓存
 */
@Slf4j
@Component
public class SalesRankCacheTask {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;

    public SalesRankCacheTask(StringRedisTemplate redisTemplate, ObjectMapper objectMapper,
                              ProductMapper productMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.productMapper = productMapper;
    }

    @Scheduled(fixedRate = 30000)
    public void refreshHotSalesCache() {
        try {
            // 1. 从 ZSet 读取 Top10（带分数）
            Set<ZSetOperations.TypedTuple<String>> top10 = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(RedisKeys.SALES_RANK, 0, 9);

            if (top10 == null || top10.isEmpty()) {
                log.debug("热销榜单为空，跳过缓存刷新");
                return;
            }

            // 2. 批量查询商品名称
            List<Integer> productIds = new ArrayList<>();
            for (ZSetOperations.TypedTuple<String> tuple : top10) {
                productIds.add(Integer.parseInt(tuple.getValue()));
            }
            Map<Integer, String> nameMap = new HashMap<>();
            if (!productIds.isEmpty()) {
                List<Product> products = productMapper.selectBatchIds(productIds);
                for (Product p : products) {
                    nameMap.put(p.getId(), p.getName());
                }
            }

            // 3. 转换为列表
            List<Map<String, Object>> list = new ArrayList<>();
            int rank = 1;
            for (ZSetOperations.TypedTuple<String> tuple : top10) {
                Map<String, Object> item = new HashMap<>();
                int productId = Integer.parseInt(tuple.getValue());
                item.put("rank", rank++);
                item.put("productId", productId);
                item.put("name", nameMap.getOrDefault(productId, "未知商品"));
                item.put("sales", tuple.getScore().intValue());
                list.add(item);
            }

            // 4. 序列化为 JSON
            String json = objectMapper.writeValueAsString(list);

            // 5. 存入 String 缓存，TTL 35 秒（比定时任务周期长一点）
            redisTemplate.opsForValue().set(RedisKeys.HOT_SALES_CACHE, json, 35, TimeUnit.SECONDS);

            log.debug("热销榜单缓存已刷新，共 {} 条", list.size());
        } catch (Exception e) {
            log.error("刷新热销榜单缓存失败", e);
        }
    }
}
