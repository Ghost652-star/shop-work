package com.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 购物车 Mapper
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT IFNULL(SUM(quantity), 0) FROM cart WHERE user_id = #{userId}")
    Integer getCartCount(@Param("userId") Long userId);
}
