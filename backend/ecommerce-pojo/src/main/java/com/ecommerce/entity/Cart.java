package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("cart")
public class Cart {
    /**
     * 购物车 ID
     */
    @TableId(type = IdType.AUTO)
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
     * 商品名称（冗余字段）
     */
    private String productName;
    
    /**
     * 商品图片（冗余字段）
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
     * 商家ID
     */
    private Long merchantId;

    /**
     * 加入购物车时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
