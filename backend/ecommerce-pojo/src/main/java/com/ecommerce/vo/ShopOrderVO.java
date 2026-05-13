package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderVO {
    private Long id;
    private String orderNo;
    private String userName;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
}
