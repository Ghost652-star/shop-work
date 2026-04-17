package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 地址视图对象
 * 用于返回给前端的地址数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressVO {
    /**
     * 地址 ID
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
