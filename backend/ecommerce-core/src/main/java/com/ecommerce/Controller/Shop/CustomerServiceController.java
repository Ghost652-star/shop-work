package com.ecommerce.Controller.Shop;

import com.ecommerce.client.FastAPIWebClient;
import com.ecommerce.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shop/customer-service")
public class CustomerServiceController {

    @Autowired
    private FastAPIWebClient fastAPIWebClient;

    @PostMapping(value = "/process", produces = "application/json; charset=utf-8")
    public Result<Object> processMessage(@RequestBody Map<String, String> requestData) {
        log.info("接收到客服请求: {}", requestData);

        String message = requestData.get("message");
        String userId = requestData.get("user_id");
        String orderNo = requestData.get("order_no");

        if (message == null || message.isEmpty()) {
            return Result.error("消息内容不能为空");
        }

        if (userId == null || userId.isEmpty()) {
            userId = "default_user";
        }

        try {
            Map<String, Object> response = fastAPIWebClient.sendProcessRequest(message, userId, orderNo);
            return Result.success(response);
        } catch (Exception e) {
            log.error("调用Python服务失败", e);
            return Result.error("调用Python服务失败: " + e.getMessage());
        }
    }
}
