package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 收藏请求 DTO
 * 用于接收前端提交的收藏数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    /**
     * 用户 ID
     */
    
    private Long userId;

    /**
     * 商品 ID
     */
  
    private Long productId;
}
