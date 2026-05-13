package com.ecommerce.Controller.Shop;

import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.DashboardService;
import com.ecommerce.vo.ShopOrderStatusVO;
import com.ecommerce.vo.ShopSalesTrendVO;
import com.ecommerce.vo.ShopTopProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/sales-trend")
    public Result<ShopSalesTrendVO> salesTrend() {
        return Result.success(dashboardService.getSalesTrend());
    }

    @GetMapping("/order-status")
    public Result<List<ShopOrderStatusVO>> orderStatus() {
        return Result.success(dashboardService.getOrderStatus());
    }

    @GetMapping("/top-products")
    public Result<List<ShopTopProductVO>> topProducts() {
        return Result.success(dashboardService.getTopProducts());
    }
}
