package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_coupon")
public class UserCoupon {
    /**
     * 记录 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 优惠券 ID
     */
    private Long couponId;
    
    /**
     * 状态：0-未使用，1-已使用，2-已冻结，3-已过期
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