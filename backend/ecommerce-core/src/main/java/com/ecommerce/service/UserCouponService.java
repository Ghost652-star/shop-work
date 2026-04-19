package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.UserCouponDTO;
import com.ecommerce.entity.UserCoupon;
import com.ecommerce.vo.UserCouponVO;

import java.util.List;

/**
 * 用户优惠券服务接口
 */
public interface UserCouponService extends IService<UserCoupon> {
    
    /**
     * 领取优惠券
     * @param userCouponDTO 用户优惠券请求 DTO
     * @return 用户优惠券 VO
     */
    UserCouponVO receiveCoupon(UserCouponDTO userCouponDTO);
    
    /**
     * 查询用户优惠券列表
     * @param userId 用户 ID
     * @return 用户优惠券 VO 列表
     */
    List<UserCouponVO> getUserCouponList(Long userId);
}