package com.ecommerce.Controller.Shop;

import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/sales-trend")
    public Result<Map<String, Object>> salesTrend() {
        return Result.success(dashboardService.getSalesTrend());
    }

    @GetMapping("/order-status")
    public Result<List<Map<String, Object>>> orderStatus() {
        return Result.success(dashboardService.getOrderStatus());
    }

    @GetMapping("/top-products")
    public Result<List<Map<String, Object>>> topProducts() {
        return Result.success(dashboardService.getTopProducts());
    }
}
