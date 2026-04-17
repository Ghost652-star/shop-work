package com.ecommerce.handler;

import com.ecommerce.exception.BaseException;
import com.ecommerce.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public Result<Object> handleBaseException(BaseException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        return Result.error(500, "服务器内部错误");
    }
}