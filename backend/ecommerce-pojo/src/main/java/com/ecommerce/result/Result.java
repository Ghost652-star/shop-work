package com.ecommerce.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 成功响应(无数据)
     */
    public static Result<Void> success() {
        return new Result<>(1, "success", null);
    }
    
    /**
     * 成功响应(带数据)
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }
    
    /**
     * 失败响应(带错误码和消息)
     */
    public static Result<Object> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
    
    /**
     * 失败响应(默认错误码0)
     */
    public static Result<Object> error(String msg) {
        return new Result<>(0, msg, null);
    }
}