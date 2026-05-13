package com.ecommerce.Controller.Shop;

import com.ecommerce.entity.Merchant;
import com.ecommerce.result.Result;
import com.ecommerce.service.Shop.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/info")
    public Result<Merchant> info() {
        return Result.success(merchantService.getMerchantInfo());
    }
}
