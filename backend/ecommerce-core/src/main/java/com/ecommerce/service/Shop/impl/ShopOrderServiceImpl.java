package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.OrderItemMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.service.Shop.ShopOrderService;
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

    @Override
    public Page<Map<String, Object>> listOrders(int page, int size, Integer status) {
        Page<Order> pageParam = new Page<>(page, size);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();

        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("id");

        Page<Order> orderPage = orderMapper.selectPage(pageParam, wrapper);

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
        List<Map<String, Object>> records = orderPage.getRecords().stream().map(order -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", order.getId());
            item.put("orderNo", order.getOrderNo());
            item.put("userName", finalUserMap.getOrDefault(order.getUserId(), "未知用户"));
            item.put("totalAmount", order.getTotalAmount());
            item.put("payAmount", order.getPayAmount());
            item.put("status", order.getStatus());
            item.put("statusText", STATUS_TEXT[order.getStatus()]);
            item.put("createTime", order.getCreateTime());
            return item;
        }).collect(Collectors.toList());

        Page<Map<String, Object>> result = new Page<>(page, size);
        result.setRecords(records);
        result.setTotal(orderPage.getTotal());
        return result;
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) return null;

        User user = userMapper.selectById(order.getUserId());
        List<OrderItem> items = orderItemMapper.selectList(
                new QueryWrapper<OrderItem>().eq("order_id", orderId));

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", order.getId());
        detail.put("orderNo", order.getOrderNo());
        detail.put("userName", user != null ? user.getNickname() : "未知用户");
        detail.put("totalAmount", order.getTotalAmount());
        detail.put("payAmount", order.getPayAmount());
        detail.put("status", order.getStatus());
        detail.put("statusText", STATUS_TEXT[order.getStatus()]);
        detail.put("createTime", order.getCreateTime());
        detail.put("receiverName", order.getReceiverName());
        detail.put("receiverPhone", order.getReceiverPhone());
        detail.put("receiverAddress",
                order.getReceiverProvince() + order.getReceiverCity()
                        + order.getReceiverDistrict() + order.getReceiverDetailAddress());
        detail.put("remark", order.getRemark());
        detail.put("items", items);
        return detail;
    }

    @Override
    public void markShipped(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        orderMapper.updateById(order);
    }
}
