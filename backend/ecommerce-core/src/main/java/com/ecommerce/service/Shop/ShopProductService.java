package com.ecommerce.service.Shop;

import com.ecommerce.dto.ShopProductQueryDTO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopProductVO;

public interface ShopProductService {
    PageResultVO<ShopProductVO> listProducts(ShopProductQueryDTO query);
    void updateStatus(Integer productId, Integer status);
    void updateStock(Integer productId, Integer stock);
}
