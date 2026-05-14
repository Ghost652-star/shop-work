package com.ecommerce.Controller.Shop;

import com.ecommerce.dto.ShopProductQueryDTO;
import com.ecommerce.dto.ShopProductSaveDTO;
import com.ecommerce.dto.ShopProductStatusDTO;
import com.ecommerce.dto.ShopProductStockDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.ShopProductService;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopProductDetailVO;
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

    @GetMapping("/detail")
    public Result<ShopProductDetailVO> detail(@RequestParam Integer productId) {
        log.debug("查询商品详情: productId={}", productId);
        return Result.success(shopProductService.getProductDetail(productId));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ShopProductSaveDTO dto) {
        log.info("新增商品: name={}", dto.getName());
        shopProductService.addProduct(dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody ShopProductSaveDTO dto) {
        log.info("修改商品: productId={}, name={}", dto.getId(), dto.getName());
        shopProductService.updateProduct(dto);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam Integer productId) {
        log.info("删除商品: productId={}", productId);
        shopProductService.deleteProduct(productId);
        return Result.success();
    }
}
