package com.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopProductSaveDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer categoryId;
    private String mainImage;
}
