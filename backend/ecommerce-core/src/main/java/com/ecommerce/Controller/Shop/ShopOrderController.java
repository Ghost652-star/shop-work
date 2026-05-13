package com.ecommerce.Controller.Shop;

import com.ecommerce.dto.ShopOrderShipDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopOrderService;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopOrderDetailVO;
import com.ecommerce.vo.ShopOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shop/order")
public class ShopOrderController {

    private final ShopOrderService shopOrderService;

    public ShopOrderController(ShopOrderService shopOrderService) {
        this.shopOrderService = shopOrderService;
    }

    @GetMapping("/list")
    public Result<PageResultVO<ShopOrderVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(shopOrderService.listOrders(page, size, status));
    }

    @GetMapping("/detail")
    public Result<ShopOrderDetailVO> detail(@RequestParam Long orderId) {
        return Result.success(shopOrderService.getOrderDetail(orderId));
    }

    @PutMapping("/ship")
    public Result<Void> ship(@RequestBody ShopOrderShipDTO dto) {
        shopOrderService.markShipped(dto.getOrderId());
        return Result.success();
    }
}
