package com.ecommerce.dto;

import lombok.Data;

@Data
public class ShopAfterSaleQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private Integer status;
}
