package com.example.usermanagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.usermanagement.dto.*;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息管理服务
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户个人信息
     */
    public UserResponse getUserProfile(Long userId) {
        User user = userMapper.findByIdWithRoles(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        return UserResponse.from(user);
    }

    /**
     * 更新用户个人信息
     */
    @Transactional
    public UserResponse updateUserProfile(Long userId, UpdateUserRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        boolean updated = false;

        // 更新用户名
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            // 检查用户名是否已被其他用户使用
            User existingUser = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("username", request.getUsername())
                    .ne("id", userId)
            );
            if (existingUser != null) {
                throw new BusinessException("USERNAME_EXISTS", "用户名已存在");
            }
            user.setUsername(request.getUsername());
            updated = true;
        }

        // 更新邮箱
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // 检查邮箱是否已被其他用户使用
            if (userMapper.countByEmailExcludingUser(request.getEmail(), userId) > 0) {
                throw new BusinessException("EMAIL_EXISTS", "邮箱已被注册");
            }
            user.setEmail(request.getEmail());
            updated = true;
        }

        if (updated) {
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }

        // 返回更新后的用户信息
        User updatedUser = userMapper.findByIdWithRoles(userId);
        return UserResponse.from(updatedUser);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("INVALID_CURRENT_PASSWORD", "当前密码错误");
        }

        // 更新密码
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        userMapper.updatePassword(userId, encodedNewPassword, LocalDateTime.now());
    }

    /**
     * 获取用户详情（管理员功能）
     */
    public UserResponse getUserById(Long userId) {
        User user = userMapper.findByIdWithRoles(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        return UserResponse.from(user);
    }

    /**
     * 分页查询用户列表（管理员功能）
     */
    public PageResponse<UserResponse> getUserList(int page, int size, String search, Integer status, Long roleId) {
        Page<User> pageParam = new Page<>(page, size);
        IPage<User> userPage = userMapper.findUsersWithPagination(pageParam, search, status, roleId);

        List<UserResponse> userResponses = userPage.getRecords().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());

        return PageResponse.of(userResponses, userPage.getTotal(), (int) userPage.getCurrent() - 1, (int) userPage.getSize());
    }

    /**
     * 更新用户状态（管理员功能）
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status, Long currentUserId) {
        // 检查是否尝试禁用自己的账户
        if (userId.equals(currentUserId) && status == 0) {
            throw new BusinessException("CANNOT_DISABLE_SELF", "不能禁用自己的账户");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        userMapper.updateStatus(userId, status, LocalDateTime.now());
    }

    /**
     * 删除用户（管理员功能）
     */
    @Transactional
    public void deleteUser(Long userId, Long currentUserId) {
        // 检查是否尝试删除自己的账户
        if (userId.equals(currentUserId)) {
            throw new BusinessException("CANNOT_DELETE_SELF", "不能删除自己的账户");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);
        
        // 删除用户
        userMapper.deleteById(userId);
    }

    /**
     * 批量更新用户状态（管理员功能）
     */
    @Transactional
    public void batchUpdateUserStatus(List<Long> userIds, Integer status, Long currentUserId) {
        // 检查是否包含当前用户
        if (userIds.contains(currentUserId) && status == 0) {
            throw new BusinessException("CANNOT_DISABLE_SELF", "不能禁用自己的账户");
        }

        LocalDateTime now = LocalDateTime.now();
        for (Long userId : userIds) {
            userMapper.updateStatus(userId, status, now);
        }
    }

    /**
     * 获取用户统计信息
     */
    public Map<String, Object> getUserStatistics() {
        long totalUsers = userMapper.getTotalCount();
        long enabledUsers = userMapper.getEnabledUserCount();
        long disabledUsers = userMapper.getDisabledUserCount();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", totalUsers);
        statistics.put("enabledUsers", enabledUsers);
        statistics.put("disabledUsers", disabledUsers);
        statistics.put("enabledPercentage", totalUsers > 0 ? (double) enabledUsers / totalUsers * 100 : 0);

        return statistics;
    }

    /**
     * 检查用户是否存在
     */
    public boolean userExists(Long userId) {
        return userMapper.selectById(userId) != null;
    }

    /**
     * 检查用户名是否可用
     */
    public boolean isUsernameAvailable(String username, Long excludeUserId) {
        if (excludeUserId != null) {
            User existingUser = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                    .eq("username", username)
                    .ne("id", excludeUserId)
            );
            return existingUser == null;
        } else {
            return userMapper.countByUsername(username) == 0;
        }
    }

    /**
     * 检查邮箱是否可用
     */
    public boolean isEmailAvailable(String email, Long excludeUserId) {
        if (excludeUserId != null) {
            return userMapper.countByEmailExcludingUser(email, excludeUserId) == 0;
        } else {
            return userMapper.countByEmail(email) == 0;
        }
    }
}