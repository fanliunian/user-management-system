package com.example.usermanagement.service;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.UserRole;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.RoleMapper;
import com.example.usermanagement.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色权限管理服务
 */
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 获取所有角色列表
     */
    public List<Role> getAllRoles() {
        return roleMapper.findAllRoles();
    }

    /**
     * 根据ID获取角色
     */
    public Role getRoleById(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("ROLE_NOT_FOUND", "角色不存在");
        }
        return role;
    }

    /**
     * 根据名称获取角色
     */
    public Role getRoleByName(String roleName) {
        return roleMapper.findByName(roleName);
    }

    /**
     * 创建新角色
     */
    @Transactional
    public Role createRole(String name, String description) {
        // 检查角色名称是否已存在
        if (roleMapper.countByName(name) > 0) {
            throw new BusinessException("ROLE_NAME_EXISTS", "角色名称已存在");
        }

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        roleMapper.insert(role);
        return role;
    }

    /**
     * 更新角色信息
     */
    @Transactional
    public Role updateRole(Long roleId, String name, String description) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("ROLE_NOT_FOUND", "角色不存在");
        }

        // 检查角色名称是否被其他角色使用
        if (name != null && !name.equals(role.getName())) {
            if (roleMapper.countByNameExcludingRole(name, roleId) > 0) {
                throw new BusinessException("ROLE_NAME_EXISTS", "角色名称已存在");
            }
            role.setName(name);
        }

        if (description != null) {
            role.setDescription(description);
        }

        role.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(role);

        return role;
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("ROLE_NOT_FOUND", "角色不存在");
        }

        // 检查是否有用户正在使用该角色
        int userCount = roleMapper.countUsersWithRole(roleId);
        if (userCount > 0) {
            throw new BusinessException("CANNOT_DELETE_ROLE_IN_USE", 
                "角色正在使用中，无法删除。当前有 " + userCount + " 个用户使用此角色");
        }

        // 删除角色
        roleMapper.deleteById(roleId);
    }

    /**
     * 为用户分配角色
     */
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // 先删除用户现有的所有角色
        userRoleMapper.deleteByUserId(userId);

        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                .map(roleId -> new UserRole(userId, roleId))
                .collect(java.util.stream.Collectors.toList());
            
            userRoleMapper.batchInsert(userRoles);
        }
    }

    /**
     * 为用户添加角色
     */
    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("ROLE_NOT_FOUND", "角色不存在");
        }

        // 检查用户是否已有该角色
        if (userRoleMapper.countByUserIdAndRoleId(userId, roleId) > 0) {
            throw new BusinessException("ROLE_ALREADY_ASSIGNED", "用户已拥有该角色");
        }

        // 添加角色
        UserRole userRole = new UserRole(userId, roleId);
        userRoleMapper.insert(userRole);
    }

    /**
     * 移除用户角色
     */
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        // 检查用户是否有该角色
        if (userRoleMapper.countByUserIdAndRoleId(userId, roleId) == 0) {
            throw new BusinessException("ROLE_NOT_ASSIGNED", "用户没有该角色");
        }

        // 移除角色
        userRoleMapper.deleteByUserIdAndRoleId(userId, roleId);
    }

    /**
     * 获取用户的角色列表
     */
    public List<Role> getUserRoles(Long userId) {
        return roleMapper.findByUserId(userId);
    }

    /**
     * 获取拥有特定角色的用户ID列表
     */
    public List<Long> getUserIdsByRole(Long roleId) {
        return userRoleMapper.findUserIdsByRoleId(roleId);
    }

    /**
     * 检查用户是否有特定角色
     */
    public boolean userHasRole(Long userId, String roleName) {
        List<Role> userRoles = roleMapper.findByUserId(userId);
        return userRoles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * 检查用户是否是管理员
     */
    public boolean isUserAdmin(Long userId) {
        return userHasRole(userId, "ADMIN");
    }

    /**
     * 获取默认用户角色
     */
    public Role getDefaultUserRole() {
        Role role = roleMapper.findDefaultUserRole();
        if (role == null) {
            throw new BusinessException("DEFAULT_ROLE_NOT_FOUND", "默认用户角色不存在");
        }
        return role;
    }

    /**
     * 获取管理员角色
     */
    public Role getAdminRole() {
        Role role = roleMapper.findAdminRole();
        if (role == null) {
            throw new BusinessException("ADMIN_ROLE_NOT_FOUND", "管理员角色不存在");
        }
        return role;
    }

    /**
     * 检查角色是否可以删除
     */
    public boolean canDeleteRole(Long roleId) {
        return roleMapper.countUsersWithRole(roleId) == 0;
    }

    /**
     * 获取角色使用统计
     */
    public int getRoleUsageCount(Long roleId) {
        return roleMapper.countUsersWithRole(roleId);
    }

    /**
     * 批量删除角色
     */
    @Transactional
    public void batchDeleteRoles(List<Long> roleIds) {
        for (Long roleId : roleIds) {
            deleteRole(roleId);
        }
    }
}