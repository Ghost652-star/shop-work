package com.ecommerce.exception;

public class CouponException extends BaseException {
    public CouponException(String message) {
        super(message);
    }

    public CouponException(Integer code, String message) {
        super(code, message);
    }
}
