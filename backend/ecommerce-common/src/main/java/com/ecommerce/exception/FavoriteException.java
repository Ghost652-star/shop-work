package com.ecommerce.exception;

public class FavoriteException extends BaseException {
    public FavoriteException(String message) {
        super(message);
    }

    public FavoriteException(Integer code, String message) {
        super(code, message);
    }
}
