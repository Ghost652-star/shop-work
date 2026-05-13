package com.ecommerce.Controller.Shop;

import com.ecommerce.dto.ShopProductStatusDTO;
import com.ecommerce.dto.ShopProductStockDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopProductService;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/product")
public class ShopProductController {

    private final ShopProductService shopProductService;

    public ShopProductController(ShopProductService shopProductService) {
        this.shopProductService = shopProductService;
    }

    @GetMapping("/list")
    public Result<PageResultVO<ShopProductVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer categoryId) {
        return Result.success(shopProductService.listProducts(page, size, name, status, categoryId));
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody ShopProductStatusDTO dto) {
        shopProductService.updateStatus(dto.getProductId(), dto.getStatus());
        return Result.success();
    }

    @PutMapping("/stock")
    public Result<Void> updateStock(@RequestBody ShopProductStockDTO dto) {
        shopProductService.updateStock(dto.getProductId(), dto.getStock());
        return Result.success();
    }
}
