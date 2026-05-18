package com.ecommerce.service.User;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.CartDTO;
import com.ecommerce.entity.Cart;
import com.ecommerce.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {
    
    /**
     * 添加商品到购物车
     * @param cartDTO 购物车 DTO
     * @return 购物车 VO
     */
    CartVO addToCart(CartDTO cartDTO);
    
    /**
     * 查询购物车列表
     * @param userId 用户 ID
     * @return 购物车 VO 列表
     */
    List<CartVO> getCartList(Long userId);
    
    /**
     * 更新购物车商品数量
     * @param cartDTO 购物车 DTO
     * @return 购物车 VO
     */
    CartVO updateQuantity(CartDTO cartDTO);
    
    /**
     * 删除购物车商品
     * @param id 购物车记录 ID
     */
    void deleteCart(Long id);
    
    /**
     * 批量删除购物车商品
     * @param ids 购物车记录 ID 列表
     */
    void batchDelete(List<Long> ids);
    
    /**
     * 全选/取消全选
     * @param userId 用户 ID
     * @param isChecked 是否选中：0-未选中，1-选中
     */
    void checkAll(Long userId, Integer isChecked);
    
    /**
     * 获取购物车商品总数
     * @param userId 用户 ID
     * @return 商品总数
     */
    Integer getCartCount(Long userId);

    /**
     * 更新购物车商品选中状态
     * @param cartDTO 购物车 DTO
     * @return 购物车 VO
     */
    CartVO updateChecked(CartDTO cartDTO);
}
