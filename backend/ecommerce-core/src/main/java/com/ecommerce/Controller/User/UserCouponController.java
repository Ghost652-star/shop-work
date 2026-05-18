package com.ecommerce.Controller.User;

import com.ecommerce.dto.UserCouponDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.UserCouponService;
import com.ecommerce.vo.UserCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户优惠券控制器
 */
@Slf4j
@RestController
@RequestMapping("/userCoupon")
public class UserCouponController {

    private final UserCouponService userCouponService;

    public UserCouponController(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    /**
     * 领取优惠券
     * @param userCouponDTO 用户优惠券请求DTO
     * @return 用户优惠券VO
     */
    @PostMapping("/receive")
    public Result<UserCouponVO> receiveCoupon(@RequestBody UserCouponDTO userCouponDTO) {
        log.info("领取优惠券请求: userId={}, couponId={}", userCouponDTO.getUserId(), userCouponDTO.getCouponId());
        UserCouponVO userCouponVO = userCouponService.receiveCoupon(userCouponDTO);
        log.info("领取优惠券成功: userCouponId={}", userCouponVO.getId());
        return Result.success(userCouponVO);
    }

    /**
     * 查询用户优惠券列表
     * @param userId 用户ID
     * @return 用户优惠券VO列表
     */
    @GetMapping("/list")
    public Result<List<UserCouponVO>> getUserCouponList(@RequestParam Long userId) {
        log.info("查询用户优惠券列表请求: userId={}", userId);
        List<UserCouponVO> list = userCouponService.getUserCouponList(userId);
        log.info("查询用户优惠券列表成功: 共{}条", list.size());
        return Result.success(list);
    }
}