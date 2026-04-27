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
 * 订单商品明细实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_item")
public class OrderItem {
    /**
     * 订单商品 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单 ID
     */
    private Long orderId;
    
    /**
     * 商品 ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 商品分类 ID（用于优惠券匹配）
     */
    private Integer categoryId;
    
    /**
     * 单价
     */
    private BigDecimal price;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 小计
     */
    private BigDecimal totalPrice;
}