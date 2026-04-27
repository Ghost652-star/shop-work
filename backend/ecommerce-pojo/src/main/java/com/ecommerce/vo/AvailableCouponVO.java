package com.ecommerce.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 可用优惠券响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableCouponVO {
    /**
     * 可用优惠券列表
     */
    private List<CouponInfoVO> available;
    
    /**
     * 不可用优惠券列表
     */
    private List<UnavailableCouponVO> unavailable;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 最大可抵扣金额
     */
    private BigDecimal maxDiscount;
}