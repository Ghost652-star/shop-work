package com.ecommerce.service.Shop.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.entity.Merchant;
import com.ecommerce.mapper.Shop.MerchantMapper;
import com.ecommerce.service.Shop.MerchantService;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    @Override
    public Merchant getMerchantInfo() {
        return query().eq("status", 1).one();
    }
}
