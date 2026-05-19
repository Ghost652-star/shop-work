package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Coupon;
import com.ecommerce.mapper.CouponMapper;
import com.ecommerce.service.User.CouponService;
import com.ecommerce.service.User.UserCategoryService;
import com.ecommerce.common.RedisKeys;
import com.ecommerce.utils.RedisCacheUtil;
import com.ecommerce.vo.CouponVO;
import com.ecommerce.vo.CategoryVO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 */
@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Autowired
    private UserCategoryService categoryService;

    private final RedisCacheUtil redisCacheUtil;

    public CouponServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    /**
     * 查询优惠券列表
     * @return 优惠券列表
     */
    @Override
    public List<CouponVO> listCoupons() {
        log.debug("查询启用状态的优惠券列表");

        // 检查缓存
        List<CouponVO> cached = redisCacheUtil.valueOps.get(RedisKeys.COUPONS_ACTIVE,
                new TypeReference<List<CouponVO>>() {});
        if (cached != null) {
            log.debug("优惠券列表缓存命中");
            return cached;
        }

        // 查询所有启用状态的优惠券
        List<Coupon> coupons = query().eq("status", 1).list();
        log.debug("查询到优惠券数量: {}", coupons.size());

        // 查询所有分类，用于关联分类名称
        List<CategoryVO> categoryVOs = categoryService.listCategories();
        Map<Integer, String> categoryMap = categoryVOs.stream()
                .collect(Collectors.toMap(CategoryVO::getId, CategoryVO::getName));

        // 转换为 CouponVO 并计算倒计时
        List<CouponVO> result = coupons.stream()
                .map(coupon -> convertToVO(coupon, categoryMap))
                .collect(Collectors.toList());

        redisCacheUtil.valueOps.set(RedisKeys.COUPONS_ACTIVE, result, 10);
        log.debug("优惠券列表已缓存");
        return result;
    }

    /**
     * 清除优惠券缓存（商家端修改优惠券时调用）
     */
    public void clearCouponCache() {
        redisCacheUtil.valueOps.delete(RedisKeys.COUPONS_ACTIVE);
        log.debug("优惠券缓存已清除");
    }
    
    /**
     * 将 Coupon 实体转换为 CouponVO
     * @param coupon 优惠券实体
     * @param categoryMap 分类ID到名称的映射
     * @return 优惠券视图对象
     */
    private CouponVO convertToVO(Coupon coupon, Map<Integer, String> categoryMap) {
        CouponVO vo = CouponVO.builder()
                .id(coupon.getId())
                .description(coupon.getDescription())
                .categoryId(coupon.getCategoryId())
                .categoryName(categoryMap.get(coupon.getCategoryId()))
                .minSpend(coupon.getMinSpend())
                .discountAmount(coupon.getDiscountAmount())
                .startTime(coupon.getStartTime())
                .endTime(coupon.getEndTime())
                .validPeriod(coupon.getValidPeriod())
                .stock(coupon.getStock())
                .image(coupon.getImage())
                .status(coupon.getStatus())
                .build();
        
        // 计算倒计时
        vo.setCountdown(calculateCountdown(coupon.getEndTime()));
        
        return vo;
    }
    
    /**
     * 计算倒计时
     * @param endTime 结束时间
     * @return 倒计时字符串，格式：HH:MM:SS
     */
    private String calculateCountdown(LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) {
            return "00:00:00";
        }
        
        long seconds = ChronoUnit.SECONDS.between(now, endTime);
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}