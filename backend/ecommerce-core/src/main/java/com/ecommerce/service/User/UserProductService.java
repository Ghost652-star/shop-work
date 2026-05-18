package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.entity.Product;
import com.ecommerce.vo.ProductVO;

import java.util.List;

/**
 * 用户端商品服务接口（只读）
 */
public interface UserProductService extends IService<Product> {

    /**
     * 查询商品列表
     * @return 商品列表
     */
    List<ProductVO> listProducts();

    /**
     * 根据ID查询商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    ProductVO getProductById(Integer id);

    /**
     * 根据分类ID查询商品列表
     * @param categoryId 分类ID
     * @return 商品列表
     */
    List<ProductVO> listProductsByCategoryId(Integer categoryId);
}
