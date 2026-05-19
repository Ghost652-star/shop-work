package com.ecommerce.exception;

public class UserException extends BaseException {
    public UserException(String message) {
        super(message);
    }

    public UserException(Integer code, String message) {
        super(code, message);
    }
}
