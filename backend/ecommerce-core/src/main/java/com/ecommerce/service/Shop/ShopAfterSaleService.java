package com.ecommerce.service.Shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

public interface ShopAfterSaleService {
    Page<Map<String, Object>> listAfterSales(int page, int size, Integer status);
    void handleAfterSale(Long afterSaleId, Integer status, String adminRemark);
}
