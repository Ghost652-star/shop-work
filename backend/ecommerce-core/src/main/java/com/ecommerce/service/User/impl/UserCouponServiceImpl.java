package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.config.RabbitMQConfig;
import com.ecommerce.dto.UserCouponDTO;
import com.ecommerce.entity.Coupon;
import com.ecommerce.entity.UserCoupon;
import com.ecommerce.common.exception.CouponException;
import com.ecommerce.mapper.CouponMapper;
import com.ecommerce.mapper.UserCouponMapper;
import com.ecommerce.mq.CouponSeckillMqProducer;
import com.ecommerce.service.User.UserCouponService;
import com.ecommerce.vo.UserCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户优惠券服务实现类（秒杀版：Redis Lua + MQ 削峰）
 */
@Slf4j
@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("couponSeckillScript")
    private DefaultRedisScript<Long> couponSeckillScript;

    @Autowired
    private CouponSeckillMqProducer mqProducer;

    @Override
    public UserCouponVO receiveCoupon(UserCouponDTO userCouponDTO) {
        Long userId = userCouponDTO.getUserId();
        Long couponId = userCouponDTO.getCouponId();
        log.info("领取优惠券请求: userId={}, couponId={}", userId, couponId);

        // 1. 校验优惠券（DB 读取，这些数据变化不频繁）
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new CouponException("优惠券不存在");
        }
        if (coupon.getStatus() != 1) {
            throw new CouponException("优惠券已下架");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime())) {
            throw new CouponException("优惠券还未开始发放");
        }
        if (now.isAfter(coupon.getEndTime())) {
            throw new CouponException("优惠券已过期");
        }

        // 2. 执行 Redis Lua 脚本（原子：检查库存 + 防重复 + 扣库存）
        String stockKey = RabbitMQConfig.COUPON_STOCK_KEY + couponId;
        String claimedKey = RabbitMQConfig.COUPON_CLAIMED_KEY + couponId;

        Long result = stringRedisTemplate.execute(
                couponSeckillScript,
                Arrays.asList(stockKey, claimedKey),
                String.valueOf(userId)
        );

        if (result == null) {
            throw new CouponException("系统繁忙，请稍后再试");
        }

        switch (result.intValue()) {
            case 0:
                // 成功 → 发 MQ 异步写库
                log.info("Lua 脚本执行成功: userId={}, couponId={}", userId, couponId);
                break;
            case 1:
                throw new CouponException("优惠券已被抢完");
            case 2:
                throw new CouponException("您已领取过该优惠券");
            default:
                throw new CouponException("系统繁忙，请稍后再试");
        }

        // 3. 发送 MQ 消息，异步写 MySQL
        mqProducer.sendSeckillMessage(userId, couponId);

        // 4. 立即返回成功（DB 稍后追上）
        return UserCouponVO.builder()
                .userId(userId)
                .couponId(couponId)
                .description(coupon.getDescription())
                .minSpend(coupon.getMinSpend())
                .discountAmount(coupon.getDiscountAmount())
                .status(0)
                .getTime(now)
                .expireTime(now.plusDays(coupon.getValidPeriod()))
                .build();
    }

    /**
     * 预热优惠券库存到 Redis（优惠券上架/启动时调用）
     */
    public void preloadCouponStock(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            log.warn("无法预热库存: couponId={}, 优惠券不存在或已下架", couponId);
            return;
        }

        String stockKey = RabbitMQConfig.COUPON_STOCK_KEY + couponId;
        stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(coupon.getStock()));
        log.info("优惠券库存已预热: couponId={}, stock={}", couponId, coupon.getStock());
    }

    /**
     * 查询用户的优惠券列表（不变）
     */
    @Override
    public List<UserCouponVO> getUserCouponList(Long userId) {
        log.info("查询用户优惠券列表：userId={}", userId);

        List<UserCoupon> userCoupons = query()
                .eq("user_id", userId)
                .orderByDesc("get_time")
                .list();

        return userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
                    return convertToVO(userCoupon, coupon);
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为 VO
     */
    private UserCouponVO convertToVO(UserCoupon userCoupon, Coupon coupon) {
        return UserCouponVO.builder()
                .id(userCoupon.getId())
                .userId(userCoupon.getUserId())
                .couponId(userCoupon.getCouponId())
                .description(coupon.getDescription())
                .minSpend(coupon.getMinSpend())
                .discountAmount(coupon.getDiscountAmount())
                .status(userCoupon.getStatus())
                .orderId(userCoupon.getOrderId())
                .getTime(userCoupon.getGetTime())
                .useTime(userCoupon.getUseTime())
                .expireTime(userCoupon.getExpireTime())
                .build();
    }
}
