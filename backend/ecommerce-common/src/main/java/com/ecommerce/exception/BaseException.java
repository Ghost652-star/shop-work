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
    
   
    public Integer getCode() {
        return code;
    }
    
 
    public void setCode(Integer code) {
        this.code = code;
    }
}