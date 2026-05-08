package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.AfterSaleDTO;
import com.ecommerce.entity.AfterSale;
import com.ecommerce.vo.AfterSaleVO;

import java.util.List;

public interface AfterSaleService extends IService<AfterSale> {
    void createAfterSale(AfterSaleDTO dto);
    List<AfterSaleVO> getAfterSaleList(Long userId);
    AfterSaleVO getAfterSaleDetail(Long id);
    void cancelAfterSale(Long id, Long userId);
}
