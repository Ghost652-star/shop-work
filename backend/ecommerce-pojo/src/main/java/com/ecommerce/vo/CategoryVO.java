package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类视图对象
 * 用于返回给前端的分类数据，只包含前端需要的字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {
    /**
     * 分类ID
     */
    private Integer id;
    
    /**
     * 分类名称
     */
    private String name;
}