package com.ecommerce.Controller.Shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/order")
public class ShopOrderController {

    private final ShopOrderService shopOrderService;

    public ShopOrderController(ShopOrderService shopOrderService) {
        this.shopOrderService = shopOrderService;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {

        Page<Map<String, Object>> result = shopOrderService.listOrders(page, size, status);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }

    @GetMapping("/detail")
    public Result<Map<String, Object>> detail(@RequestParam Long orderId) {
        return Result.success(shopOrderService.getOrderDetail(orderId));
    }

    @PutMapping("/ship")
    public Result<Void> ship(@RequestBody Map<String, Long> params) {
        shopOrderService.markShipped(params.get("orderId"));
        return Result.success();
    }
}
