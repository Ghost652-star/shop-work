package com.ecommerce.dto;

import lombok.Data;

@Data
public class ShopAfterSaleHandleDTO {
    private Long afterSaleId;
    private Integer status;
    private String adminRemark;
}
