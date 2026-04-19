package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponVO {
    /**
     * 记录 ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 优惠券描述
     */
    private String description;
    
    /**
     * 最低消费金额
     */
    private BigDecimal minSpend;
    
    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 状态：0-未使用，1-已使用，2-已过期
     */
    private Integer status;
    
    /**
     * 使用该优惠券的订单 ID
     */
    private Long orderId;
    
    /**
     * 领取时间
     */
    private LocalDateTime getTime;
    
    /**
     * 使用时间
     */
    private LocalDateTime useTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}