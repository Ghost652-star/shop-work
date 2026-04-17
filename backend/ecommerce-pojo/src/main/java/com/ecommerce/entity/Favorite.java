package com.ecommerce.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 * 对应数据库表: favorite
 */
@Data
@Builder

@TableName("favorite")
public class Favorite {
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
     * 收藏时间
     */
    private LocalDateTime createTime;
}
