package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecommerce.dto.LoginDTO;
import com.ecommerce.dto.RegisterDTO;
import com.ecommerce.dto.UpdateUserDTO;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BaseException;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.JwtUtils;
import com.ecommerce.vo.LoginVO;
import com.ecommerce.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;

    public UserServiceImpl(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户登录
     * @param loginDTO 登录请求 DTO
     * @return 登录结果（用户信息 + token）
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        log.debug("开始处理用户登录: username={}", loginDTO.getUsername());
        
        // 根据用户名查询用户
        User user = findByUsername(loginDTO.getUsername());
        if (user == null) {
            log.warn("用户登录失败: 用户名不存在, username={}", loginDTO.getUsername());
            throw new BaseException("用户名不存在");
        }
        
        // 验证密码
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            log.warn("用户登录失败: 密码错误, username={}", loginDTO.getUsername());
            throw new BaseException("密码错误");
        }
        
        // 验证用户状态
        if (user.getStatus() == 0) {
            log.warn("用户登录失败: 账号已被禁用, userId={}, username={}", user.getId(), user.getUsername());
            throw new BaseException("账号已被禁用");
        }
        
        log.debug("用户登录验证通过: userId={}", user.getId());
        // 转换为 UserVO 并生成 token
        UserVO userVO = convertToVO(user);
        String token = jwtUtils.generateToken(user.getId());
        return LoginVO.builder().user(userVO).token(token).build();
    }
    
    /**
     * 用户注册
     * @param registerDTO 注册请求DTO
     * @return 用户信息
     */
    @Override
    public UserVO register(RegisterDTO registerDTO) {
        log.debug("开始处理用户注册: username={}, phone={}", registerDTO.getUsername(), registerDTO.getPhone());
        
        // 检查用户名是否已存在
        if (findByUsername(registerDTO.getUsername()) != null) {
            log.warn("用户注册失败: 用户名已存在, username={}", registerDTO.getUsername());
            throw new BaseException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (findByPhone(registerDTO.getPhone()) != null) {
            log.warn("用户注册失败: 手机号已被注册, phone={}", maskPhone(registerDTO.getPhone()));
            throw new BaseException("手机号已被注册");
        }
        
        // 创建新用户
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(registerDTO.getPassword())
                .phone(registerDTO.getPhone())
                .nickname(generateNickname(registerDTO.getUsername()))
                .avatar("https://picsum.photos/100/100?random=" + new Random().nextInt(1000))
                .gender(0)
                .status(1)
                .build();
        
        // 保存用户
        save(user);
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
        
        // 转换为 UserVO
        return convertToVO(user);
    }
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public UserVO getUserById(Integer userId) {
        log.debug("查询用户详情: userId={}", userId);
        User user = getById(userId);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            throw new BaseException("用户不存在");
        }
        return convertToVO(user);
    }
    
    /**
     * 更新用户信息
     * @param updateUserDTO 用户更新DTO
     * @return 更新后的用户信息
     */
    @Override
    public UserVO updateUser(UpdateUserDTO updateUserDTO) {
        log.debug("开始更新用户信息: userId={}", updateUserDTO.getId());
        
        // 查询用户是否存在
        User user = getById(updateUserDTO.getId());
        if (user == null) {
            log.warn("更新用户信息失败: 用户不存在, userId={}", updateUserDTO.getId());
            throw new BaseException("用户不存在");
        }
        
        // 检查手机号是否被其他用户使用
        if (updateUserDTO.getPhone() != null && !updateUserDTO.getPhone().equals(user.getPhone())) {
            User existingUser = findByPhone(updateUserDTO.getPhone());
            if (existingUser != null && !existingUser.getId().equals(updateUserDTO.getId())) {
                log.warn("更新用户信息失败: 手机号已被其他用户使用, phone={}", maskPhone(updateUserDTO.getPhone()));
                throw new BaseException("手机号已被其他用户使用");
            }
        }
        
        // 更新用户信息
        if (updateUserDTO.getNickname() != null) {
            user.setNickname(updateUserDTO.getNickname());
        }
        if (updateUserDTO.getPhone() != null) {
            user.setPhone(updateUserDTO.getPhone());
        }
        if (updateUserDTO.getEmail() != null) {
            user.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getAvatar() != null) {
            user.setAvatar(updateUserDTO.getAvatar());
        }
        if (updateUserDTO.getGender() != null) {
            user.setGender(updateUserDTO.getGender());
        }
        
        // 保存更新
        updateById(user);
        log.info("用户信息更新成功: userId={}", user.getId());
        
        return convertToVO(user);
    }
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User findByUsername(String username) {
        return query().eq("username", username).one();
    }
    
    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    @Override
    public User findByPhone(String phone) {
        return query().eq("phone", phone).one();
    }
    
    /**
     * 将 User 实体转换为 UserVO
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .build();
    }
    
    /**
     * 生成随机昵称
     * @param username 用户名
     * @return 昵称
     */
    private String generateNickname(String username) {
        String[] suffixes = {"aaa","bbb","ccc","ddd","eee","fff","ggg","hhh","iii","jjj"};
        Random random = new Random();
        return username + suffixes[random.nextInt(suffixes.length)];
    }
    
    /**
     * 手机号脱敏
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}