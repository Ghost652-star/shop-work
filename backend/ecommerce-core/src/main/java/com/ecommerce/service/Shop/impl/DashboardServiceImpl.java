package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.Shop.DashboardService;
import com.ecommerce.vo.SalesTrendRow;
import com.ecommerce.vo.ShopOrderStatusVO;
import com.ecommerce.vo.ShopSalesTrendVO;
import com.ecommerce.vo.ShopTopProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        // 构建完整的 7 天日期列表（含无销售的日期）
        List<String> dates = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            dates.add(today.minusDays(i).format(fmt));
        }

        // 一条 SQL 查出有销售记录的日期
        java.util.Date start = java.sql.Timestamp.valueOf(today.minusDays(6).atStartOfDay());
        java.util.Date end = java.sql.Timestamp.valueOf(today.atTime(LocalTime.MAX));
        List<SalesTrendRow> rows = orderMapper.getSalesTrend(start, end);

        // 按日期聚合为 Map
        Map<String, BigDecimal> salesMap = rows.stream()
                .collect(Collectors.toMap(SalesTrendRow::getDate, SalesTrendRow::getTotal, BigDecimal::add));

        // 按日期列表顺序填充金额，无记录的日期填 0
        List<BigDecimal> amounts = new ArrayList<>();
        for (String date : dates) {
            amounts.add(salesMap.getOrDefault(date, BigDecimal.ZERO));
        }

        ShopSalesTrendVO result = ShopSalesTrendVO.builder()
                .dates(dates)
                .amounts(amounts)
                .build();
        log.debug("销售趋势查询完成: days={}, totalAmount={}", dates.size(),
                amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        return result;
    }

    @Override
    public List<ShopOrderStatusVO> getOrderStatus() {
        String[] statusNames = {"待付款", "待发货", "待收货", "已完成", "已取消"};

        // 一条 SQL 查出所有状态的计数
        List<ShopOrderStatusVO> rows = orderMapper.getOrderStatusCounts();

        // 按 status 建立索引映射
        Map<Integer, Long> countMap = rows.stream()
                .collect(Collectors.toMap(ShopOrderStatusVO::getStatus, ShopOrderStatusVO::getValue));

        // 按固定顺序组装，确保每个状态都有值（无订单的状态填 0）
        List<ShopOrderStatusVO> result = new ArrayList<>();
        for (int i = 0; i < statusNames.length; i++) {
            result.add(ShopOrderStatusVO.builder()
                    .status(i)
                    .name(statusNames[i])
                    .value(countMap.getOrDefault(i, 0L))
                    .build());
        }
        log.debug("订单状态分布查询完成: {}", result);
        return result;
    }

    @Override
    public List<ShopTopProductVO> getTopProducts(Long merchantId) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.select("name", "sales")
               .eq("merchant_id", merchantId)
               .orderByDesc("sales")
               .last("LIMIT 10");
        List<Product> products = productMapper.selectList(wrapper);

        List<ShopTopProductVO> topProducts = products.stream().map(p -> ShopTopProductVO.builder()
                .name(p.getName())
                .sales(p.getSales())
                .build()
        ).collect(Collectors.toList());
        log.debug("热销商品排行查询完成: count={}", topProducts.size());
        return topProducts;
    }
}
