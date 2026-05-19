package com.ecommerce.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类（门面模式）
 */
@Component
public class RedisCacheUtil {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    public final ValueOps valueOps;
    public final ZSetOps zSetOps;
    public final HashOps hashOps;

    public RedisCacheUtil(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;

        // 初始化各个子模块
        this.valueOps = new ValueOps(stringRedisTemplate, objectMapper);
        this.zSetOps = new ZSetOps(stringRedisTemplate);
        this.hashOps = new HashOps(stringRedisTemplate, objectMapper);
    }

    

    // ==================== 内部类：处理 String/JSON 类型 ====================

    public static class ValueOps {
        private final StringRedisTemplate template;
        private final ObjectMapper mapper;

        ValueOps(StringRedisTemplate template, ObjectMapper mapper) {
            this.template = template;
            this.mapper = mapper;
        }

        /**
         * 设置缓存（JSON 序列化）
         */
        public void set(String key, Object value, long minutes) {
            try {
                String json = mapper.writeValueAsString(value);
                template.opsForValue().set(key, json, minutes, TimeUnit.MINUTES);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("存入缓存失败", e);
            }
        }

        /**
         * 获取缓存（简单对象反序列化）
         */
        public <T> T get(String key, Class<T> clazz) {
            String json = template.opsForValue().get(key);
            if (json == null || json.isEmpty()) return null;

            try {
                return mapper.readValue(json, clazz);
            } catch (Exception e) {
                throw new RuntimeException("取出缓存失败", e);
            }
        }

        /**
         * 获取缓存（复杂对象反序列化，如 List、Map）
         */
        public <T> T get(String key, TypeReference<T> typeReference) {
            String json = template.opsForValue().get(key);
            if (json == null || json.isEmpty()) return null;

            try {
                return mapper.readValue(json, typeReference);
            } catch (Exception e) {
                throw new RuntimeException("取出缓存失败", e);
            }
        }

        /**
         * 删除单个缓存
         */
        public void delete(String key) {
            template.delete(key);
        }

        /**
         * 按前缀批量删除缓存
         */
        public void deleteByPrefix(String prefix) {
            Set<String> keys = template.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                template.delete(keys);
            }
        }

        /**
         * 设置缓存（带过期时间，支持自定义时间单位）
         */
        public void set(String key, Object value, long timeout, TimeUnit unit) {
            try {
                String json = mapper.writeValueAsString(value);
                template.opsForValue().set(key, json, timeout, unit);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("存入缓存失败", e);
            }
        }

        /**
         * 仅当 key 不存在时设置（分布式锁场景常用）
         */
        public Boolean setIfAbsent(String key, Object value, long minutes) {
            try {
                String json = mapper.writeValueAsString(value);
                return template.opsForValue().setIfAbsent(key, json, minutes, TimeUnit.MINUTES);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("存入缓存失败", e);
            }
        }
    }

    // ==================== 内部类：处理 ZSet 类型（排行榜） ====================

    public static class ZSetOps {
        private final StringRedisTemplate template;

        ZSetOps(StringRedisTemplate template) {
            this.template = template;
        }

        /**
         * 增加分数（比如商品卖出一件，销量+1）
         */
        public Double incrementScore(String key, String member, double score) {
            return template.opsForZSet().incrementScore(key, member, score);
        }

        /**
         * 获取排行榜（倒序取前 N 名）
         */
        public Set<String> reverseRange(String key, long start, long end) {
            return template.opsForZSet().reverseRange(key, start, end);
        }

        /**
         * 获取排行榜（倒序，带分数）
         */
        public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, long start, long end) {
            return template.opsForZSet().reverseRangeWithScores(key, start, end);
        }

        /**
         * 获取成员的排名（倒序，从 0 开始）
         */
        public Long reverseRank(String key, String member) {
            return template.opsForZSet().reverseRank(key, member);
        }

        /**
         * 获取成员的分数
         */
        public Double score(String key, String member) {
            return template.opsForZSet().score(key, member);
        }

        /**
         * 获取集合大小
         */
        public Long size(String key) {
            return template.opsForZSet().zCard(key);
        }

        /**
         * 删除成员
         */
        public Long remove(String key, Object... members) {
            return template.opsForZSet().remove(key, members);
        }
    }

    // ==================== 内部类：处理 Hash 类型（对象属性存储） ====================

    public static class HashOps {
        private final StringRedisTemplate template;
        private final ObjectMapper mapper;

        HashOps(StringRedisTemplate template, ObjectMapper mapper) {
            this.template = template;
            this.mapper = mapper;
        }

        /**
         * 设置 Hash 字段值
         */
        public void put(String key, String field, Object value) {
            try {
                String json = mapper.writeValueAsString(value);
                template.opsForHash().put(key, field, json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("存入 Hash 缓存失败", e);
            }
        }

        /**
         * 获取 Hash 字段值
         */
        public <T> T get(String key, String field, Class<T> clazz) {
            Object value = template.opsForHash().get(key, field);
            if (value == null) return null;

            try {
                return mapper.readValue(value.toString(), clazz);
            } catch (Exception e) {
                throw new RuntimeException("取出 Hash 缓存失败", e);
            }
        }

        /**
         * 获取整个 Hash
         */
        public <T> Map<String, T> getAll(String key, Class<T> valueClass) {
            Map<Object, Object> entries = template.opsForHash().entries(key);
            if (entries.isEmpty()) return new java.util.HashMap<>();

            try {
                Map<String, T> result = new HashMap<>();
                for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                    result.put(entry.getKey().toString(),
                            mapper.readValue(entry.getValue().toString(), valueClass));
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException("取出 Hash 缓存失败", e);
            }
        }

        /**
         * 删除 Hash 字段
         */
        public Long delete(String key, String... fields) {
            return template.opsForHash().delete(key, (Object[]) fields);
        }

        /**
         * 检查 Hash 字段是否存在
         */
        public Boolean hasKey(String key, String field) {
            return template.opsForHash().hasKey(key, field);
        }

        /**
         * 获取 Hash 所有字段
         */
        public Set<Object> keys(String key) {
            return template.opsForHash().keys(key);
        }

        /**
         * 获取 Hash 所有值
         */
        public List<Object> values(String key) {
            return template.opsForHash().values(key);
        }
    }
}
