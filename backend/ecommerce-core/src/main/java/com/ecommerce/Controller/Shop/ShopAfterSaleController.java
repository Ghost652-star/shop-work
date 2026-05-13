package com.ecommerce.Controller.Shop;

import com.ecommerce.dto.ShopAfterSaleHandleDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopAfterSaleService;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopAfterSaleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/after-sale")
public class ShopAfterSaleController {

    private final ShopAfterSaleService shopAfterSaleService;

    public ShopAfterSaleController(ShopAfterSaleService shopAfterSaleService) {
        this.shopAfterSaleService = shopAfterSaleService;
    }

    @GetMapping("/list")
    public Result<PageResultVO<ShopAfterSaleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(shopAfterSaleService.listAfterSales(page, size, status));
    }

    @PutMapping("/handle")
    public Result<Void> handle(@RequestBody ShopAfterSaleHandleDTO dto) {
        shopAfterSaleService.handleAfterSale(dto.getAfterSaleId(), dto.getStatus(), dto.getAdminRemark());
        return Result.success();
    }
}
