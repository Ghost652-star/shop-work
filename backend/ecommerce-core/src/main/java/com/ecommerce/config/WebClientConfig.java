package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient配置类
 * 用于与Python FastAPI服务通信
 */
@Configuration
public class WebClientConfig {

    /**
     * 配置WebClient Bean
     * 设置Python FastAPI服务的基础URL
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://127.0.0.1:8000") // Python FastAPI服务地址
                .build();
    }
}
