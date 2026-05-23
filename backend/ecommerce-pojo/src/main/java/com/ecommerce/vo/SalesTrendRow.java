package com.ecommerce.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesTrendRow {
    private String date;
    private BigDecimal total;
}
