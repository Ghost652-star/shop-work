package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Category;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.common.RedisKeys;
import com.ecommerce.service.User.UserCategoryService;
import com.ecommerce.utils.RedisCacheUtil;
import com.fasterxml.jackson.core.type.TypeReference;
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

    private final RedisCacheUtil redisCacheUtil;

    public UserCategoryServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    public List<CategoryVO> listCategories() {
        log.debug("用户端查询分类列表");
        List<CategoryVO> cached = redisCacheUtil.get(RedisKeys.CATEGORIES_ALL,
                new TypeReference<List<CategoryVO>>() {});
        if (cached != null) {
            log.debug("分类列表缓存命中");
            return cached;
        }

        List<Category> categories = query().list();
        log.debug("查询到分类数量: {}", categories.size());

        List<CategoryVO> result = categories.stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        redisCacheUtil.set(RedisKeys.CATEGORIES_ALL, result, 30);
        log.debug("分类列表已缓存");

        return result;
    }

    /**
     * 清除分类缓存（商家端修改分类时调用）
     */
    public void clearCategoryCache() {
        redisCacheUtil.delete(RedisKeys.CATEGORIES_ALL);
        log.debug("分类缓存已清除");
    }

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
