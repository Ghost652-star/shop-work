package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.OrderItemMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.dto.ShopOrderQueryDTO;
import com.ecommerce.service.Shop.ShopOrderService;
import com.ecommerce.vo.OrderItemVO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopOrderDetailVO;
import com.ecommerce.vo.ShopOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShopOrderServiceImpl implements ShopOrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserMapper userMapper;

    public ShopOrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.userMapper = userMapper;
    }

    private static final String[] STATUS_TEXT = {"待付款", "待发货", "待收货", "已完成", "已取消"};

    private String getStatusText(int status) {
        return status >= 0 && status < STATUS_TEXT.length ? STATUS_TEXT[status] : "未知";
    }

    @Override
    public PageResultVO<ShopOrderVO> listOrders(ShopOrderQueryDTO query) {
        Page<Order> pageParam = new Page<>(query.getPage(), query.getSize());
        QueryWrapper<Order> wrapper = new QueryWrapper<>();

        if (query.getMerchantId() != null) {
            wrapper.eq("merchant_id", query.getMerchantId());
        }
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        wrapper.orderByDesc("id");

        Page<Order> orderPage = orderMapper.selectPage(pageParam, wrapper);
        log.debug("订单列表查询完成: total={}, page={}, size={}", orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize());

        // batch fetch user names
        Set<Long> userIds = orderPage.getRecords().stream()
                .map(Order::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(u -> u.getId().longValue(), User::getNickname, (a, b) -> a));
        }

        Map<Long, String> finalUserMap = userMap;
        List<ShopOrderVO> voList = orderPage.getRecords().stream().map(order -> ShopOrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userName(finalUserMap.getOrDefault(order.getUserId(), "未知用户"))
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .statusText(getStatusText(order.getStatus()))
                .createTime(order.getCreateTime())
                .build()
        ).collect(Collectors.toList());

        return PageResultVO.<ShopOrderVO>builder()
                .records(voList)
                .total(orderPage.getTotal())
                .page(orderPage.getCurrent())
                .size(orderPage.getSize())
                .build();
    }

    @Override
    public ShopOrderDetailVO getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在: orderId={}", orderId);
            return null;
        }

        User user = userMapper.selectById(order.getUserId());
        List<OrderItem> items = orderItemMapper.selectList(
                new QueryWrapper<OrderItem>().eq("order_id", orderId));

        List<OrderItemVO> itemVOs = items.stream().map(item -> OrderItemVO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build()
        ).collect(Collectors.toList());

        return ShopOrderDetailVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userName(user != null ? user.getNickname() : "未知用户")
                .totalAmount(order.getTotalAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .statusText(getStatusText(order.getStatus()))
                .createTime(order.getCreateTime())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(
                        order.getReceiverProvince() + order.getReceiverCity()
                                + order.getReceiverDistrict() + order.getReceiverDetailAddress())
                .remark(order.getRemark())
                .items(itemVOs)
                .build();
    }

    @Override
    public void markShipped(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        orderMapper.updateById(order);
        log.info("订单发货成功: orderId={}", orderId);
    }
}
