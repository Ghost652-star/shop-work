package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏视图对象
 * 用于返回收藏信息给前端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteVO {
    /**
     * 收藏 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品 ID
     */
    private Long productId;

    /**
     * 收藏时间
     */
    private String createTime;
}
