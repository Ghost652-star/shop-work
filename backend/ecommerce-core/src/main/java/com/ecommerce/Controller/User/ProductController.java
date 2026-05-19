package com.ecommerce.Controller.User;

import com.ecommerce.common.RedisKeys;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.UserProductService;
import com.ecommerce.utils.RedisCacheUtil;
import com.ecommerce.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private final UserProductService userProductService;
    private final RedisCacheUtil redisCacheUtil;

    ProductController(UserProductService userProductService, RedisCacheUtil redisCacheUtil) {
        this.userProductService = userProductService;
        this.redisCacheUtil = redisCacheUtil;
    }

    @GetMapping("/list")
    public Result<List<ProductVO>> list() {
        log.debug("查询商品列表请求");
        List<ProductVO> products = userProductService.listProducts();
        log.debug("查询到商品数量: {}", products.size());
        return Result.success(products);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Integer id) {
        log.debug("查询商品详情请求: productId={}", id);
        ProductVO product = userProductService.getProductById(id);
        return Result.success(product);
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<ProductVO>> listByCategory(@PathVariable Integer categoryId) {
        log.debug("根据分类查询商品请求: categoryId={}", categoryId);
        List<ProductVO> products = userProductService.listProductsByCategoryId(categoryId);
        log.debug("查询到商品数量: {}", products.size());
        return Result.success(products);
    }

    @GetMapping("/hot-sales")
    public Result<List<Map<String, Object>>> hotSales() {
        log.debug("查询热销榜单");
        List<Map<String, Object>> list = redisCacheUtil.valueOps.get(
                RedisKeys.HOT_SALES_CACHE,
                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {}
        );
        if (list == null) {
            log.debug("热销榜单缓存为空");
            return Result.success(Collections.emptyList());
        }
        return Result.success(list);
    }
}
