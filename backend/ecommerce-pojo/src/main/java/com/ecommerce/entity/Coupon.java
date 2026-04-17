package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("coupon")
public class Coupon {
    /**
     * 优惠券ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 优惠券简介
     */
    private String description;
    
    /**
     * 分类ID，关联category表
     */
    private Integer categoryId;
    
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
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}