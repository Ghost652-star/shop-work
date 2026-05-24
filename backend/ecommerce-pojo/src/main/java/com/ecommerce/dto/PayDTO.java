package com.ecommerce.dto;

import lombok.Data;

@Data
public class PayDTO {
    private String batchNo;
    private Long userId;
    private String paymentType;
}
