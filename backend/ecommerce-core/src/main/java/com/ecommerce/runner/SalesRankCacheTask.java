package com.ecommerce.runner;

import com.ecommerce.common.RedisKeys;
import com.ecommerce.utils.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 定时刷新热销榜单缓存
 * 每 30 秒从 ZSet 读取 Top10，商品名从 Hash 获取，存入 String 缓存
 */
@Slf4j
@Component
public class SalesRankCacheTask {

    private final RedisCacheUtil redisCacheUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public SalesRankCacheTask(RedisCacheUtil redisCacheUtil, StringRedisTemplate stringRedisTemplate) {
        this.redisCacheUtil = redisCacheUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Scheduled(fixedRate = 30000)
    public void refreshHotSalesCache() {
        try {
            // 1. 从 ZSet 读取 Top10（带分数）
            Set<ZSetOperations.TypedTuple<String>> top10 = redisCacheUtil.zSetOps
                    .reverseRangeWithScores(RedisKeys.SALES_RANK, 0, 9);

            if (top10 == null || top10.isEmpty()) {
                log.debug("热销榜单为空，跳过缓存刷新");
                return;
            }

            // 2. 收集商品 ID，从 Hash 批量获取商品名
            List<Object> productIds = new ArrayList<>();
            for (ZSetOperations.TypedTuple<String> tuple : top10) {
                productIds.add(tuple.getValue());
            }

            // 检查 Hash 是否存在，不存在则跳过（由 SalesRankInitRunner 负责预热）
            Boolean hashExists = stringRedisTemplate.hasKey(RedisKeys.PRODUCT_NAME_MAP);
            if (hashExists == null || !hashExists) {
                log.debug("商品名映射 Hash 不存在，跳过缓存刷新");
                return;
            }

            List<Object> names = stringRedisTemplate.opsForHash()
                    .multiGet(RedisKeys.PRODUCT_NAME_MAP, productIds);

            // 3. 构建 ID -> Name 映射
            Map<String, String> nameMap = new HashMap<>();
            for (int i = 0; i < productIds.size(); i++) {
                if (names.get(i) != null) {
                    nameMap.put(productIds.get(i).toString(), names.get(i).toString());
                }
            }

            // 4. 转换为列表
            List<Map<String, Object>> list = new ArrayList<>();
            int rank = 1;
            for (ZSetOperations.TypedTuple<String> tuple : top10) {
                Map<String, Object> item = new HashMap<>();
                String productId = tuple.getValue();
                item.put("rank", rank++);
                item.put("productId", Integer.parseInt(productId));
                item.put("name", nameMap.getOrDefault(productId, "未知商品"));
                item.put("sales", tuple.getScore().intValue());
                list.add(item);
            }

            // 5. 存入 String 缓存，TTL 35 秒
            redisCacheUtil.valueOps.set(RedisKeys.HOT_SALES_CACHE, list, 35, TimeUnit.SECONDS);

            log.debug("热销榜单缓存已刷新，共 {} 条", list.size());
        } catch (Exception e) {
            log.error("刷新热销榜单缓存失败", e);
        }
    }
}
