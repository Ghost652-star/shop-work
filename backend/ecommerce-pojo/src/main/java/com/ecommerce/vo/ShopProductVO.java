package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductVO {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String categoryName;
    private String mainImage;
    private Integer status;
}
