package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商家视图对象
 * 用于返回给前端的商家数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantVO {
    /**
     * 商家ID
     */
    private Integer id;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 商家描述
     */
    private String description;

    /**
     * Logo URL
     */
    private String logo;

    /**
     * 店铺评分
     */
    private BigDecimal score;
}
