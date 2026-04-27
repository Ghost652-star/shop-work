package com.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单商品项 DTO（通用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    /**
     * 商品 ID
     */
    private Long productId;
    
    /**
     * 分类 ID（用于优惠券计算）
     */
    private Integer categoryId;
    
    /**
     * 价格（用于优惠券计算）
     */
    private BigDecimal price;
    
    /**
     * 数量
     */
    private Integer quantity;
}
