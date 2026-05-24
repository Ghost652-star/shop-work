package com.ecommerce.service.User.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.MerchantProductQueryDTO;
import com.ecommerce.entity.Merchant;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.User.UserMerchantMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.User.UserMerchantService;
import com.ecommerce.vo.MerchantVO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMerchantServiceImpl extends ServiceImpl<UserMerchantMapper, Merchant>
        implements UserMerchantService {

    private final ProductMapper productMapper;

    @Override
    public MerchantVO getMerchantById(Long merchantId) {
        Merchant merchant = getById(merchantId);
        if (merchant == null) {
            return null;
        }
        return MerchantVO.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .phone(merchant.getPhone())
                .description(merchant.getDescription())
                .logo(merchant.getLogo())
                .score(merchant.getScore())
                .build();
    }

    @Override
    public PageResultVO<ProductVO> listMerchantProducts(Long merchantId, MerchantProductQueryDTO queryDTO) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getMerchantId, merchantId)
                .eq(Product::getStatus, 1);

        // 排序
        if ("sales".equals(queryDTO.getSort())) {
            wrapper.orderByDesc(Product::getSales);
        } else {
            // 综合排序：按更新时间倒序
            wrapper.orderByDesc(Product::getUpdateTime);
        }

        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Product> result = productMapper.selectPage(page, wrapper);

        List<ProductVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResultVO.<ProductVO>builder()
                .records(records)
                .total(result.getTotal())
                .page(result.getCurrent())
                .size(result.getSize())
                .build();
    }

    private ProductVO convertToVO(Product product) {
        return ProductVO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sales(product.getSales())
                .mainImage(product.getMainImage())
                .categoryId(product.getCategoryId())
                .stock(product.getStock())
                .merchantId(product.getMerchantId())
                .build();
    }
}
