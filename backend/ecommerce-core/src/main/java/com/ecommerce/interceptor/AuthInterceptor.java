package com.ecommerce.interceptor;

import com.ecommerce.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(JwtUtils jwtUtils, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求缺少Token: {}", request.getRequestURI());
            send401(response);
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Integer userId = jwtUtils.parseToken(token);
            request.setAttribute("userId", userId);
            log.debug("Token验证成功: userId={}, uri={}", userId, request.getRequestURI());
            return true;
        } catch (Exception e) {
            log.warn("Token验证失败: uri={}, error={}", request.getRequestURI(), e.getMessage());
            send401(response);
            return false;
        }
    }

    private void send401(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "未登录或token已过期");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
