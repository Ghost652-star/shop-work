package com.ecommerce.service.Shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Product;

public interface ShopProductService {
    Page<Product> listProducts(int page, int size, String name, Integer status, Integer categoryId);
    void updateStatus(Integer productId, Integer status);
    void updateStock(Integer productId, Integer stock);
}
