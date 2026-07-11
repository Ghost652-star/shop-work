package com.ecommerce.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 优惠券秒杀 MQ 消息体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponSeckillMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 优惠券 ID
     */
    private Long couponId;

    /**
     * 幂等键（userId + ":" + couponId），消费者端用于防重复
     */
    private String idempotentKey;
}
