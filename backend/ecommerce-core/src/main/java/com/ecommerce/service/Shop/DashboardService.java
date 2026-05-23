package com.ecommerce.service.Shop;

import com.ecommerce.vo.ShopOrderStatusVO;
import com.ecommerce.vo.ShopSalesTrendVO;
import com.ecommerce.vo.ShopTopProductVO;

import java.util.List;

public interface DashboardService {
    ShopSalesTrendVO getSalesTrend();
    List<ShopOrderStatusVO> getOrderStatus();
    List<ShopTopProductVO> getTopProducts(Long merchantId);
}
