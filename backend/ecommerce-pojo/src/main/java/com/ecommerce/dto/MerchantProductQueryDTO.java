package com.ecommerce.dto;

import lombok.Data;

/**
 * 商家商品查询参数
 */
@Data
public class MerchantProductQueryDTO {
    /**
     * 排序方式：composite-综合，sales-销量
     */
    private String sort = "composite";

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;
}
