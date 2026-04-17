package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.entity.Coupon;
import com.ecommerce.vo.CouponVO;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService extends IService<Coupon> {
    /**
     * 查询优惠券列表
     * @return 优惠券列表
     */
    List<CouponVO> listCoupons();
}