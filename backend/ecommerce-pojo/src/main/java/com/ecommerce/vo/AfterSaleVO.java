package com.ecommerce.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AfterSaleVO {
    private Long id;
    private String orderNo;
    private Long orderId;
    private String reason;
    private String description;
    private String images;
    private BigDecimal refundAmount;
    private Integer status;
    private String statusText;
    private String adminRemark;
    private String createTime;
    private List<AfterSaleItemVO> items;
}
