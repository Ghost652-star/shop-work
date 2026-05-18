package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.impl.ProductServiceImpl;
import com.ecommerce.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户端商品服务实现类（只读）
 */
@Slf4j
@Service
public class UserProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements UserProductService {

    private final ProductServiceImpl productService;

    public UserProductServiceImpl(ProductMapper productMapper, ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Override
    public List<ProductVO> listProducts() {
        log.debug("用户端查询商品列表");
        return productService.listProducts();
    }

    @Override
    public ProductVO getProductById(Integer id) {
        log.debug("用户端查询商品详情: productId={}", id);
        return productService.getProductById(id);
    }

    @Override
    public List<ProductVO> listProductsByCategoryId(Integer categoryId) {
        log.debug("用户端根据分类查询商品: categoryId={}", categoryId);
        return productService.listProductsByCategoryId(categoryId);
    }
}
