package com.ecommerce.common.exception;

public class CommentException extends BaseException {
    public CommentException(String message) {
        super(message);
    }

    public CommentException(Integer code, String message) {
        super(code, message);
    }
}
