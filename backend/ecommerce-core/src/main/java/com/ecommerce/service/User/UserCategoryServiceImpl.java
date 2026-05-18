package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Category;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.service.impl.CategoryServiceImpl;
import com.ecommerce.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户端分类服务实现类（只读）
 */
@Slf4j
@Service
public class UserCategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements UserCategoryService {

    private final CategoryServiceImpl categoryService;

    public UserCategoryServiceImpl(CategoryMapper categoryMapper, CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public List<CategoryVO> listCategories() {
        log.debug("用户端查询分类列表");
        return categoryService.listCategories();
    }
}
