package com.ecommerce.Controller.Shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopAfterSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/after-sale")
public class ShopAfterSaleController {

    private final ShopAfterSaleService shopAfterSaleService;

    public ShopAfterSaleController(ShopAfterSaleService shopAfterSaleService) {
        this.shopAfterSaleService = shopAfterSaleService;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {

        Page<Map<String, Object>> result = shopAfterSaleService.listAfterSales(page, size, status);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }

    @PutMapping("/handle")
    public Result<Void> handle(@RequestBody Map<String, Object> params) {
        Long afterSaleId = Long.valueOf(params.get("afterSaleId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        String adminRemark = (String) params.get("adminRemark");
        shopAfterSaleService.handleAfterSale(afterSaleId, status, adminRemark);
        return Result.success();
    }
}
