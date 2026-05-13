package com.ecommerce.service.Shop;

import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopOrderDetailVO;
import com.ecommerce.vo.ShopOrderVO;

public interface ShopOrderService {
    PageResultVO<ShopOrderVO> listOrders(int page, int size, Integer status);
    ShopOrderDetailVO getOrderDetail(Long orderId);
    void markShipped(Long orderId);
}
