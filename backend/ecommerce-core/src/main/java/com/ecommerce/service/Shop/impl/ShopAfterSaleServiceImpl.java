package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.AfterSale;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.AfterSaleMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.service.Shop.ShopAfterSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShopAfterSaleServiceImpl implements ShopAfterSaleService {

    private final AfterSaleMapper afterSaleMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    public ShopAfterSaleServiceImpl(AfterSaleMapper afterSaleMapper, OrderMapper orderMapper, UserMapper userMapper) {
        this.afterSaleMapper = afterSaleMapper;
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    private static final String[] STATUS_TEXT = {"待处理", "已通过", "已驳回"};

    @Override
    public Page<Map<String, Object>> listAfterSales(int page, int size, Integer status) {
        Page<AfterSale> pageParam = new Page<>(page, size);
        QueryWrapper<AfterSale> wrapper = new QueryWrapper<>();

        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("id");

        Page<AfterSale> afterSalePage = afterSaleMapper.selectPage(pageParam, wrapper);

        // batch fetch related data
        Set<Long> orderIds = afterSalePage.getRecords().stream()
                .map(AfterSale::getOrderId).collect(Collectors.toSet());
        Set<Long> userIds = afterSalePage.getRecords().stream()
                .map(AfterSale::getUserId).collect(Collectors.toSet());

        Map<Long, Order> orderMap = new HashMap<>();
        Map<Long, String> userMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            orderMap = orderMapper.selectBatchIds(orderIds).stream()
                    .collect(Collectors.toMap(Order::getId, o -> o, (a, b) -> a));
        }
        if (!userIds.isEmpty()) {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(u -> u.getId().longValue(), User::getNickname, (a, b) -> a));
        }

        Map<Long, Order> finalOrderMap = orderMap;
        Map<Long, String> finalUserMap = userMap;
        List<Map<String, Object>> records = afterSalePage.getRecords().stream().map(as -> {
            Order order = finalOrderMap.get(as.getOrderId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", as.getId());
            item.put("orderNo", order != null ? order.getOrderNo() : "未知");
            item.put("userName", finalUserMap.getOrDefault(as.getUserId(), "未知用户"));
            item.put("reason", as.getReason());
            item.put("refundAmount", as.getRefundAmount());
            item.put("status", as.getStatus());
            item.put("statusText", as.getStatus() < STATUS_TEXT.length ? STATUS_TEXT[as.getStatus()] : "未知");
            item.put("createTime", as.getCreateTime());
            return item;
        }).collect(Collectors.toList());

        Page<Map<String, Object>> result = new Page<>(page, size);
        result.setRecords(records);
        result.setTotal(afterSalePage.getTotal());
        return result;
    }

    @Override
    public void handleAfterSale(Long afterSaleId, Integer status, String adminRemark) {
        AfterSale afterSale = new AfterSale();
        afterSale.setId(afterSaleId);
        afterSale.setStatus(status);
        afterSale.setAdminRemark(adminRemark);
        afterSaleMapper.updateById(afterSale);
    }
}
