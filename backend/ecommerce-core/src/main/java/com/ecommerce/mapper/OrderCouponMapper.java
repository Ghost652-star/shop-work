package com.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.entity.OrderCoupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单优惠券 Mapper
 */
@Mapper
public interface OrderCouponMapper extends BaseMapper<OrderCoupon> {
}
