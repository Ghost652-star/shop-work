package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.entity.Category;
import com.ecommerce.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {
    /**
     * 查询分类列表
     * @return 分类列表
     */
    List<CategoryVO> listCategories();
}