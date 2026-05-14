package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.AfterSale;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.AfterSaleMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.dto.ShopAfterSaleQueryDTO;
import com.ecommerce.service.Shop.ShopAfterSaleService;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopAfterSaleVO;
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
    public PageResultVO<ShopAfterSaleVO> listAfterSales(ShopAfterSaleQueryDTO query) {
        Page<AfterSale> pageParam = new Page<>(query.getPage(), query.getSize());
        QueryWrapper<AfterSale> wrapper = new QueryWrapper<>();

        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
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
        List<ShopAfterSaleVO> voList = afterSalePage.getRecords().stream().map(as -> {
            Order order = finalOrderMap.get(as.getOrderId());
            String statusText = as.getStatus() < STATUS_TEXT.length ? STATUS_TEXT[as.getStatus()] : "未知";
            return ShopAfterSaleVO.builder()
                    .id(as.getId())
                    .orderNo(order != null ? order.getOrderNo() : "未知")
                    .userName(finalUserMap.getOrDefault(as.getUserId(), "未知用户"))
                    .reason(as.getReason())
                    .refundAmount(as.getRefundAmount())
                    .status(as.getStatus())
                    .statusText(statusText)
                    .createTime(as.getCreateTime())
                    .build();
        }).collect(Collectors.toList());

        return PageResultVO.<ShopAfterSaleVO>builder()
                .records(voList)
                .total(afterSalePage.getTotal())
                .page(afterSalePage.getCurrent())
                .size(afterSalePage.getSize())
                .build();
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
