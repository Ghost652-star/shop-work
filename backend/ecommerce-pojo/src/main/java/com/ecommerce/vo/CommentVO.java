package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论视图对象
 * 用于返回评论信息给前端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    /**
     * 评论 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名（关联查询）
     */
    private String username;

    /**
     * 商品 ID
     */
    private Long productId;

    /**
     * 订单 ID
     */
    private Long orderId;

    /**
     * 评分：1-5 星
     */
    private Integer rating;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论图片（多张用逗号分隔）
     */
    private String images;

    /**
     * 评论时间
     */
    private String createTime;
}
