package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.dto.ShopProductQueryDTO;
import com.ecommerce.dto.ShopProductSaveDTO;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.Shop.ShopProductService;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopProductDetailVO;
import com.ecommerce.vo.ShopProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShopProductServiceImpl implements ShopProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final com.ecommerce.service.impl.ProductServiceImpl productService;

    public ShopProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper,
                                  com.ecommerce.service.impl.ProductServiceImpl productService) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.productService = productService;
    }

    @Override
    public PageResultVO<ShopProductVO> listProducts(ShopProductQueryDTO query) {
        Page<Product> pageParam = new Page<>(query.getPage(), query.getSize());
        QueryWrapper<Product> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(query.getName())) {
            wrapper.like("name", query.getName());
        }
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq("category_id", query.getCategoryId());
        }
        wrapper.orderByDesc("id");

        Page<Product> result = productMapper.selectPage(pageParam, wrapper);
        log.debug("商品列表查询完成: total={}, page={}, size={}", result.getTotal(), result.getCurrent(), result.getSize());

        // batch fetch category names
        Set<Integer> categoryIds = result.getRecords().stream()
                .map(Product::getCategoryId)
                .collect(Collectors.toSet());
        Map<Integer, String> categoryMap = new java.util.HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            categoryMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        }

        Map<Integer, String> finalCategoryMap = categoryMap;
        List<ShopProductVO> voList = result.getRecords().stream().map(p -> ShopProductVO.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .stock(p.getStock())
                .sales(p.getSales())
                .categoryName(finalCategoryMap.getOrDefault(p.getCategoryId(), ""))
                .mainImage(p.getMainImage())
                .status(p.getStatus())
                .build()
        ).collect(Collectors.toList());

        return PageResultVO.<ShopProductVO>builder()
                .records(voList)
                .total(result.getTotal())
                .page(result.getCurrent())
                .size(result.getSize())
                .build();
    }

    @Override
    public void updateStatus(Integer productId, Integer status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        productMapper.updateById(product);
        log.info("商品状态更新成功: productId={}, newStatus={}", productId, status);
    }

    @Override
    public void updateStock(Integer productId, Integer stock) {
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        productMapper.updateById(product);
        log.info("商品库存更新成功: productId={}, newStock={}", productId, stock);
    }

    @Override
    public ShopProductDetailVO getProductDetail(Integer productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) return null;

        Category category = categoryMapper.selectById(product.getCategoryId());
        log.debug("商品详情查询完成: productId={}, name={}", productId, product.getName());

        return ShopProductDetailVO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .sales(product.getSales())
                .categoryId(product.getCategoryId())
                .categoryName(category != null ? category.getName() : "")
                .mainImage(product.getMainImage())
                .status(product.getStatus())
                .build();
    }

    @Override
    public void addProduct(ShopProductSaveDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategoryId(dto.getCategoryId());
        product.setMainImage(dto.getMainImage());
        product.setStatus(0);
        product.setSales(0);
        productMapper.insert(product);
        log.info("新增商品成功: productId={}, name={}", product.getId(), dto.getName());
    }

    @Override
    public void updateProduct(ShopProductSaveDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategoryId(dto.getCategoryId());
        product.setMainImage(dto.getMainImage());
        productMapper.updateById(product);
        // 清除该商品的缓存
        productService.clearProductCache(dto.getId());
        log.info("修改商品信息成功: productId={}, name={}", dto.getId(), dto.getName());
    }

    @Override
    public void deleteProduct(Integer productId) {
        productMapper.deleteById(productId);
        // 清除该商品的缓存
        productService.clearProductCache(productId);
        log.info("删除商品成功: productId={}", productId);
    }
}
