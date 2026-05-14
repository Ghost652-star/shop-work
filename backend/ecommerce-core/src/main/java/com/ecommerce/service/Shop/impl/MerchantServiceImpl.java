package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Merchant;
import com.ecommerce.mapper.Shop.MerchantMapper;
import com.ecommerce.service.Shop.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    @Override
    public Merchant getMerchantInfo() {
        Merchant merchant = query().eq("status", 1).one();
        log.debug("查询商家信息: merchantId={}, name={}", merchant != null ? merchant.getId() : null, merchant != null ? merchant.getName() : null);
        return merchant;
    }
}
