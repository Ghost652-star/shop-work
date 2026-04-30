package com.ecommerce.Controller.Shop;

import com.ecommerce.dto.CustomerServiceDTO;
import com.ecommerce.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 客服控制器
 */
@Slf4j
@RestController
@RequestMapping("/shop/customer-service")
public class CustomerServiceController {

    private final FastAPIWebClient fastAPIWebClient;

    public CustomerServiceController(FastAPIWebClient fastAPIWebClient) {
        this.fastAPIWebClient = fastAPIWebClient;
    }

    /**
     * 处理客服消息请求
     * @param dto 客服消息DTO
     * @return 处理结果
     */
    @PostMapping("/process")
    public Result<Object> processMessage(@RequestBody CustomerServiceDTO dto) {
        log.info("接收到客服请求: message={}, userId={}", dto.getMessage(), dto.getUserId());

        String message = dto.getMessage();
        String userId = dto.getUserId();

        if (message == null || message.isEmpty()) {
            return Result.error("消息内容不能为空");
        }

        if (userId == null || userId.isEmpty()) {
            userId = "default_user";
        }

        try {
            Map<String, Object> response = fastAPIWebClient.sendProcessRequest(message, userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("调用Python服务失败", e);
            return Result.error("调用Python服务失败: " + e.getMessage());
        }
    }
}
