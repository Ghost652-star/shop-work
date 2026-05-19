package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券视图对象
 * 用于返回给前端的优惠券数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponVO {
    /**
     * 优惠券ID
     */
    private Integer id;
    
    /**
     * 优惠券简介
     */
    private String description;
    
    /**
     * 分类ID
     */
    private Integer categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 最低消费金额
     */
    private BigDecimal minSpend;
    
    /**
     * 可扣减金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 开始抢购时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束抢购时间
     */
    private LocalDateTime endTime;
    
    /**
     * 有效期（天数）
     */
    private Integer validPeriod;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 优惠券图片路径
     */
    private String image;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 倒计时（格式：HH:MM:SS）
     */
    private String countdown;
}