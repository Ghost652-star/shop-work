package com.ecommerce.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券信息 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponInfoVO {
    /**
     * 用户优惠券 ID
     */
    private Long userCouponId;
    
    /**
     * 优惠券 ID
     */
    private Integer couponId;
    
    /**
     * 优惠券描述
     */
    private String description;
    
    /**
     * 最低消费金额
     */
    private BigDecimal minSpend;
    
    /**
     * 可抵扣金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 分类 ID
     */
    private Integer categoryId;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 实际可抵扣金额
     */
    private BigDecimal actualDiscount;
}
