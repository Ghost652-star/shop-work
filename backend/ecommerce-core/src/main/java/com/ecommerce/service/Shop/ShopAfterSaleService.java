package com.ecommerce.service.Shop;

import com.ecommerce.dto.ShopAfterSaleQueryDTO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopAfterSaleVO;

public interface ShopAfterSaleService {
    PageResultVO<ShopAfterSaleVO> listAfterSales(ShopAfterSaleQueryDTO query);
    void handleAfterSale(Long afterSaleId, Integer status, String adminRemark);
}
