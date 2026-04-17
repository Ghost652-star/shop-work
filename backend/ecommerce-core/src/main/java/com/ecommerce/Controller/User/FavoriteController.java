package com.ecommerce.Controller.User;

import com.ecommerce.dto.FavoriteDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.FavoriteService;
import com.ecommerce.vo.FavoriteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 收藏控制器
 */
@RestController
@Slf4j
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 收藏商品
     * @param favoriteDTO 收藏请求 DTO
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> addFavorite( @RequestBody FavoriteDTO favoriteDTO) {
        log.info("收藏商品请求: userId={}, productId={}", favoriteDTO.getUserId(), favoriteDTO.getProductId());
        favoriteService.addFavorite(favoriteDTO);
        log.info("收藏商品成功");
        return Result.success();
    }

    /**
     * 取消收藏
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 操作结果
     */
    @DeleteMapping
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        log.info("取消收藏请求: userId={}, productId={}", userId, productId);
        favoriteService.removeFavorite(userId, productId);
        log.info("取消收藏成功");
        return Result.success();
    }

    /**
     * 查询用户的收藏列表
     * @param userId 用户 ID
     * @return 收藏列表
     */
    @GetMapping("/list")
    public Result<List<FavoriteVO>> getFavoriteList(@RequestParam Long userId) {
        log.debug("查询用户收藏列表请求: userId={}", userId);
        List<FavoriteVO> favoriteList = favoriteService.getFavoriteList(userId);
        log.debug("查询到收藏数量: {}", favoriteList.size());
        return Result.success(favoriteList);
    }

    /**
     * 检查是否已收藏
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 是否已收藏 (data: true/false)
     */
    @GetMapping("/check")
    public Result<Boolean> isFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        log.debug("检查收藏状态请求: userId={}, productId={}", userId, productId);
        boolean favorited = favoriteService.isFavorite(userId, productId);
        return Result.success(favorited);
    }
}
