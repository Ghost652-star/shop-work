package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址请求 DTO
 * 用于接收前端提交的地址数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    /**
     * 地址 ID（新增时不需要）
     */
    private Integer id;
    
    /**
     * 用户 ID
     */
    private Integer userId;
    
    /**
     * 收货人姓名
     */
    private String name;
    
    /**
     * 收货人电话
     */
    private String phone;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区/县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 是否默认：0-否，1-是
     */
    private Integer isDefault;
}
