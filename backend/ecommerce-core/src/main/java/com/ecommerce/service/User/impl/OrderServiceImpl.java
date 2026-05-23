package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.common.RedisKeys;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.AvailableCouponDTO;
import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.entity.*;
import com.ecommerce.common.exception.AddressException;
import com.ecommerce.common.exception.CartException;
import com.ecommerce.common.exception.CouponException;
import com.ecommerce.common.exception.OrderException;
import com.ecommerce.common.exception.ProductException;
import com.ecommerce.mapper.*;
import com.ecommerce.service.User.OrderService;
import com.ecommerce.vo.OrderVO;
import com.ecommerce.vo.AvailableCouponVO;
import com.ecommerce.vo.OrderItemVO;
import com.ecommerce.vo.OrderCouponVO;
import com.ecommerce.vo.CouponInfoVO;
import com.ecommerce.vo.UnavailableCouponVO;
import lombok.extern.slf4j.Slf4j;
import com.ecommerce.utils.RedisCacheUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderCouponMapper orderCouponMapper;
    private final CartMapper cartMapper;
    private final UserCouponMapper userCouponMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;
    private final CouponMapper couponMapper;
    private final RedisCacheUtil redisCacheUtil;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                            OrderCouponMapper orderCouponMapper, CartMapper cartMapper,
                            UserCouponMapper userCouponMapper, ProductMapper productMapper,
                            AddressMapper addressMapper, CouponMapper couponMapper,
                            RedisCacheUtil redisCacheUtil) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderCouponMapper = orderCouponMapper;
        this.cartMapper = cartMapper;
        this.userCouponMapper = userCouponMapper;
        this.productMapper = productMapper;
        this.addressMapper = addressMapper;
        this.couponMapper = couponMapper;
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    @Transactional
    public OrderVO createOrder(OrderDTO orderDTO) {
        log.info("创建订单请求: userId={}, addressId={}, couponIds={}",
                orderDTO.getUserId(), orderDTO.getAddressId(), orderDTO.getCouponIds());

        // 1. 验证地址
        Address address = addressMapper.selectById(orderDTO.getAddressId().intValue());
        if (address == null || !address.getUserId().equals(orderDTO.getUserId().intValue())) {
            throw new AddressException("地址不存在或不属于当前用户");
        }

        // 2. 从购物车获取商品项
        List<Cart> cartItems = Collections.emptyList();
        List<OrderItemDTO> requestItems = orderDTO.getItems();
        if (orderDTO.getCartItemIds() != null && !orderDTO.getCartItemIds().isEmpty()) {
            cartItems = cartMapper.selectList(new QueryWrapper<Cart>()
                    .eq("user_id", orderDTO.getUserId())
                    .in("id", orderDTO.getCartItemIds()));
            if (cartItems.size() != orderDTO.getCartItemIds().size()) {
                throw new CartException("购物车商品不存在或不属于当前用户");
            }
            boolean hasUnchecked = cartItems.stream()
                    .anyMatch(item -> item.getIsChecked() == null || item.getIsChecked() != 1);
            if (hasUnchecked) {
                throw new CartException("存在未勾选的购物车商品");
            }
            requestItems = cartItems.stream()
                    .map(item -> OrderItemDTO.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .build())
                    .collect(Collectors.toList());
        }
        if (requestItems == null || requestItems.isEmpty()) {
            throw new OrderException("订单商品不能为空");
        }

        // 3. 批量查询商品，避免循环查询导致的 N+1 问题
        List<Long> productIds = requestItems.stream()
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(p -> p.getId().longValue(), p -> p));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<Integer> categoryIds = new HashSet<>();

        for (OrderItemDTO item : requestItems) {
            Product product = productMap.get(item.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new ProductException("商品不存在或已下架");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new ProductException("商品库存不足");
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            categoryIds.add(product.getCategoryId());

            orderItems.add(OrderItem.builder()
                    .productId(product.getId().longValue())
                    .productName(product.getName())
                    .productImage(product.getMainImage())
                    .categoryId(product.getCategoryId())
                    .price(product.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(itemTotal)
                    .build());
        }

        // 4. 批量查询优惠券，避免循环查询导致的 N+1 问题
        BigDecimal couponAmount = BigDecimal.ZERO;
        List<OrderCoupon> orderCoupons = new ArrayList<>();
        List<UserCoupon> usedUserCoupons = new ArrayList<>();
        Map<Long, Coupon> couponCache = new HashMap<>();

        if (orderDTO.getCouponIds() != null && !orderDTO.getCouponIds().isEmpty()) {
            // 批量查 user_coupon
            List<UserCoupon> userCoupons = userCouponMapper.selectBatchIds(orderDTO.getCouponIds());
            Map<Long, UserCoupon> userCouponMap = userCoupons.stream()
                    .collect(Collectors.toMap(UserCoupon::getId, uc -> uc));

            // 批量查 coupon 详情
            Set<Integer> couponDefIds = userCoupons.stream()
                    .map(uc -> uc.getCouponId().intValue())
                    .collect(Collectors.toSet());
            if (!couponDefIds.isEmpty()) {
                List<Coupon> couponDefs = couponMapper.selectBatchIds(couponDefIds);
                couponCache = couponDefs.stream()
                        .collect(Collectors.toMap(c -> c.getId().longValue(), c -> c));
            }

            for (Long couponId : orderDTO.getCouponIds()) {
                UserCoupon userCoupon = userCouponMap.get(couponId);
                if (userCoupon == null || !userCoupon.getUserId().equals(orderDTO.getUserId())) {
                    throw new CouponException("优惠券不存在或不属于当前用户");
                }
                if (userCoupon.getStatus() != 0) {
                    throw new CouponException("优惠券已使用或已过期");
                }
                if (LocalDateTime.now().isAfter(userCoupon.getExpireTime())) {
                    throw new CouponException("优惠券已过期");
                }

                Coupon coupon = couponCache.get(userCoupon.getCouponId());
                if (coupon == null) {
                    throw new CouponException("优惠券不存在");
                }
                if (totalAmount.compareTo(coupon.getMinSpend()) < 0) {
                    throw new CouponException("订单金额不满足优惠券使用条件");
                }
                if (coupon.getCategoryId() != null && !categoryIds.contains(coupon.getCategoryId())) {
                    throw new CouponException("商品不符合优惠券使用条件");
                }

                BigDecimal discount = coupon.getDiscountAmount();
                if (discount.compareTo(totalAmount) > 0) {
                    discount = totalAmount;
                }
                couponAmount = couponAmount.add(discount);

                orderCoupons.add(OrderCoupon.builder()
                        .couponId(coupon.getId().longValue())
                        .categoryId(coupon.getCategoryId())
                        .discountAmount(discount)
                        .build());
                usedUserCoupons.add(userCoupon);
            }
        }

        BigDecimal freightAmount = BigDecimal.valueOf(5);
        BigDecimal payAmount = totalAmount.add(freightAmount).subtract(couponAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        String orderNo = generateOrderNo();

        // 5. 创建订单
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(orderDTO.getUserId())
                .status(0)
                .totalAmount(totalAmount)
                .freightAmount(freightAmount)
                .couponAmount(couponAmount)
                .payAmount(payAmount)
                .receiverName(address.getName())
                .receiverPhone(address.getPhone())
                .receiverProvince(address.getProvince())
                .receiverCity(address.getCity())
                .receiverDistrict(address.getDistrict())
                .receiverDetailAddress(address.getDetailAddress())
                .remark(orderDTO.getRemark())
                .createTime(LocalDateTime.now())
                .build();
        orderMapper.insert(order);

        // 6. 插入订单明细（写操作必须逐条，确保数据一致性）
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        for (OrderCoupon oc : orderCoupons) {
            oc.setOrderId(order.getId());
            orderCouponMapper.insert(oc);
        }

        // 7. 更新用户优惠券状态
        for (UserCoupon userCoupon : usedUserCoupons) {
            userCoupon.setStatus(1);
            userCoupon.setOrderId(order.getId());
            userCoupon.setUseTime(LocalDateTime.now());
            userCouponMapper.updateById(userCoupon);
        }

        // 8. 原子扣减库存（使用 WHERE stock >= ? 防止超卖）
        for (OrderItemDTO item : requestItems) {
            int rows = productMapper.deductStock(item.getProductId(), item.getQuantity());
            if (rows == 0) {
                throw new ProductException("商品库存不足");
            }
        }

        // 9. 清空购物车
        if (orderDTO.getCartItemIds() != null && !orderDTO.getCartItemIds().isEmpty()) {
            cartMapper.delete(new QueryWrapper<Cart>()
                    .eq("user_id", orderDTO.getUserId())
                    .in("id", orderDTO.getCartItemIds()));
        }

        // 10. 构建返回结果（复用步骤 4 的 couponCache，避免重复查询）
        OrderVO orderVO = buildOrderVO(order, orderItems, orderCoupons, couponCache);
        log.info("创建订单成功: orderId={}, orderNo={}", order.getId(), orderNo);
        return orderVO;
    }

    /**
     * 查询订单列表
     * @param userId 用户 ID
     * @param status 订单状态（可选）
     * @return
     */
    @Override
    public List<OrderVO> getOrderList(Long userId, Integer status) {
        log.info("查询订单列表: userId={}, status={}", userId, status);

        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>()
                .eq("user_id", userId)
                .orderByDesc("create_time");

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        List<Order> orders = orderMapper.selectList(queryWrapper);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询所有关联数据，避免 N+1 查询问题
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        List<OrderItem> allItems = orderItemMapper.selectList(
                new QueryWrapper<OrderItem>().in("order_id", orderIds));
        List<OrderCoupon> allCoupons = orderCouponMapper.selectList(
                new QueryWrapper<OrderCoupon>().in("order_id", orderIds));

        // 批量查询优惠券详情（Coupon.id 是 Integer，需要转换）
        Set<Integer> couponIds = allCoupons.stream()
                .map(oc -> oc.getCouponId().intValue())
                .collect(Collectors.toSet());
        List<Coupon> allCouponDetails = couponIds.isEmpty()
                ? Collections.emptyList()
                : couponMapper.selectBatchIds(couponIds);
        Map<Long, Coupon> couponMap = allCouponDetails.stream()
                .collect(Collectors.toMap(c -> c.getId().longValue(), c -> c));

        // 按 order_id 分组，避免循环中重复查询
        Map<Long, List<OrderItem>> itemsByOrder = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        Map<Long, List<OrderCoupon>> couponsByOrder = allCoupons.stream()
                .collect(Collectors.groupingBy(OrderCoupon::getOrderId));

        return orders.stream().map(order -> {
            List<OrderItem> items = itemsByOrder.getOrDefault(order.getId(), Collections.emptyList());
            List<OrderCoupon> coupons = couponsByOrder.getOrDefault(order.getId(), Collections.emptyList());
            return buildOrderVO(order, items, coupons, couponMap);
        }).collect(Collectors.toList());
    }

    /**
     * 查询订单详情
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        log.info("查询订单详情: orderId={}, userId={}", orderId, userId);

        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new OrderException("订单不存在或不属于当前用户");
        }

        List<OrderItem> items = orderItemMapper.selectList(new QueryWrapper<OrderItem>()
                .eq("order_id", order.getId()));

        List<OrderCoupon> coupons = orderCouponMapper.selectList(new QueryWrapper<OrderCoupon>()
                .eq("order_id", order.getId()));

        // 批量查询优惠券详情，避免循环查询
        Set<Integer> couponIds = coupons.stream()
                .map(oc -> oc.getCouponId().intValue())
                .collect(Collectors.toSet());
        List<Coupon> couponDetails = couponIds.isEmpty()
                ? Collections.emptyList()
                : couponMapper.selectBatchIds(couponIds);
        Map<Long, Coupon> couponMap = couponDetails.stream()
                .collect(Collectors.toMap(c -> c.getId().longValue(), c -> c));

        return buildOrderVO(order, items, coupons, couponMap);
    }

    /**
     * 取消订单
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @return
     */
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        log.info("取消订单: orderId={}, userId={}", orderId, userId);
        
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new OrderException("订单不存在或不属于当前用户");
        }
        
        if (order.getStatus() != 0 && order.getStatus() != 1 && order.getStatus() != 2) {
            throw new OrderException("订单无法取消");
        }
        
        // 更新订单状态
        // TODO 已支付/待收货订单取消需要处理退款/售后流程
        order.setStatus(4); // 已取消
        orderMapper.updateById(order);
        
        // 恢复优惠券
        List<UserCoupon> userCoupons = userCouponMapper.selectList(new QueryWrapper<UserCoupon>()
                .eq("order_id", orderId)
                .eq("user_id", userId)
                .eq("status", 1));
        for (UserCoupon uc : userCoupons) {
            uc.setStatus(0); // 恢复为未使用
            uc.setOrderId(null);
            uc.setUseTime(null);
            userCouponMapper.updateById(uc);
        }

        // 恢复库存和销量
        List<OrderItem> items = orderItemMapper.selectList(new QueryWrapper<OrderItem>()
                .eq("order_id", orderId));

        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            product.setStock(product.getStock() + item.getQuantity());
            // 只有已支付的订单才需要恢复销量（状态 1、2、3）
            if (order.getStatus() >= 1) {
                product.setSales(product.getSales() - item.getQuantity());
                // 更新热销榜单 ZSet
                updateSalesRank(product.getId().longValue(), -item.getQuantity());
            }
            productMapper.updateById(product);
        }
        
        log.info("取消订单成功: orderId={}", orderId);
        return true;
    }

    /**
     * 支付订单
     * @param orderId 订单 ID
     * @param userId 用户 ID
     * @param paymentType 支付方式
     * @return
     */
    @Override
    @Transactional
    public boolean payOrder(Long orderId, Long userId, String paymentType) {
        log.info("支付订单: orderId={}, userId={}, paymentType={}", orderId, userId, paymentType);
        
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new OrderException("订单不存在或不属于当前用户");
        }
        
        if (order.getStatus() != 0) {
            throw new OrderException("只有待付款订单可以支付");
        }
        
        // 更新订单状态
        order.setStatus(1); // 待发货
        order.setPaymentType(paymentType);
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("支付订单成功: orderId={}", orderId);
        return true;
    }

    /**
     * 获取可用优惠券
     * @param availableCouponDTO 可用优惠券请求 DTO
     * @return
     */
    @Override
    public AvailableCouponVO getAvailableCoupons(AvailableCouponDTO availableCouponDTO) {
        log.info("获取可用优惠券: userId={}", availableCouponDTO.getUserId());

        // 计算订单总金额和商品分类
        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<Integer> categoryIds = new HashSet<>();

        for (OrderItemDTO item : availableCouponDTO.getItems()) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            categoryIds.add(item.getCategoryId());
        }
        
        // 查询用户所有未使用的优惠券
        List<UserCoupon> userCoupons = userCouponMapper.selectList(new QueryWrapper<UserCoupon>()
                .eq("user_id", availableCouponDTO.getUserId())
                .eq("status", 0));
        
        List<CouponInfoVO> availableCoupons = new ArrayList<>();
        List<UnavailableCouponVO> unavailableCoupons = new ArrayList<>();
        BigDecimal maxDiscount = BigDecimal.ZERO;
        
        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
            if (coupon == null) {
                continue;
            }
            
            boolean available = true;
            String reason = null;
            
            // 检查是否过期
            if (LocalDateTime.now().isAfter(userCoupon.getExpireTime())) {
                available = false;
                reason = "已过期";
            }
            // 检查金额条件
            else if (totalAmount.compareTo(coupon.getMinSpend()) < 0) {
                available = false;
                reason = "金额不满足最低消费";
            }
            // 检查分类条件
            else if (coupon.getCategoryId() != null && !categoryIds.contains(coupon.getCategoryId())) {
                available = false;
                reason = "商品不符合使用条件";
            }
            
            if (available) {
                // 计算实际可抵扣金额
                BigDecimal actualDiscount = coupon.getDiscountAmount();
                if (actualDiscount.compareTo(totalAmount) > 0) {
                    actualDiscount = totalAmount;
                }
                
                if (actualDiscount.compareTo(maxDiscount) > 0) {
                    maxDiscount = actualDiscount;
                }
                
                CouponInfoVO couponInfo = CouponInfoVO.builder()
                        .userCouponId(userCoupon.getId())
                        .couponId(coupon.getId())
                        .description(coupon.getDescription())
                        .minSpend(coupon.getMinSpend())
                        .discountAmount(coupon.getDiscountAmount())
                        .categoryId(coupon.getCategoryId())
                        .expireTime(userCoupon.getExpireTime())
                        .actualDiscount(actualDiscount)
                        .build();
                availableCoupons.add(couponInfo);
            } else {
                UnavailableCouponVO unavailableCoupon = UnavailableCouponVO.builder()
                        .userCouponId(userCoupon.getId())
                        .description(coupon.getDescription())
                        .reason(reason)
                        .build();
                unavailableCoupons.add(unavailableCoupon);
            }
        }
        
        return AvailableCouponVO.builder()
                .available(availableCoupons)
                .unavailable(unavailableCoupons)
                .totalAmount(totalAmount)
                .maxDiscount(maxDiscount)
                .build();
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = String.format("%03d", new Random().nextInt(1000));
        return "ORD" + timestamp + random;
    }

    /**
     * 构建订单 VO
     */
    private OrderVO buildOrderVO(Order order, List<OrderItem> items, List<OrderCoupon> coupons, Map<Long, Coupon> couponMap) {
        List<OrderItemVO> itemVOs = items.stream().map(item -> OrderItemVO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .categoryId(item.getCategoryId())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build()).collect(Collectors.toList());

        List<OrderCouponVO> couponVOs = coupons.stream().map(coupon -> {
            Coupon c = couponMap.get(coupon.getCouponId());
            return OrderCouponVO.builder()
                    .id(coupon.getId())
                    .couponId(coupon.getCouponId())
                    .description(c != null ? c.getDescription() : "")
                    .categoryId(coupon.getCategoryId())
                    .discountAmount(coupon.getDiscountAmount())
                    .build();
        }).collect(Collectors.toList());
        
        String statusText = getStatusText(order.getStatus());
        String receiverAddress = order.getReceiverProvince() + order.getReceiverCity() + 
                order.getReceiverDistrict() + order.getReceiverDetailAddress();
        
        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .status(order.getStatus())
                .statusText(statusText)
                .totalAmount(order.getTotalAmount())
                .freightAmount(order.getFreightAmount())
                .couponAmount(order.getCouponAmount())
                .payAmount(order.getPayAmount())
                .paymentType(order.getPaymentType())
                .paymentTime(order.getPaymentTime())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(receiverAddress)
                .remark(order.getRemark())
                .afterSaleStatus(order.getAfterSaleStatus())
                .createTime(order.getCreateTime())
                .items(itemVOs)
                .coupons(couponVOs)
                .build();
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "待收货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    @Override
    public boolean confirmOrder(Long orderId, Long userId) {
        log.info("确认收货: orderId={}, userId={}", orderId, userId);
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new OrderException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new OrderException("无权操作该订单");
        }
        if (order.getStatus() != 2) {
            throw new OrderException("仅待收货状态可确认收货");
        }
        order.setStatus(3);
        orderMapper.updateById(order);

        // 增加商品销量
        List<OrderItem> items = orderItemMapper.selectList(new QueryWrapper<OrderItem>()
                .eq("order_id", orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setSales(product.getSales() + item.getQuantity());
                productMapper.updateById(product);
                // 更新热销榜单 ZSet
                updateSalesRank(product.getId().longValue(), item.getQuantity());
            }
        }

        log.info("确认收货成功: orderId={}", orderId);
        return true;
    }

    /**
     * 更新热销榜单 ZSet
     */
    private void updateSalesRank(Long productId, int delta) {
        try {
            redisCacheUtil.zSetOps.incrementScore(
                    RedisKeys.SALES_RANK,
                    productId.toString(),
                    delta
            );
        } catch (Exception e) {
            log.error("更新热销榜单失败: productId={}", productId, e);
        }
    }
}
