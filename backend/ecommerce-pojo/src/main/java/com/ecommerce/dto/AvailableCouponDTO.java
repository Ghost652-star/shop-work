package com.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 可用优惠券请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableCouponDTO {
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 订单商品项
     */
    private List<OrderItemDTO> items;
}