package com.ecommerce.utils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheUtil {
    
    private  final StringRedisTemplate stringRedisTemplate;
   private  final ObjectMapper objectMapper;


   public RedisCacheUtil(ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
    this.objectMapper = objectMapper;
    this.stringRedisTemplate = stringRedisTemplate;
}

/**
 * 设置缓存
 * @param key
 * @param value
 * @param minutes
 */
   public void set(String key, Object value, long minutes) {
        try {
           
            String json = objectMapper.writeValueAsString(value); 
            stringRedisTemplate.opsForValue().set(key, json, minutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("存入缓存失败", e);
        }
    }

/**
 * 获取简单缓存对象
 * @param key
 * @param clazz
 * @return
 * @param <T>
 */
public <T> T get(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.isEmpty()) return null;
        
        try {
            return objectMapper.readValue(json, clazz); 
        } catch (Exception e) {
            throw new RuntimeException("取出缓存失败", e);
        }
    }

/**
 * 获取复杂缓存对象
 * @param key
 * @param typeReference
 * @return
 * @param <T>
 */
 public <T> T get(String key, TypeReference<T> typeReference) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.isEmpty()) return null;

        try {

            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("取出缓存失败", e);
        }
    }

/**
 * 删除单个缓存
 * @param key
 */
public void delete(String key) {
    stringRedisTemplate.delete(key);
}

/**
 * 按前缀批量删除缓存
 * @param prefix 前缀，如 "product:" 会删除所有 product:1, product:2 等
 */
public void deleteByPrefix(String prefix) {
    Set<String> keys = stringRedisTemplate.keys(prefix + "*");
    if (keys != null && !keys.isEmpty()) {
        stringRedisTemplate.delete(keys);
    }
}

}
