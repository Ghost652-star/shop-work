package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录视图对象
 * 用于返回用户登录结果，包含用户信息和JWT token
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * JWT token
     */
    private String token;
}
