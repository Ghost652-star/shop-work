package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.Shop.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public DashboardServiceImpl(OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Override
    public Map<String, Object> getSalesTrend() {
        List<String> dates = new ArrayList<>();
        List<java.math.BigDecimal> amounts = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(fmt));

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.between("create_time", start, end)
                   .in("status", 1, 2, 3);

            List<Order> orders = orderMapper.selectList(wrapper);
            java.math.BigDecimal total = orders.stream()
                    .map(Order::getPayAmount)
                    .filter(Objects::nonNull)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            amounts.add(total);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("amounts", amounts);
        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderStatus() {
        String[] statusNames = {"待付款", "待发货", "待收货", "已完成", "已取消"};
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < statusNames.length; i++) {
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("status", i);
            long count = orderMapper.selectCount(wrapper);

            Map<String, Object> item = new HashMap<>();
            item.put("name", statusNames[i]);
            item.put("value", count);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTopProducts() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("sales").last("LIMIT 10");
        List<Product> products = productMapper.selectList(wrapper);

        return products.stream().map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", p.getName());
            item.put("sales", p.getSales());
            return item;
        }).collect(Collectors.toList());
    }
}
