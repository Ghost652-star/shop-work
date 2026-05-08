package com.ecommerce.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AfterSaleDTO {
    private Long userId;
    private Long orderId;
    private String reason;
    private String description;
    private String images;
    private List<Long> orderItemIds;
}
