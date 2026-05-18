package com.ecommerce.Controller.User;

import com.ecommerce.dto.LoginDTO;
import com.ecommerce.dto.RegisterDTO;
import com.ecommerce.dto.UpdateUserDTO;
import com.ecommerce.result.Result;
import com.ecommerce.service.User.UserService;
import com.ecommerce.vo.LoginVO;
import com.ecommerce.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     * @param loginDTO 登录请求DTO
     * @return 登录结果（用户信息 + token）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: username={}", loginDTO.getUsername());
        LoginVO loginVO = userService.login(loginDTO);
        log.info("用户登录成功: userId={}, username={}", loginVO.getUser().getId(), loginVO.getUser().getUsername());
        return Result.success(loginVO);
    }

    /**
     * 用户注册
     * @param registerDTO 注册请求DTO
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求: username={}, phone={}", registerDTO.getUsername(), registerDTO.getPhone());
        UserVO user = userService.register(registerDTO);
        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
        return Result.success(user);
    }
    
    /**
     * 获取用户信息
     * @param userId 用户 ID
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(@RequestParam Integer userId) {
        log.debug("查询用户信息：userId={}", userId);
        UserVO user = userService.getUserById(userId);
        return Result.success(user);
    }

    /**
     * 获取当前登录用户信息
     * @param request HTTP请求（由AuthInterceptor注入userId属性）
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public Result<UserVO> getCurrentUser(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.debug("获取当前登录用户: userId={}", userId);
        UserVO user = userService.getUserById(userId);
        return Result.success(user);
    }

    /**
     * 用户退出登录
     * @return 退出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("用户退出登录");
        return Result.success();
    }
    
    /**
     * 更新用户信息
     * @param updateUserDTO 用户更新DTO
     * @return 更新后的用户信息
     */
    @PutMapping("/update")
    public Result<UserVO> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        log.info("用户信息更新请求: userId={}, nickname={}", updateUserDTO.getId(), updateUserDTO.getNickname());
        UserVO user = userService.updateUser(updateUserDTO);
        log.info("用户信息更新成功: userId={}", user.getId());
        return Result.success(user);
    }
}
