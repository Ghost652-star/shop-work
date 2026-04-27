package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单优惠券实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_coupon")
public class OrderCoupon {
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单 ID
     */
    private Long orderId;
    
    /**
     * 优惠券 ID
     */
    private Long couponId;
    
    /**
     * 分类 ID（冗余字段）
     */
    private Integer categoryId;
    
    /**
     * 该优惠券的抵扣金额
     */
    private BigDecimal discountAmount;
}