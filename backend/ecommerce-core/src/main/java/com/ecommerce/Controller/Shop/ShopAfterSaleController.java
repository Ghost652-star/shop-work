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
        log.debug("查询售后列表: page={}, size={}, status={}", query.getPage(), query.getSize(), query.getStatus());
        return Result.success(shopAfterSaleService.listAfterSales(query));
    }

    @PutMapping("/handle")
    public Result<Void> handle(@RequestBody ShopAfterSaleHandleDTO dto) {
        log.info("处理售后申请: afterSaleId={}, status={}, remark={}", dto.getAfterSaleId(), dto.getStatus(), dto.getAdminRemark());
        shopAfterSaleService.handleAfterSale(dto.getAfterSaleId(), dto.getStatus(), dto.getAdminRemark());
        return Result.success();
    }
}
