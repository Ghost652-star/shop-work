package com.ecommerce.exception;

public class ProductException extends BaseException {
    public ProductException(String message) {
        super(message);
    }
    
    public ProductException(Integer code, String message) {
        super(code, message);
    }
}