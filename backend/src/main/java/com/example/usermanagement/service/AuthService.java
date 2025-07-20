package com.example.usermanagement.service;

import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.RegisterRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.mapper.UserRoleMapper;
import com.example.usermanagement.security.JwtTokenProvider;
import com.example.usermanagement.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @Transactional
    public Map<String, Object> login(LoginRequest loginRequest) {
        try {
            // 创建认证令牌
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // 获取用户信息
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // 检查用户状态
            if (!userPrincipal.isEnabled()) {
                throw new BusinessException("ACCOUNT_DISABLED", "账户已被禁用");
            }

            // 生成JWT令牌
            String accessToken = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            // 更新最后登录时间
            LocalDateTime now = LocalDateTime.now();
            userMapper.updateLastLoginTime(userPrincipal.getId(), now, now);

            // 获取完整用户信息
            User user = userMapper.findByIdWithRoles(userPrincipal.getId());
            UserResponse userResponse = UserResponse.from(user);

            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("user", userResponse);

            return response;

        } catch (AuthenticationException e) {
            throw new BusinessException("INVALID_CREDENTIALS", "用户名或密码错误");
        }
    }

    /**
     * 用户注册
     */
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userMapper.countByUsername(registerRequest.getUsername()) > 0) {
            throw new BusinessException("USERNAME_EXISTS", "用户名已存在");
        }

        // 检查邮箱是否已被注册
        if (userMapper.countByEmail(registerRequest.getEmail()) > 0) {
            throw new BusinessException("EMAIL_EXISTS", "邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(1); // 默认启用
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        userMapper.insert(user);

        // 为用户分配默认角色
        userRoleMapper.assignDefaultRole(user.getId());

        // 获取完整用户信息（包含角色）
        User savedUser = userMapper.findByIdWithRoles(user.getId());
        
        return UserResponse.from(savedUser);
    }

    /**
     * 刷新令牌
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("INVALID_TOKEN", "刷新令牌无效");
        }

        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userMapper.findByIdWithRoles(userId);
        
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在或已被禁用");
        }

        // 创建认证对象
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities());

        // 生成新的访问令牌
        String newAccessToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        response.put("tokenType", "Bearer");

        return response;
    }

    /**
     * 验证用户凭据
     */
    public boolean validateCredentials(String username, String password) {
        try {
            User user = userMapper.findByUsernameWithRoles(username);
            if (user == null || user.getStatus() != 1) {
                return false;
            }
            return passwordEncoder.matches(password, user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查用户是否存在且启用
     */
    public boolean isUserActiveByUsername(String username) {
        User user = userMapper.findByUsernameWithRoles(username);
        return user != null && user.getStatus() == 1;
    }

    /**
     * 检查用户是否存在且启用
     */
    public boolean isUserActiveById(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null && user.getStatus() == 1;
    }
    
    /**
     * 检查邮箱是否可用
     */
    public boolean isEmailAvailable(String email) {
        return userMapper.countByEmail(email) == 0;
    }
}