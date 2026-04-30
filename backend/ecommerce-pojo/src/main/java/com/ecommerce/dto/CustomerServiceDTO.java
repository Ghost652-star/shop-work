package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客服消息请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceDTO {
    /**
     * 消息内容
     */
    private String message;

    /**
     * 用户ID
     */
    private String userId;
}
