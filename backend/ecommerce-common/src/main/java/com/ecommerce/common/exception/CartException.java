package com.ecommerce.common.exception;

public class CartException extends BaseException {
    public CartException(String message) {
        super(message);
    }

    public CartException(Integer code, String message) {
        super(code, message);
    }
}
