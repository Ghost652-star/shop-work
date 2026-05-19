package com.ecommerce.exception;

public class AddressException extends BaseException {
    public AddressException(String message) {
        super(message);
    }

    public AddressException(Integer code, String message) {
        super(code, message);
    }
}
