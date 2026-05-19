package com.ecommerce;

import com.ecommerce.exception.*;
import com.ecommerce.handler.GlobalExceptionHandler;
import com.ecommerce.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void userException_shouldReturnCode500() {
        Result<Object> result = handler.handleUserException(new UserException("用户名不存在"));
        assertEquals(500, result.getCode());
        assertEquals("用户名不存在", result.getMsg());
    }

    @Test
    void productException_shouldReturnCode500() {
        Result<Object> result = handler.handleProductException(new ProductException("商品不存在"));
        assertEquals(500, result.getCode());
        assertEquals("商品不存在", result.getMsg());
    }

    @Test
    void orderException_shouldReturnCode500() {
        Result<Object> result = handler.handleOrderException(new OrderException("订单无法取消"));
        assertEquals(500, result.getCode());
        assertEquals("订单无法取消", result.getMsg());
    }

    @Test
    void cartException_shouldReturnCode500() {
        Result<Object> result = handler.handleCartException(new CartException("购物车记录不存在"));
        assertEquals(500, result.getCode());
        assertEquals("购物车记录不存在", result.getMsg());
    }

    @Test
    void couponException_shouldReturnCode500() {
        Result<Object> result = handler.handleCouponException(new CouponException("优惠券已过期"));
        assertEquals(500, result.getCode());
        assertEquals("优惠券已过期", result.getMsg());
    }

    @Test
    void addressException_shouldReturnCode500() {
        Result<Object> result = handler.handleAddressException(new AddressException("地址不存在"));
        assertEquals(500, result.getCode());
        assertEquals("地址不存在", result.getMsg());
    }

    @Test
    void afterSaleException_shouldReturnCode500() {
        Result<Object> result = handler.handleAfterSaleException(new AfterSaleException("售后单不存在"));
        assertEquals(500, result.getCode());
        assertEquals("售后单不存在", result.getMsg());
    }

    @Test
    void commentException_shouldReturnCode500() {
        Result<Object> result = handler.handleCommentException(new CommentException("评论不存在"));
        assertEquals(500, result.getCode());
        assertEquals("评论不存在", result.getMsg());
    }

    @Test
    void favoriteException_shouldReturnCode500() {
        Result<Object> result = handler.handleFavoriteException(new FavoriteException("商品已收藏"));
        assertEquals(500, result.getCode());
        assertEquals("商品已收藏", result.getMsg());
    }

    @Test
    void customCode_shouldBePreserved() {
        Result<Object> result = handler.handleUserException(new UserException(4001, "自定义错误码"));
        assertEquals(4001, result.getCode());
        assertEquals("自定义错误码", result.getMsg());
    }

    @Test
    void fallbackException_shouldReturnCode500() {
        Result<Object> result = handler.handleException(new RuntimeException("unexpected"));
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("服务器内部错误"));
    }

    @Test
    void exceptionHierarchy_allExtendBaseException() {
        assertTrue(new UserException("") instanceof BaseException);
        assertTrue(new ProductException("") instanceof BaseException);
        assertTrue(new OrderException("") instanceof BaseException);
        assertTrue(new CartException("") instanceof BaseException);
        assertTrue(new CouponException("") instanceof BaseException);
        assertTrue(new AddressException("") instanceof BaseException);
        assertTrue(new AfterSaleException("") instanceof BaseException);
        assertTrue(new CommentException("") instanceof BaseException);
        assertTrue(new FavoriteException("") instanceof BaseException);
    }
}
