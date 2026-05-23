package com.ecommerce.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.entity.Order;
import com.ecommerce.vo.SalesTrendRow;
import com.ecommerce.vo.ShopOrderStatusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT DATE(create_time) AS date, SUM(pay_amount) AS total " +
            "FROM orders WHERE create_time BETWEEN #{start} AND #{end} " +
            "AND status IN (1,2,3) GROUP BY DATE(create_time)")
    List<SalesTrendRow> getSalesTrend(@Param("start") Date start, @Param("end") Date end);

    @Select("SELECT status, COUNT(*) AS count FROM orders GROUP BY status")
    List<ShopOrderStatusVO> getOrderStatusCounts();
}
