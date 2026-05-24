package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.MerchantProductQueryDTO;
import com.ecommerce.entity.Merchant;
import com.ecommerce.vo.MerchantVO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ProductVO;

/**
 * 用户端商家服务接口（只读）
 */
public interface UserMerchantService extends IService<Merchant> {

    /**
     * 查询商家详情
     * @param merchantId 商家ID
     * @return 商家详情
     */
    MerchantVO getMerchantById(Long merchantId);

    /**
     * 查询商家商品列表
     * @param merchantId 商家ID
     * @param queryDTO 查询参数
     * @return 商品分页列表
     */
    PageResultVO<ProductVO> listMerchantProducts(Long merchantId, MerchantProductQueryDTO queryDTO);
}
