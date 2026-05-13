package com.ecommerce.service.Shop;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.entity.Merchant;

public interface MerchantService extends IService<Merchant> {
    Merchant getMerchantInfo();
}
