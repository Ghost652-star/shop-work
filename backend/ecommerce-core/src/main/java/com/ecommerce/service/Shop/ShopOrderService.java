package com.ecommerce.service.Shop;

import com.ecommerce.dto.ShopOrderQueryDTO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopOrderDetailVO;
import com.ecommerce.vo.ShopOrderVO;

public interface ShopOrderService {
    PageResultVO<ShopOrderVO> listOrders(ShopOrderQueryDTO query);
    ShopOrderDetailVO getOrderDetail(Long orderId);
    void markShipped(Long orderId);
}
