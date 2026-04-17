package com.ecommerce.Controller.User;

import com.ecommerce.result.Result;
import com.ecommerce.service.ProductService;
import com.ecommerce.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {
    
   private final ProductService productService;
   ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/list")
    public Result<List<ProductVO>> list() {
        log.debug("查询商品列表请求");
        List<ProductVO> products = productService.listProducts();
        log.debug("查询到商品数量: {}", products.size());
        return Result.success(products);
    }
    
    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Integer id) {
        log.debug("查询商品详情请求: productId={}", id);
        ProductVO product = productService.getProductById(id);
        return Result.success(product);
    }
    
    @GetMapping("/category/{categoryId}")
    public Result<List<ProductVO>> listByCategory(@PathVariable Integer categoryId) {
        log.debug("根据分类查询商品请求: categoryId={}", categoryId);
        List<ProductVO> products = productService.listProductsByCategoryId(categoryId);
        log.debug("查询到商品数量: {}", products.size());
        return Result.success(products);
    }
}