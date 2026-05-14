package com.ecommerce.Controller.Shop;

import com.ecommerce.dto.ShopProductQueryDTO;
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
@RequestMapping("/shop/product")
public class ShopProductController {

    private final ShopProductService shopProductService;

    public ShopProductController(ShopProductService shopProductService) {
        this.shopProductService = shopProductService;
    }

    @GetMapping("/list")
    public Result<PageResultVO<ShopProductVO>> list(ShopProductQueryDTO query) {
        log.debug("查询商品列表: page={}, size={}, name={}, status={}, categoryId={}",
                query.getPage(), query.getSize(), query.getName(), query.getStatus(), query.getCategoryId());
        return Result.success(shopProductService.listProducts(query));
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody ShopProductStatusDTO dto) {
        log.info("更新商品状态: productId={}, status={}", dto.getProductId(), dto.getStatus());
        shopProductService.updateStatus(dto.getProductId(), dto.getStatus());
        return Result.success();
    }

    @PutMapping("/stock")
    public Result<Void> updateStock(@RequestBody ShopProductStockDTO dto) {
        log.info("更新商品库存: productId={}, stock={}", dto.getProductId(), dto.getStock());
        shopProductService.updateStock(dto.getProductId(), dto.getStock());
        return Result.success();
    }
}
