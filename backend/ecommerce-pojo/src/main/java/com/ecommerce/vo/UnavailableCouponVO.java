package com.ecommerce.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 不可用优惠券 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnavailableCouponVO {
    /**
     * 用户优惠券 ID
     */
    private Long userCouponId;
    
    /**
     * 优惠券描述
     */
    private String description;
    
    /**
     * 不可用原因
     */
    private String reason;
}
