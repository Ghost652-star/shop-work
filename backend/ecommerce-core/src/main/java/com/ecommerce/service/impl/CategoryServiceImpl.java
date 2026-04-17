package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Category;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.service.CategoryService;
import com.ecommerce.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 查询分类列表
     * @return 分类列表
     */
    @Override
    public List<CategoryVO> listCategories() {
        log.debug("查询所有分类列表");
        List<Category> categories = query().list();
        log.debug("查询到分类数量: {}", categories.size());
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将 Category 实体转换为 CategoryVO
     * @param category 分类实体
     * @return 分类视图对象
     */
    private CategoryVO convertToVO(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}