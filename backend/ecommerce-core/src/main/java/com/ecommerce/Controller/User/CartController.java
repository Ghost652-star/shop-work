package com.ecommerce.Controller.User;

import com.ecommerce.dto.CartDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.CartService;
import com.ecommerce.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 添加商品到购物车
     * @param cartDTO 购物车 DTO
     * @return 购物车 VO
     */
    @PostMapping("/add")
    public Result<CartVO> addToCart(@RequestBody CartDTO cartDTO) {
        log.info("添加商品到购物车请求：userId={}, productId={}", cartDTO.getUserId(), cartDTO.getProductId());
        CartVO cartVO = cartService.addToCart(cartDTO);
        log.info("添加商品到购物车成功：cartId={}", cartVO.getId());
        return Result.success(cartVO);
    }

    /**
     * 查询购物车列表
     * @param userId 用户 ID
     * @return 购物车 VO 列表
     */
    @GetMapping("/list")
    public Result<List<CartVO>> getCartList(@RequestParam Long userId) {
        log.info("查询购物车列表请求：userId={}", userId);
        List<CartVO> list = cartService.getCartList(userId);
        log.info("查询购物车列表成功：共{}条", list.size());
        return Result.success(list);
    }

    /**
     * 更新购物车商品数量
     * @param cartDTO 购物车 DTO
     * @return 购物车 VO
     */
    @PutMapping("/update")
    public Result<CartVO> updateQuantity(@RequestBody CartDTO cartDTO) {
        log.info("更新购物车商品数量请求：cartId={}, quantity={}", cartDTO.getId(), cartDTO.getQuantity());
        CartVO cartVO = cartService.updateQuantity(cartDTO);
        log.info("更新购物车商品数量成功：cartId={}", cartVO.getId());
        return Result.success(cartVO);
    }

    /**
     * 删除购物车商品
     * @param id 购物车记录 ID
     * @return 成功消息
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteCart(@RequestParam Long id) {
        log.info("删除购物车商品请求：id={}", id);
        cartService.deleteCart(id);
        log.info("删除购物车商品成功：id={}", id);
        return Result.success();
    }

    /**
     * 批量删除购物车商品
     * @param cartDTO 包含 ID 列表的 DTO
     * @return 成功消息
     */
    @DeleteMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody CartDTO cartDTO) {
        log.info("批量删除购物车商品请求：ids={}", cartDTO.getIds());
        cartService.batchDelete(cartDTO.getIds());
        log.info("批量删除购物车商品成功");
        return Result.success();
    }

    /**
     * 更新购物车商品选中状态
     * @param cartDTO 购物车 DTO
     * @return 购物车 VO
     */
    @PutMapping("/check")
    public Result<CartVO> updateChecked(@RequestBody CartDTO cartDTO) {
        log.info("更新购物车商品选中状态请求：cartId={}, isChecked={}", cartDTO.getId(), cartDTO.getIsChecked());
        CartVO cartVO = cartService.updateChecked(cartDTO);
        log.info("更新购物车商品选中状态成功：cartId={}", cartVO.getId());
        return Result.success(cartVO);
    }

    /**
     * 全选/取消全选
     * @param cartDTO 包含 userId 和 isChecked 的 DTO
     * @return 成功消息
     */
    @PutMapping("/check-all")
    public Result<Void> checkAll(@RequestBody CartDTO cartDTO) {
        log.info("全选/取消全选请求：userId={}, isChecked={}", cartDTO.getUserId(), cartDTO.getIsChecked());
        cartService.checkAll(cartDTO.getUserId(), cartDTO.getIsChecked());
        log.info("全选/取消全选成功");
        return Result.success();
    }

    /**
     * 获取购物车商品总数
     * @param userId 用户 ID
     * @return 商品总数
     */
    @GetMapping("/count")
    public Result<Integer> getCartCount(@RequestParam Long userId) {
        log.info("获取购物车商品总数请求：userId={}", userId);
        Integer count = cartService.getCartCount(userId);
        log.info("获取购物车商品总数成功：count={}", count);
        return Result.success(count);
    }
}
