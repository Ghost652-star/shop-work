package com.ecommerce.mq;

import com.ecommerce.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 优惠券秒杀消息生产者
 */
@Slf4j
@Component
public class CouponSeckillMqProducer {

    private final RabbitTemplate rabbitTemplate;

    public CouponSeckillMqProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送秒杀成功消息到 MQ，由消费者异步写库
     */
    public void sendSeckillMessage(Long userId, Long couponId) {
        CouponSeckillMessage message = CouponSeckillMessage.builder()
                .userId(userId)
                .couponId(couponId)
                .idempotentKey(userId + ":" + couponId)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COUPON_SECKILL_EXCHANGE,
                RabbitMQConfig.COUPON_SECKILL_ROUTING_KEY,
                message
        );

        log.info("秒杀消息已发送: userId={}, couponId={}", userId, couponId);
    }
}
