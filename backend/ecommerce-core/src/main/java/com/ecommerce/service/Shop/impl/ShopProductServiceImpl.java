package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.Shop.ShopProductService;
import com.ecommerce.vo.PageResultVO;
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

    public ShopProductServiceImpl(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public PageResultVO<ShopProductVO> listProducts(int page, int size, String name, Integer status, Integer categoryId) {
        Page<Product> pageParam = new Page<>(page, size);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like("name", name);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        wrapper.orderByDesc("id");

        Page<Product> result = productMapper.selectPage(pageParam, wrapper);

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
    }

    @Override
    public void updateStock(Integer productId, Integer stock) {
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        productMapper.updateById(product);
    }
}
