package com.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 订单请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 地址 ID
     */
    private Long addressId;
    
    /**
     * 优惠券 ID 列表
     */
    //TODO 为什么是从前端传过来一个优惠券 ID 列表
    private List<Long> couponIds;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 订单商品项
     */
    private List<OrderItemDTO> items;
    
    /**
     * 支付方式（用于支付接口）
     */
    private String paymentType;
    
    /**
     * 订单 ID（用于取消订单接口）
     */
    private Long orderId;

    /**
     * 购物车记录 ID 列表（用于购物车勾选结算）
     */
    private List<Long> cartItemIds;
}