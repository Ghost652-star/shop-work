package com.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecommerce.dto.LoginDTO;
import com.ecommerce.dto.RegisterDTO;
import com.ecommerce.dto.UpdateUserDTO;
import com.ecommerce.entity.User;
import com.ecommerce.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param loginDTO 登录请求DTO
     * @return 用户信息
     */
    UserVO login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     * @param registerDTO 注册请求DTO
     * @return 用户信息
     */
    UserVO register(RegisterDTO registerDTO);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Integer userId);
    
    /**
     * 更新用户信息
     * @param updateUserDTO 用户更新DTO
     * @return 更新后的用户信息
     */
    UserVO updateUser(UpdateUserDTO updateUserDTO);
}