package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.security.UserPrincipal;
import com.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            UserResponse userResponse = userService.getUserProfile(userPrincipal.getId());
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserResponse userResponse = userService.updateUserProfile(userPrincipal.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("个人信息更新成功", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userPrincipal.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户列表（管理员功能）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long roleId) {
        try {
            PageResponse<UserResponse> result = userService.getUserList(page, size, search, status, roleId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 创建新用户（管理员功能）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            UserResponse userResponse = userService.createUser(request, userPrincipal.getId());
            return ResponseEntity.ok(ApiResponse.success("用户创建成功", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户详情（管理员功能）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse userResponse = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新用户状态（管理员功能）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Integer status = request.get("status");
            if (status == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("状态参数不能为空"));
            }

            userService.updateUserStatus(id, status, userPrincipal.getId());
            String message = status == 1 ? "用户已启用" : "用户已禁用";
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 重置用户密码（管理员功能）
     */
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> resetUserPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            String password = request.get("password");
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("密码不能为空"));
            }

            userService.resetUserPassword(id, password, userPrincipal.getId());
            return ResponseEntity.ok(ApiResponse.success("密码重置成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除用户（管理员功能）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            userService.deleteUser(id, userPrincipal.getId());
            return ResponseEntity.ok(ApiResponse.success("用户删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 批量更新用户状态（管理员功能）
     */
    @PutMapping("/batch/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> batchUpdateUserStatus(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> userIds = (List<Long>) request.get("userIds");
            Integer status = (Integer) request.get("status");

            if (userIds == null || userIds.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("用户ID列表不能为空"));
            }

            if (status == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("状态参数不能为空"));
            }

            userService.batchUpdateUserStatus(userIds, status, userPrincipal.getId());
            String message = status == 1 ? "用户批量启用成功" : "用户批量禁用成功";
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户统计信息（管理员功能）
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStatistics() {
        try {
            Map<String, Object> statistics = userService.getUserStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(
            @RequestParam String username,
            @RequestParam(required = false) Long excludeUserId) {
        try {
            boolean available = userService.isUsernameAvailable(username, excludeUserId);
            return ResponseEntity.ok(ApiResponse.success(available));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查用户名失败"));
        }
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) Long excludeUserId) {
        try {
            boolean available = userService.isEmailAvailable(email, excludeUserId);
            return ResponseEntity.ok(ApiResponse.success(available));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查邮箱失败"));
        }
    }
}