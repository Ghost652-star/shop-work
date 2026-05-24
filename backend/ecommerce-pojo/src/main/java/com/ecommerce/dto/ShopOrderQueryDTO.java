package com.ecommerce.dto;

import lombok.Data;

@Data
public class ShopOrderQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private Integer status;
    private Long merchantId;
}
