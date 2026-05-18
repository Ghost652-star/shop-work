package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.common.RedisKeys;
import com.ecommerce.entity.Category;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.service.CategoryService;
import com.ecommerce.utils.RedisCacheUtil;
import com.ecommerce.vo.CategoryVO;
import com.fasterxml.jackson.core.type.TypeReference;
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

    private final RedisCacheUtil redisCacheUtil;

    public CategoryServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    /**
     * 查询分类列表（带缓存）
     * @return 分类列表
     */
    @Override
    public List<CategoryVO> listCategories() {
        // 1. 先从 Redis 获取
        List<CategoryVO> cached = redisCacheUtil.get(RedisKeys.CATEGORIES_ALL,
                new TypeReference<List<CategoryVO>>() {});
        if (cached != null) {
            log.debug("分类列表缓存命中");
            return cached;
        }

        // 2. 缓存没有，查数据库
        log.debug("查询所有分类列表");
        List<Category> categories = query().list();
        log.debug("查询到分类数量: {}", categories.size());

        List<CategoryVO> result = categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 3. 存入 Redis，缓存 30 分钟
        redisCacheUtil.set(RedisKeys.CATEGORIES_ALL, result, 30);
        log.debug("分类列表已缓存");

        return result;
    }

    /**
     * 清除分类缓存（分类变更时调用）
     */
    public void clearCategoryCache() {
        redisCacheUtil.delete(RedisKeys.CATEGORIES_ALL);
        log.debug("分类缓存已清除");
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