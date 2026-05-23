package com.ecommerce.Controller.Shop;

import com.ecommerce.entity.Merchant;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.DashboardService;
import com.ecommerce.service.Shop.MerchantService;
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
@RequestMapping("/shop/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final MerchantService merchantService;

    public DashboardController(DashboardService dashboardService, MerchantService merchantService) {
        this.dashboardService = dashboardService;
        this.merchantService = merchantService;
    }

    @GetMapping("/sales-trend")
    public Result<ShopSalesTrendVO> salesTrend() {
        log.debug("查询销售趋势数据");
        return Result.success(dashboardService.getSalesTrend());
    }

    @GetMapping("/order-status")
    public Result<List<ShopOrderStatusVO>> orderStatus() {
        log.debug("查询订单状态分布");
        return Result.success(dashboardService.getOrderStatus());
    }

    @GetMapping("/top-products")
    public Result<List<ShopTopProductVO>> topProducts() {
        Merchant merchant = merchantService.getMerchantInfo();
        log.debug("查询热销商品排行: merchantId={}", merchant.getId());
        return Result.success(dashboardService.getTopProducts(merchant.getId().longValue()));
    }
}
