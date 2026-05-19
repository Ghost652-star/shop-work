package com.ecommerce.common.handler;

import com.ecommerce.common.exception.*;
import com.ecommerce.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public Result<Object> handleUserException(UserException e) {
        log.error("用户业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ProductException.class)
    public Result<Object> handleProductException(ProductException e) {
        log.error("商品业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(OrderException.class)
    public Result<Object> handleOrderException(OrderException e) {
        log.error("订单业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(CartException.class)
    public Result<Object> handleCartException(CartException e) {
        log.error("购物车业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(CouponException.class)
    public Result<Object> handleCouponException(CouponException e) {
        log.error("优惠券业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AddressException.class)
    public Result<Object> handleAddressException(AddressException e) {
        log.error("地址业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AfterSaleException.class)
    public Result<Object> handleAfterSaleException(AfterSaleException e) {
        log.error("售后业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(CommentException.class)
    public Result<Object> handleCommentException(CommentException e) {
        log.error("评论业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(FavoriteException.class)
    public Result<Object> handleFavoriteException(FavoriteException e) {
        log.error("收藏业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    public Result<Object> handleBaseException(BaseException e) {
        log.error("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("服务器内部错误：", e);
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }
}
