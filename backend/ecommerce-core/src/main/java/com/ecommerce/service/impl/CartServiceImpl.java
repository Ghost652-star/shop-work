package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.CartDTO;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.BaseException;
import com.ecommerce.mapper.CartMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.CartService;
import com.ecommerce.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public CartVO addToCart(CartDTO cartDTO) {
        log.info("添加商品到购物车：userId={}, productId={}", cartDTO.getUserId(), cartDTO.getProductId());
        
        // 1. 检查商品是否存在
        Product product = productMapper.selectById(cartDTO.getProductId());
        if (product == null) {
            throw new BaseException("商品不存在");
        }
        
        // 2. 检查商品状态
        if (product.getStatus() != 1) {
            throw new BaseException("商品已下架");
        }
        
        // 3. 检查购物车中是否已有该商品
        Cart existingCart = query()
                .eq("user_id", cartDTO.getUserId())
                .eq("product_id", cartDTO.getProductId())
                .one();
        
        if (existingCart != null) {
            // 已有该商品，增加数量
            existingCart.setQuantity(existingCart.getQuantity() + cartDTO.getQuantity());
            existingCart.setUpdateTime(LocalDateTime.now());
            updateById(existingCart);
            log.info("更新购物车商品数量：cartId={}, quantity={}", existingCart.getId(), existingCart.getQuantity());
            return convertToVO(existingCart);
        } else {
            // 创建新的购物车记录
            Cart cart = Cart.builder()
                    .userId(cartDTO.getUserId())
                    .productId(cartDTO.getProductId())
                    .productName(product.getName())
                    .productImage(product.getMainImage())
                    .price(product.getPrice())
                    .quantity(cartDTO.getQuantity())
                    .isChecked(1)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            save(cart);
            log.info("创建购物车记录：cartId={}", cart.getId());
            return convertToVO(cart);
        }
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        log.info("查询购物车列表：userId={}", userId);
        
        // 查询用户的购物车列表
        List<Cart> carts = query()
                .eq("user_id", userId)
                .orderByDesc("update_time")
                .list();
        
        // 转换为 VO 列表
        return carts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartVO updateQuantity(CartDTO cartDTO) {
        log.info("更新购物车商品数量：cartId={}, quantity={}", cartDTO.getId(), cartDTO.getQuantity());
        
        // 1. 查询购物车记录
        Cart cart = getById(cartDTO.getId());
        if (cart == null) {
            throw new BaseException("购物车记录不存在");
        }
        
        // 2. 检查数量是否合法
        if (cartDTO.getQuantity() <= 0) {
            throw new BaseException("商品数量必须大于 0");
        }
        
        // 3. 更新数量
        cart.setQuantity(cartDTO.getQuantity());
        cart.setUpdateTime(LocalDateTime.now());
        updateById(cart);
        
        return convertToVO(cart);
    }

    @Override
    @Transactional
    public void deleteCart(Long id) {
        log.info("删除购物车商品：cartId={}", id);
        removeById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        log.info("批量删除购物车商品：ids={}", ids);
        removeByIds(ids);
    }

    @Override
    @Transactional
    public void checkAll(Long userId, Integer isChecked) {
        log.info("全选/取消全选：userId={}, isChecked={}", userId, isChecked);
        
        // 更新用户所有购物车记录的选中状态
        List<Cart> carts = query()
                .eq("user_id", userId)
                .list();
        
        for (Cart cart : carts) {
            cart.setIsChecked(isChecked);
            cart.setUpdateTime(LocalDateTime.now());
        }
        
        updateBatchById(carts);
    }

    @Override
    @Transactional
    public CartVO updateChecked(CartDTO cartDTO) {
        log.info("更新购物车商品选中状态：cartId={}, isChecked={}", cartDTO.getId(), cartDTO.getIsChecked());

        Cart cart = getById(cartDTO.getId());
        if (cart == null) {
            throw new BaseException("购物车记录不存在");
        }
        if (cartDTO.getUserId() != null && !cart.getUserId().equals(cartDTO.getUserId())) {
            throw new BaseException("购物车记录不属于当前用户");
        }
        cart.setIsChecked(cartDTO.getIsChecked());
        cart.setUpdateTime(LocalDateTime.now());
        updateById(cart);

        return convertToVO(cart);
    }

    @Override
    public Integer getCartCount(Long userId) {
        log.info("获取购物车商品总数：userId={}", userId);
        
        // 查询用户购物车中所有商品的数量总和
        List<Cart> carts = query()
                .eq("user_id", userId)
                .list();
        
        return carts.stream()
                .mapToInt(Cart::getQuantity)
                .sum();
    }

    /**
     * 转换为 VO
     */
    private CartVO convertToVO(Cart cart) {
        BigDecimal subtotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
        
        return CartVO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .productId(cart.getProductId())
                .productName(cart.getProductName())
                .productImage(cart.getProductImage())
                .price(cart.getPrice())
                .quantity(cart.getQuantity())
                .isChecked(cart.getIsChecked())
                .createTime(cart.getCreateTime())
                .updateTime(cart.getUpdateTime())
                .subtotal(subtotal)
                .build();
    }
}
