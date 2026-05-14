package com.ecommerce.dto;

import lombok.Data;

@Data
public class ShopProductQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String name;
    private Integer status;
    private Integer categoryId;
}
