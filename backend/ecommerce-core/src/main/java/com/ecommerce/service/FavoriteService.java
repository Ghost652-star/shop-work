package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.FavoriteDTO;
import com.ecommerce.entity.Favorite;
import com.ecommerce.vo.FavoriteVO;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService extends IService<Favorite> {
    /**
     * 收藏商品
     * @param favoriteDTO 收藏请求 DTO
     */
    void addFavorite(FavoriteDTO favoriteDTO);

    /**
     * 取消收藏
     * @param userId 用户 ID
     * @param productId 商品 ID
     */
    void removeFavorite(Long userId, Long productId);

    /**
     * 查询用户的收藏列表
     * @param userId 用户 ID
     * @return 收藏列表
     */
    List<FavoriteVO> getFavoriteList(Long userId);

    /**
     * 检查是否已收藏
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long productId);
}
