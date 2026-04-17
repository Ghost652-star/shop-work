package com.ecommerce.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品视图对象
 * 用于返回给前端的商品数据，只包含前端需要的字段
 */
@Data
@Builder
public class ProductVO {
    /**
     * 商品ID
     */
    private Integer id;
    
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 商品销量
     */
    private Integer sales;
    
    /**
     * 商品主图
     */
    private String mainImage;
    
    /**
     * 分类ID
     */
    private Integer categoryId;
}