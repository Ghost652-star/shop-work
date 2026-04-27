package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Order {
    /**
     * 订单 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消
     */
    private Integer status;
    
    /**
     * 总金额（优惠券抵扣前）
     */
    private BigDecimal totalAmount;
    
    /**
     * 运费
     */
    private BigDecimal freightAmount;
    
    /**
     * 优惠券总抵扣金额
     */
    private BigDecimal couponAmount;
    
    /**
     * 实付金额（总金额 + 运费 - 优惠券总抵扣）
     */
    private BigDecimal payAmount;
    
    /**
     * 支付方式
     */
    private String paymentType;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 收货人
     */
    private String receiverName;
    
    /**
     * 收货电话
     */
    private String receiverPhone;
    
    /**
     * 收货省份
     */
    private String receiverProvince;
    
    /**
     * 收货城市
     */
    private String receiverCity;
    
    /**
     * 收货区/县
     */
    private String receiverDistrict;
    
    /**
     * 收货详细地址
     */
    private String receiverDetailAddress;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}