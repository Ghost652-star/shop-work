package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.Shop.DashboardService;
import com.ecommerce.vo.ShopOrderStatusVO;
import com.ecommerce.vo.ShopSalesTrendVO;
import com.ecommerce.vo.ShopTopProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public ShopSalesTrendVO getSalesTrend() {
        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
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
            BigDecimal total = orders.stream()
                    .map(Order::getPayAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            amounts.add(total);
        }

        return ShopSalesTrendVO.builder()
                .dates(dates)
                .amounts(amounts)
                .build();
    }

    @Override
    public List<ShopOrderStatusVO> getOrderStatus() {
        String[] statusNames = {"待付款", "待发货", "待收货", "已完成", "已取消"};
        List<ShopOrderStatusVO> result = new ArrayList<>();

        for (int i = 0; i < statusNames.length; i++) {
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("status", i);
            long count = orderMapper.selectCount(wrapper);

            result.add(ShopOrderStatusVO.builder()
                    .name(statusNames[i])
                    .value(count)
                    .build());
        }
        return result;
    }

    @Override
    public List<ShopTopProductVO> getTopProducts() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("sales").last("LIMIT 10");
        List<Product> products = productMapper.selectList(wrapper);

        return products.stream().map(p -> ShopTopProductVO.builder()
                .name(p.getName())
                .sales(p.getSales())
                .build()
        ).collect(Collectors.toList());
    }
}
