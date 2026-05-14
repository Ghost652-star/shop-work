package com.ecommerce.service.Shop;

import com.ecommerce.dto.ShopProductQueryDTO;
import com.ecommerce.dto.ShopProductSaveDTO;
import com.ecommerce.vo.PageResultVO;
import com.ecommerce.vo.ShopProductDetailVO;
import com.ecommerce.vo.ShopProductVO;

public interface ShopProductService {
    PageResultVO<ShopProductVO> listProducts(ShopProductQueryDTO query);
    ShopProductDetailVO getProductDetail(Integer productId);
    void addProduct(ShopProductSaveDTO dto);
    void updateProduct(ShopProductSaveDTO dto);
    void deleteProduct(Integer productId);
    void updateStatus(Integer productId, Integer status);
    void updateStock(Integer productId, Integer stock);
}
