package com.ecommerce.Controller.Shop;

import com.ecommerce.dto.ShopAfterSaleHandleDTO;
import com.ecommerce.dto.ShopAfterSaleQueryDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopAfterSaleService;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopAfterSaleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/shop/after-sale")
public class ShopAfterSaleController {

    private final ShopAfterSaleService shopAfterSaleService;

    public ShopAfterSaleController(ShopAfterSaleService shopAfterSaleService) {
        this.shopAfterSaleService = shopAfterSaleService;
    }

    @GetMapping("/list")
    public Result<PageResultVO<ShopAfterSaleVO>> list(ShopAfterSaleQueryDTO query) {
        return Result.success(shopAfterSaleService.listAfterSales(query));
    }

    @PutMapping("/handle")
    public Result<Void> handle(@RequestBody ShopAfterSaleHandleDTO dto) {
        shopAfterSaleService.handleAfterSale(dto.getAfterSaleId(), dto.getStatus(), dto.getAdminRemark());
        return Result.success();
    }
}
