package com.ecommerce.Controller.Shop;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Product;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/product")
public class ShopProductController {

    private final ShopProductService shopProductService;

    public ShopProductController(ShopProductService shopProductService) {
        this.shopProductService = shopProductService;
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer categoryId) {

        Page<Product> result = shopProductService.listProducts(page, size, name, status, categoryId);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("page", result.getCurrent());
        data.put("size", result.getSize());
        return Result.success(data);
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Integer> params) {
        shopProductService.updateStatus(params.get("productId"), params.get("status"));
        return Result.success();
    }

    @PutMapping("/stock")
    public Result<Void> updateStock(@RequestBody Map<String, Integer> params) {
        shopProductService.updateStock(params.get("productId"), params.get("stock"));
        return Result.success();
    }
}
