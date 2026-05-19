package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.FavoriteDTO;
import com.ecommerce.entity.Favorite;
import com.ecommerce.common.exception.FavoriteException;
import com.ecommerce.mapper.FavoriteMapper;
import com.ecommerce.service.User.FavoriteService;
import com.ecommerce.vo.FavoriteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 收藏商品
     * @param favoriteDTO 收藏请求 DTO
     */
    @Override
    public void addFavorite(FavoriteDTO favoriteDTO) {
        log.info("收藏商品：userId={}, productId={}", favoriteDTO.getUserId(), favoriteDTO.getProductId());

        // 检查是否已收藏（数据库有唯一约束，但这里先查一遍给友好提示）
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, favoriteDTO.getUserId())
                    .eq(Favorite::getProductId, favoriteDTO.getProductId());
        Favorite exist = getOne(queryWrapper);
        if (exist != null) {
            log.warn("商品已收藏：userId={}, productId={}", favoriteDTO.getUserId(), favoriteDTO.getProductId());
            throw new FavoriteException("商品已收藏");
        }

        // 构建收藏实体
        Favorite favorite = Favorite.builder()
                .userId(favoriteDTO.getUserId())
                .productId(favoriteDTO.getProductId())
                .build();

        save(favorite);
        log.info("收藏商品成功：userId={}, productId={}", favoriteDTO.getUserId(), favoriteDTO.getProductId());
    }

    /**
     * 取消收藏
     * @param userId 用户 ID
     * @param productId 商品 ID
     */
    @Override
    public void removeFavorite(Long userId, Long productId) {
        log.info("取消收藏：userId={}, productId={}", userId, productId);

        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, productId);
        boolean removed = remove(queryWrapper);
        if (!removed) {
            log.warn("取消收藏失败，记录不存在：userId={}, productId={}", userId, productId);
            throw new FavoriteException("收藏记录不存在");
        }
        log.info("取消收藏成功：userId={}, productId={}", userId, productId);
    }

    /**
     * 查询用户的收藏列表
     * @param userId 用户 ID
     * @return 收藏列表
     */
    @Override
    public List<FavoriteVO> getFavoriteList(Long userId) {
        log.debug("查询用户收藏列表：userId={}", userId);
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                    .orderByDesc(Favorite::getCreateTime);

        List<Favorite> favoriteList = list(queryWrapper);
        log.debug("用户{}的收藏数量：{}", userId, favoriteList.size());

        return favoriteList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 检查是否已收藏
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 是否已收藏
     */
    @Override
    public boolean isFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                    .eq(Favorite::getProductId, productId);
        return count(queryWrapper) > 0;
    }

    /**
     * 转换为 VO
     * @param favorite 收藏实体
     * @return 收藏 VO
     */
    private FavoriteVO convertToVO(Favorite favorite) {
        if (favorite == null) {
            return null;
        }
        return FavoriteVO.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .productId(favorite.getProductId())
                .createTime(favorite.getCreateTime() != null ? favorite.getCreateTime().format(DATE_TIME_FORMATTER) : null)
                .build();
    }
}
