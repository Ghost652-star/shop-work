package com.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE product SET stock = stock - #{quantity}, sales = sales + #{quantity} " +
            "WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") int quantity);

    @Select("SELECT id, sales FROM product WHERE sales > 0")
    List<Product> selectIdAndSales();
}