package com.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testRedisConnection() {
        // 存
        redisTemplate.opsForValue().set("testKey", "hello redis");

        // 取
        String value = redisTemplate.opsForValue().get("testKey");

        // 断言：值应该是 "hello redis"
        assertEquals("hello redis", value);

        System.out.println("Redis 连接正常，值: " + value);
    }
} 