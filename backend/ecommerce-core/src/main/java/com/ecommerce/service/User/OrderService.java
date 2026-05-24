package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.AvailableCouponDTO;
import com.ecommerce.entity.Order;
import com.ecommerce.vo.OrderVO;
import com.ecommerce.vo.AvailableCouponVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     * @param orderDTO 订单请求 DTO
     * @return 订单 VO
     */
    List<OrderVO> createOrder(OrderDTO orderDTO);
    
    /**
     * 查询订单列表
     * @param userId 用户 ID
     * @param status 订单状态（可选）
     * @return 订单 VO 列表
     */
    List<OrderVO> getOrderList(Long userId, Integer status);
    
    /**
     * 查询订单详情
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @return 订单 VO
     */
    OrderVO getOrderDetail(Long orderId, Long userId);
    
    /**
     * 取消订单
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @return 操作结果
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 支付订单
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @param paymentType 支付方式
     * @return 操作结果
     */
    boolean payOrder(String batchNo, Long userId, String paymentType);
    
    /**
     * 获取可用优惠券
     * @param availableCouponDTO 可用优惠券请求 DTO
     * @return 可用优惠券 VO
     */
    AvailableCouponVO getAvailableCoupons(AvailableCouponDTO availableCouponDTO);

    /**
     * 确认收货
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @return 操作结果
     */
    boolean confirmOrder(Long orderId, Long userId);
}
