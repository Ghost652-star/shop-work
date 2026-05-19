package com.ecommerce.exception;

public class OrderException extends BaseException {
    public OrderException(String message) {
        super(message);
    }

    public OrderException(Integer code, String message) {
        super(code, message);
    }
}
