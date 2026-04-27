package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    /**
     * 购物车记录 ID（更新、删除时使用）
     */
    private Long id;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
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
     * 单价
     */
    private BigDecimal price;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 是否选中：0-未选中，1-选中
     */
    private Integer isChecked;
    
    /**
     * 批量删除时的 ID 列表
     */
    private List<Long> ids;
}
