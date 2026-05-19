package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.AfterSaleDTO;
import com.ecommerce.entity.AfterSale;
import com.ecommerce.entity.AfterSaleItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.exception.AfterSaleException;
import com.ecommerce.mapper.AfterSaleItemMapper;
import com.ecommerce.mapper.AfterSaleMapper;
import com.ecommerce.mapper.OrderItemMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.service.User.AfterSaleService;
import com.ecommerce.vo.AfterSaleItemVO;
import com.ecommerce.vo.AfterSaleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AfterSaleServiceImpl extends ServiceImpl<AfterSaleMapper, AfterSale> implements AfterSaleService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AfterSaleItemMapper afterSaleItemMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    AfterSaleServiceImpl(AfterSaleItemMapper afterSaleItemMapper,
                         OrderMapper orderMapper,
                         OrderItemMapper orderItemMapper) {
        this.afterSaleItemMapper = afterSaleItemMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public void createAfterSale(AfterSaleDTO dto) {
        log.info("创建售后申请: userId={}, orderId={}", dto.getUserId(), dto.getOrderId());

        Order order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new AfterSaleException("订单不存在");
        }
        if (!order.getUserId().equals(dto.getUserId())) {
            throw new AfterSaleException("无权操作该订单");
        }
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new AfterSaleException("当前订单状态不支持申请售后");
        }
        if (order.getAfterSaleStatus() != null && order.getAfterSaleStatus() == 1) {
            throw new AfterSaleException("该订单已有售后处理中");
        }

        List<Long> itemIds = dto.getOrderItemIds();
        if (itemIds == null || itemIds.isEmpty()) {
            throw new AfterSaleException("请选择要售后的商品");
        }

        LambdaQueryWrapper<OrderItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(OrderItem::getOrderId, dto.getOrderId())
                 .in(OrderItem::getId, itemIds);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemQuery);
        if (orderItems.size() != itemIds.size()) {
            throw new AfterSaleException("商品信息不正确");
        }

        BigDecimal refundAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AfterSale afterSale = AfterSale.builder()
                .orderId(dto.getOrderId())
                .userId(dto.getUserId())
                .reason(dto.getReason())
                .description(dto.getDescription())
                .images(dto.getImages())
                .refundAmount(refundAmount)
                .status(0)
                .build();
        save(afterSale);

        for (Long orderItemId : itemIds) {
            AfterSaleItem item = AfterSaleItem.builder()
                    .afterSaleId(afterSale.getId())
                    .orderItemId(orderItemId)
                    .build();
            afterSaleItemMapper.insert(item);
        }

        order.setAfterSaleStatus(1);
        orderMapper.updateById(order);

        log.info("售后申请创建成功: afterSaleId={}", afterSale.getId());
    }

    @Override
    public List<AfterSaleVO> getAfterSaleList(Long userId) {
        LambdaQueryWrapper<AfterSale> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AfterSale::getUserId, userId)
                   .orderByDesc(AfterSale::getCreateTime);
        List<AfterSale> afterSales = list(queryWrapper);
        return convertToVOList(afterSales);
    }

    @Override
    public AfterSaleVO getAfterSaleDetail(Long id) {
        AfterSale afterSale = getById(id);
        if (afterSale == null) {
            throw new AfterSaleException("售后单不存在");
        }
        return convertToVO(afterSale);
    }

    @Override
    @Transactional
    public void cancelAfterSale(Long id, Long userId) {
        AfterSale afterSale = getById(id);
        if (afterSale == null) {
            throw new AfterSaleException("售后单不存在");
        }
        if (!afterSale.getUserId().equals(userId)) {
            throw new AfterSaleException("无权操作该售后单");
        }
        if (afterSale.getStatus() != 0) {
            throw new AfterSaleException("仅待处理的售后单可取消");
        }

        removeById(id);

        LambdaQueryWrapper<AfterSaleItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(AfterSaleItem::getAfterSaleId, id);
        afterSaleItemMapper.delete(itemQuery);

        Order order = orderMapper.selectById(afterSale.getOrderId());
        if (order != null) {
            order.setAfterSaleStatus(0);
            orderMapper.updateById(order);
        }

        log.info("售后单已取消: id={}", id);
    }

    private List<AfterSaleVO> convertToVOList(List<AfterSale> afterSales) {
        if (afterSales.isEmpty()) {
            return new ArrayList<>();
        }
        return afterSales.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private AfterSaleVO convertToVO(AfterSale afterSale) {
        Order order = orderMapper.selectById(afterSale.getOrderId());
        String orderNo = order != null ? order.getOrderNo() : "";

        LambdaQueryWrapper<AfterSaleItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(AfterSaleItem::getAfterSaleId, afterSale.getId());
        List<AfterSaleItem> afterSaleItems = afterSaleItemMapper.selectList(itemQuery);

        List<AfterSaleItemVO> itemVOs = new ArrayList<>();
        if (!afterSaleItems.isEmpty()) {
            List<Long> orderItemIds = afterSaleItems.stream()
                    .map(AfterSaleItem::getOrderItemId)
                    .collect(Collectors.toList());
            LambdaQueryWrapper<OrderItem> oiQuery = new LambdaQueryWrapper<>();
            oiQuery.in(OrderItem::getId, orderItemIds);
            List<OrderItem> orderItems = orderItemMapper.selectList(oiQuery);
            Map<Long, OrderItem> oiMap = orderItems.stream()
                    .collect(Collectors.toMap(OrderItem::getId, oi -> oi));

            for (AfterSaleItem asi : afterSaleItems) {
                OrderItem oi = oiMap.get(asi.getOrderItemId());
                if (oi != null) {
                    itemVOs.add(AfterSaleItemVO.builder()
                            .orderItemId(oi.getId())
                            .productId(oi.getProductId())
                            .productName(oi.getProductName())
                            .productImage(oi.getProductImage())
                            .price(oi.getPrice())
                            .quantity(oi.getQuantity())
                            .totalPrice(oi.getTotalPrice())
                            .build());
                }
            }
        }

        return AfterSaleVO.builder()
                .id(afterSale.getId())
                .orderNo(orderNo)
                .orderId(afterSale.getOrderId())
                .reason(afterSale.getReason())
                .description(afterSale.getDescription())
                .images(afterSale.getImages())
                .refundAmount(afterSale.getRefundAmount())
                .status(afterSale.getStatus())
                .statusText(getStatusText(afterSale.getStatus()))
                .adminRemark(afterSale.getAdminRemark())
                .createTime(afterSale.getCreateTime() != null ? afterSale.getCreateTime().format(DATE_TIME_FORMATTER) : null)
                .items(itemVOs)
                .build();
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待处理";
            case 1: return "已通过";
            case 2: return "已驳回";
            case 3: return "已完成";
            default: return "未知";
        }
    }
}
