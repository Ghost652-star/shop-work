package com.ecommerce.exception;

public class AfterSaleException extends BaseException {
    public AfterSaleException(String message) {
        super(message);
    }

    public AfterSaleException(Integer code, String message) {
        super(code, message);
    }
}
