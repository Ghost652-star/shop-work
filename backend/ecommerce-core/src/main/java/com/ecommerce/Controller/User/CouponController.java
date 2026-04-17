package com.ecommerce.Controller.User;

import com.ecommerce.result.Result;
import com.ecommerce.service.CouponService;
import com.ecommerce.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 优惠券控制器
 */
@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponController {
    
   private final CouponService couponService;
    CouponController(CouponService couponService) {
        this.couponService = couponService;
    }
    /**
     * 查询优惠券列表
     * @return 优惠券列表
     */
    @GetMapping("/list")
    public Result<List<CouponVO>> list() {
        log.debug("查询优惠券列表请求");
        List<CouponVO> coupons = couponService.listCoupons();
        log.debug("查询到优惠券数量: {}", coupons.size());
        return Result.success(coupons);
    }
}