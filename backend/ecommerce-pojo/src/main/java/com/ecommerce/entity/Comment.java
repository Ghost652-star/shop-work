package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * 对应数据库表: comment
 */
@Data
@Builder
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
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
    private LocalDateTime createTime;
}
