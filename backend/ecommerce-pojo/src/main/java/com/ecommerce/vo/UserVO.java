package com.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户视图对象
 * 用于返回给前端的用户数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    /**
     * 用户ID
     */
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 头像路径
     */
    private String avatar;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
}