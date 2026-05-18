package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.UserCouponDTO;
import com.ecommerce.entity.Coupon;
import com.ecommerce.entity.UserCoupon;
import com.ecommerce.exception.BaseException;
import com.ecommerce.mapper.CouponMapper;
import com.ecommerce.mapper.UserCouponMapper;
import com.ecommerce.service.User.UserCouponService;
import com.ecommerce.vo.UserCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户优惠券服务实现类
 */
@Slf4j
@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    @Transactional
    public UserCouponVO receiveCoupon(UserCouponDTO userCouponDTO) {
        log.info("领取优惠券请求: userId={}, couponId={}", userCouponDTO.getUserId(), userCouponDTO.getCouponId());
        
        // 1. 检查优惠券是否存在且有效
        Coupon coupon = couponMapper.selectById(userCouponDTO.getCouponId());
        if (coupon == null) {
            throw new BaseException("优惠券不存在");
        }
        if (coupon.getStatus() != 1) {
            throw new BaseException("优惠券已下架");
        }
        if (coupon.getStock() <= 0) {
            throw new BaseException("优惠券已被抢完");
        }
        
        // 2. 检查是否在领取时间范围内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime())) {
            throw new BaseException("优惠券还未开始发放");
        }
        if (now.isAfter(coupon.getEndTime())) {
            throw new BaseException("优惠券已过期");
        }
        
        // 3. 检查用户是否已领取过该优惠券
        // 注意：数据库通常会对 (user_id, coupon_id) 做唯一约束，表示“同一用户同一优惠券只能领一次”。
        // 所以这里不能只查 status=0，否则用户领取后再使用/过期（status=1/2）时，仍会触发唯一键冲突。
        UserCoupon existingCoupon = query()
                .eq("user_id", userCouponDTO.getUserId())
                .eq("coupon_id", userCouponDTO.getCouponId())
                .one();
        if (existingCoupon != null) {
            throw new BaseException("您已领取过该优惠券");
        }
        
        // 4. 扣减优惠券库存
        coupon.setStock(coupon.getStock() - 1);
        couponMapper.updateById(coupon);
        
        // 5. 创建用户优惠券记录
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userCouponDTO.getUserId())
                .couponId(userCouponDTO.getCouponId())
                .status(0) // 未使用
                .orderId(null) // 未使用，订单ID为空
                .getTime(now) // 领取时间
                .useTime(null) // 未使用，使用时间为空
                .expireTime(now.plusDays(coupon.getValidPeriod())) // 过期时间
                .build();
        try {
            save(userCoupon);
        } catch (DuplicateKeyException e) {
            // 并发/重复点击导致的唯一键冲突，转换为可读的业务异常
            throw new BaseException("您已领取过该优惠券");
        }

        log.info("领取优惠券成功: userCouponId={}", userCoupon.getId());
        
        // 6. 返回VO
        return convertToVO(userCoupon, coupon);
    }

    /**
     *
     * 查询用户的优惠券列表
     * @param userId 用户 ID
     * @return
     */
    @Override
    public List<UserCouponVO> getUserCouponList(Long userId) {
        log.info("查询用户优惠券列表：userId={}", userId);
        
        // 查询用户的所有优惠券
        List<UserCoupon> userCoupons = query()
                .eq("user_id", userId)
                .orderByDesc("get_time")
                .list();
        
        // 转换为 VO 列表
        return userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
                    return convertToVO(userCoupon, coupon);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为VO
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