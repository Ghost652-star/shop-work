package com.ecommerce.Controller.User;

import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.AvailableCouponDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.OrderService;
import com.ecommerce.vo.OrderVO;
import com.ecommerce.vo.AvailableCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

     OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     * @param orderDTO 订单请求 DTO
     * @return 订单 VO
     */
    @PostMapping("/create")
    public Result<OrderVO> createOrder(@RequestBody OrderDTO orderDTO) {
        log.info("创建订单请求: userId={}", orderDTO.getUserId());
        OrderVO orderVO = orderService.createOrder(orderDTO);
        log.info("创建订单成功: orderId={}, orderNo={}", orderVO.getId(), orderVO.getOrderNo());
        return Result.success(orderVO);
    }

    /**
     * 查询订单列表
     * @param userId 用户 ID
     * @param status 订单状态（可选）
     * @return 订单 VO 列表
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(@RequestParam Long userId, @RequestParam(required = false) Integer status) {
        log.info("查询订单列表请求: userId={}, status={}", userId, status);
        List<OrderVO> list = orderService.getOrderList(userId, status);
        log.info("查询订单列表成功: 共{}条", list.size());
        return Result.success(list);
    }

    /**
     * 查询订单详情
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @return 订单 VO
     */
    @GetMapping("/detail")
    public Result<OrderVO> getOrderDetail(@RequestParam Long orderId, @RequestParam Long userId) {
        log.info("查询订单详情请求: orderId={}, userId={}", orderId, userId);
        OrderVO orderVO = orderService.getOrderDetail(orderId, userId);
        log.info("查询订单详情成功: orderId={}", orderId);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @return 操作结果
     */
    @PutMapping("/cancel")
    public Result<Boolean> cancelOrder(@RequestParam Long orderId, @RequestParam Long userId) {
        log.info("取消订单请求: orderId={}, userId={}", orderId, userId);
        boolean result = orderService.cancelOrder(orderId, userId);
        log.info("取消订单成功: orderId={}", orderId);
        return Result.success(result);
    }

    /**
     * 支付订单
     * @param orderDTO 订单请求 DTO
     * @return 操作结果
     */
    @PostMapping("/pay")
    public Result<Boolean> payOrder(@RequestBody OrderDTO orderDTO) {
        log.info("支付订单请求: orderId={}, userId={}, paymentType={}", 
                orderDTO.getOrderId(), orderDTO.getUserId(), orderDTO.getPaymentType());
        boolean result = orderService.payOrder(orderDTO.getOrderId(), orderDTO.getUserId(), orderDTO.getPaymentType());
        log.info("支付订单成功: orderId={}", orderDTO.getOrderId());
        return Result.success(result);
    }

    /**
     * 获取可用优惠券
     * @param availableCouponDTO 可用优惠券请求 DTO
     * @return 可用优惠券 VO
     */
    @PostMapping("/coupons/available")
    public Result<AvailableCouponVO> getAvailableCoupons(@RequestBody AvailableCouponDTO availableCouponDTO) {
        log.info("获取可用优惠券请求: userId={}", availableCouponDTO.getUserId());
        AvailableCouponVO availableCouponVO = orderService.getAvailableCoupons(availableCouponDTO);
        log.info("获取可用优惠券成功: 可用{}张, 不可用{}张", 
                availableCouponVO.getAvailable().size(), availableCouponVO.getUnavailable().size());
        return Result.success(availableCouponVO);
    }
}
