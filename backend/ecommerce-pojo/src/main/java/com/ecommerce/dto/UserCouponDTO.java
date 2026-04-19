package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户优惠券请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponDTO {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
}