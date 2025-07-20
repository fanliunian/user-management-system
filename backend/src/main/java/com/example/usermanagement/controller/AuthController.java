package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Map<String, Object> result = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("登录成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserResponse userResponse = authService.register(registerRequest);
            return ResponseEntity.ok(ApiResponse.success("注册成功", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("刷新令牌不能为空"));
            }

            Map<String, Object> result = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // JWT是无状态的，客户端删除令牌即可实现登出
        // 这里可以添加令牌黑名单逻辑（可选）
        return ResponseEntity.ok(ApiResponse.success("登出成功"));
    }

    /**
     * 验证令牌
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("无效的认证头"));
            }

            // 如果能到达这里，说明JWT过滤器已经验证了令牌
            return ResponseEntity.ok(ApiResponse.success("令牌有效"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("令牌验证失败"));
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        try {
            boolean available = authService.isUserActiveByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(!available)); // 返回是否可用（不存在则可用）
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查用户名失败"));
        }
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        try {
            boolean available = authService.isEmailAvailable(email);
            return ResponseEntity.ok(ApiResponse.success(available));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查邮箱失败"));
        }
    }
}