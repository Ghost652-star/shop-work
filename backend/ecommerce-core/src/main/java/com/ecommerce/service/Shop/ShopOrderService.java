package com.ecommerce.service.Shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Map;

public interface ShopOrderService {
    Page<Map<String, Object>> listOrders(int page, int size, Integer status);
    Map<String, Object> getOrderDetail(Long orderId);
    void markShipped(Long orderId);
}
