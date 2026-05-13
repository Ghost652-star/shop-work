package com.ecommerce.service.Shop;

import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopAfterSaleVO;

public interface ShopAfterSaleService {
    PageResultVO<ShopAfterSaleVO> listAfterSales(int page, int size, Integer status);
    void handleAfterSale(Long afterSaleId, Integer status, String adminRemark);
}
