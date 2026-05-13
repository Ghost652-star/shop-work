package com.ecommerce.service.Shop;

import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopProductVO;

public interface ShopProductService {
    PageResultVO<ShopProductVO> listProducts(int page, int size, String name, Integer status, Integer categoryId);
    void updateStatus(Integer productId, Integer status);
    void updateStock(Integer productId, Integer stock);
}
