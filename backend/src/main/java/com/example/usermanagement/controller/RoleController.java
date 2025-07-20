package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ApiResponse;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有角色列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id);
            return ResponseEntity.ok(ApiResponse.success(role));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 创建新角色
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@Valid @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String description = request.get("description");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色名称不能为空"));
            }

            Role role = roleService.createRole(name.trim(), description);
            return ResponseEntity.ok(ApiResponse.success("角色创建成功", role));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String description = request.get("description");

            Role role = roleService.updateRole(id, name, description);
            return ResponseEntity.ok(ApiResponse.success("角色更新成功", role));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(ApiResponse.success("角色删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<String>> batchDeleteRoles(@RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> roleIds = request.get("roleIds");
            if (roleIds == null || roleIds.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色ID列表不能为空"));
            }

            roleService.batchDeleteRoles(roleIds);
            return ResponseEntity.ok(ApiResponse.success("角色批量删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 为用户分配角色
     */
    @PutMapping("/assign/{userId}")
    public ResponseEntity<ApiResponse<String>> assignRolesToUser(
            @PathVariable Long userId,
            @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> roleIds = request.get("roleIds");
            roleService.assignRolesToUser(userId, roleIds);
            return ResponseEntity.ok(ApiResponse.success("用户角色分配成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 为用户添加角色
     */
    @PostMapping("/add/{userId}/{roleId}")
    public ResponseEntity<ApiResponse<String>> addRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        try {
            roleService.addRoleToUser(userId, roleId);
            return ResponseEntity.ok(ApiResponse.success("角色添加成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/remove/{userId}/{roleId}")
    public ResponseEntity<ApiResponse<String>> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        try {
            roleService.removeRoleFromUser(userId, roleId);
            return ResponseEntity.ok(ApiResponse.success("角色移除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Role>>> getUserRoles(@PathVariable Long userId) {
        try {
            List<Role> roles = roleService.getUserRoles(userId);
            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取拥有特定角色的用户ID列表
     */
    @GetMapping("/{roleId}/users")
    public ResponseEntity<ApiResponse<List<Long>>> getUserIdsByRole(@PathVariable Long roleId) {
        try {
            List<Long> userIds = roleService.getUserIdsByRole(roleId);
            return ResponseEntity.ok(ApiResponse.success(userIds));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 检查角色是否可以删除
     */
    @GetMapping("/{roleId}/can-delete")
    public ResponseEntity<ApiResponse<Boolean>> canDeleteRole(@PathVariable Long roleId) {
        try {
            boolean canDelete = roleService.canDeleteRole(roleId);
            return ResponseEntity.ok(ApiResponse.success(canDelete));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取角色使用统计
     */
    @GetMapping("/{roleId}/usage")
    public ResponseEntity<ApiResponse<Integer>> getRoleUsage(@PathVariable Long roleId) {
        try {
            int usageCount = roleService.getRoleUsageCount(roleId);
            return ResponseEntity.ok(ApiResponse.success(usageCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 检查用户是否有特定角色
     */
    @GetMapping("/check/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<Boolean>> checkUserRole(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        try {
            boolean hasRole = roleService.userHasRole(userId, roleName);
            return ResponseEntity.ok(ApiResponse.success(hasRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}