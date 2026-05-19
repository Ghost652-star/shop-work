package com.ecommerce.Controller.Shop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FastAPIWebClient {

    private final WebClient webClient;

    
    public FastAPIWebClient(WebClient webClient) {
        this.webClient = webClient;
        log.info("FastAPIWebClient初始化成功，目标地址: http://127.0.0.1:8000");
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> sendProcessRequest(String message, String userId, String orderNo) {
        log.info("准备发送POST请求 - message: {}, userId: {}, orderNo: {}", message, userId, orderNo);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", message);
        requestBody.put("user_id", userId);
        if (orderNo != null && !orderNo.isEmpty()) {
            requestBody.put("order_no", orderNo);
        }

        Map<String, Object> response = webClient.post()
                .uri("/process")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Charset", "UTF-8")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("Python端响应: {}", response);
        return response;
    }
}
