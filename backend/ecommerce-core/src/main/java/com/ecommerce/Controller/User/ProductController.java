package com.ecommerce.Controller.User;

import com.ecommerce.common.RedisKeys;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.UserProductService;
import com.ecommerce.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private final UserProductService userProductService;
    private final StringRedisTemplate stringRedisTemplate;

    ProductController(UserProductService userProductService, StringRedisTemplate stringRedisTemplate) {
        this.userProductService = userProductService;
        this.stringRedisTemplate = stringRedisTemplate;
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
        String json = stringRedisTemplate.opsForValue().get(RedisKeys.HOT_SALES_CACHE);
        if (json == null || json.isEmpty()) {
            log.debug("热销榜单缓存为空");
            return Result.success(java.util.Collections.emptyList());
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> list = mapper.readValue(json,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
            return Result.success(list);
        } catch (Exception e) {
            log.error("解析热销榜单缓存失败", e);
            return Result.success(java.util.Collections.emptyList());
        }
    }
}