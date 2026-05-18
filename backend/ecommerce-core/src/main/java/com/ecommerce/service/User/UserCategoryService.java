package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.entity.Category;
import com.ecommerce.vo.CategoryVO;

import java.util.List;

/**
 * 用户端分类服务接口（只读）
 */
public interface UserCategoryService extends IService<Category> {

    /**
     * 查询分类列表
     * @return 分类列表
     */
    List<CategoryVO> listCategories();
}
