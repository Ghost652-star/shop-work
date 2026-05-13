package com.ecommerce.service.Shop;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getSalesTrend();
    List<Map<String, Object>> getOrderStatus();
    List<Map<String, Object>> getTopProducts();
}
