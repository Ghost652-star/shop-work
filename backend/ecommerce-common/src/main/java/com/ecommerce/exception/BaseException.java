package com.ecommerce.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    private Integer code;
    
    public BaseException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    // 手动添加 getCode 方法
    public Integer getCode() {
        return code;
    }
    
    // 手动添加 setCode 方法
    public void setCode(Integer code) {
        this.code = code;
    }
}