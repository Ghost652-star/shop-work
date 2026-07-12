package com.ecommerce.mq;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ecommerce.config.RabbitMQConfig;
import com.ecommerce.entity.Coupon;
import com.ecommerce.entity.UserCoupon;
import com.ecommerce.mapper.CouponMapper;
import com.ecommerce.mapper.UserCouponMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 优惠券秒杀消息消费者
 * <p>
 * 消费 MQ 消息，异步将领取记录写入 MySQL。
 * 唯一键 (user_id, coupon_id) 保证幂等。
 * 失败时回滚 Redis 库存和已领取标记，确保最终一致。
 */
@Slf4j
@Component
public class CouponSeckillMqConsumer {

    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public CouponSeckillMqConsumer(UserCouponMapper userCouponMapper,
                                   CouponMapper couponMapper,
                                   StringRedisTemplate stringRedisTemplate) {
        this.userCouponMapper = userCouponMapper;
        this.couponMapper = couponMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.COUPON_SECKILL_QUEUE)
    @Transactional
    public void handleSeckillMessage(CouponSeckillMessage message,
                                     Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        Long userId = message.getUserId();
        Long couponId = message.getCouponId();
        log.info("消费秒杀消息: userId={}, couponId={}", userId, couponId);

        try {
            // 1. 查询优惠券（用于获取有效期等字段）
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                log.error("优惠券不存在: couponId={}", couponId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 2. 检查 DB 库存（Redis 库存可能因补偿而虚高，DB 是最终真相）
            if (coupon.getStock() <= 0) {
                log.warn("DB库存已耗尽，补偿Redis后丢弃消息: couponId={}", couponId);
                compensateRedis(couponId, userId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 3. 插入用户优惠券记录（唯一键防重复消费）
            LocalDateTime now = LocalDateTime.now();
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .status(0)
                    .getTime(now)
                    .expireTime(now.plusDays(coupon.getValidPeriod()))
                    .build();

            try {
                userCouponMapper.insert(userCoupon);
            } catch (DuplicateKeyException e) {
                // 重复消息，幂等处理：直接确认
                log.info("重复消息，跳过: userId={}, couponId={}", userId, couponId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 4. 原子扣减数据库库存（SQL 级别，避免并发读取脏数据）
            int affected = couponMapper.update(null,
                    Wrappers.<Coupon>lambdaUpdate()
                            .setSql("stock = stock - 1")
                            .eq(Coupon::getId, couponId.intValue())
                            .gt(Coupon::getStock, 0));
            if (affected == 0) {
                throw new RuntimeException("DB库存扣减失败: couponId=" + couponId);
            }

            // 5. 成功 → 手动确认
            channel.basicAck(deliveryTag, false);
            log.info("秒杀入库成功: userId={}, couponId={}, userCouponId={}",
                    userId, couponId, userCoupon.getId());

        } catch (DuplicateKeyException e) {
            // 捕获可能在第二步再次出现的唯一键冲突
            log.info("重复消息(二次捕获): userId={}, couponId={}", userId, couponId);
            safeAck(channel, deliveryTag);

        } catch (Exception e) {
            log.error("消费秒杀消息失败，补偿Redis后丢弃消息: userId={}, couponId={}", userId, couponId, e);

            // 补偿 Redis：退回库存 + 移除已领取标记（只做一次，不重入队避免重复补偿）
            compensateRedis(couponId, userId);

            // requeue=false：直接丢弃，避免无限重试导致补偿多次、库存虚高
            safeNack(channel, deliveryTag);
        }
    }

    /**
     * 补偿 Redis：失败时退回库存、移除已领取标记，保持最终一致
     */
    private void compensateRedis(Long couponId, Long userId) {
        try {
            String stockKey = RabbitMQConfig.COUPON_STOCK_KEY + couponId;
            String claimedKey = RabbitMQConfig.COUPON_CLAIMED_KEY + couponId;

            stringRedisTemplate.opsForValue().increment(stockKey);   // 库存 +1
            stringRedisTemplate.opsForSet().remove(claimedKey, String.valueOf(userId)); // 移除标记
            log.info("Redis 已补偿: couponId={}, userId={}", couponId, userId);
        } catch (Exception ex) {
            log.error("Redis 补偿失败，需要人工处理: couponId={}, userId={}", couponId, userId, ex);
        }
    }

    private void safeAck(Channel channel, long deliveryTag) {
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("确认消息失败", e);
        }
    }

    private void safeNack(Channel channel, long deliveryTag) {
        try {
            channel.basicReject(deliveryTag, false); // requeue=false，丢弃消息，避免重复补偿
        } catch (IOException e) {
            log.error("拒绝消息失败", e);
        }
    }
}
