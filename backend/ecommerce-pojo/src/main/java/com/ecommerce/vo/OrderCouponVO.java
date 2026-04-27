package com.ecommerce.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单优惠券响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCouponVO {
    /**
     * 订单优惠券 ID
     */
    private Long id;
    
    /**
     * 优惠券 ID
     */
    private Long couponId;
    
    /**
     * 优惠券描述
     */
    private String description;
    
    /**
     * 分类 ID
     */
    private Integer categoryId;
    
    /**
     * 抵扣金额
     */
    private BigDecimal discountAmount;
}
