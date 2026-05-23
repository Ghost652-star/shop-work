package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论请求 DTO
 * 用于接收前端提交的评论数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品 ID
     */
    private Long productId;

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
}
