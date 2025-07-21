package com.example.usermanagement.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.usermanagement.dto.*;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.entity.UserRole;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.mapper.UserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setStatus(1);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("USER");
        testRole.setDescription("普通用户");

        testUser.setRoles(Arrays.asList(testRole));
    }

    @Test
    void getUserProfile_Success() {
        // Given
        when(userMapper.findByIdWithRoles(1L)).thenReturn(testUser);

        // When
        UserResponse result = userService.getUserProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userMapper).findByIdWithRoles(1L);
    }

    @Test
    void getUserProfile_UserNotFound() {
        // Given
        when(userMapper.findByIdWithRoles(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.getUserProfile(1L));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void updateUserProfile_Success() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newusername");
        request.setEmail("newemail@example.com");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(userMapper.countByEmailExcludingUser("newemail@example.com", 1L)).thenReturn(0);
        when(userMapper.findByIdWithRoles(1L)).thenReturn(testUser);

        // When
        UserResponse result = userService.updateUserProfile(1L, request);

        // Then
        assertNotNull(result);
        verify(userMapper).updateById(any(User.class));
        verify(userMapper).findByIdWithRoles(1L);
    }

    @Test
    void updateUserProfile_UsernameExists() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("existinguser");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("existinguser");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.updateUserProfile(1L, request));
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void changePassword_Success() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // When
        userService.changePassword(1L, request);

        // Then
        verify(userMapper).updatePassword(eq(1L), eq("encodedNewPassword"), any(LocalDateTime.class));
    }

    @Test
    void changePassword_InvalidCurrentPassword() {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.changePassword(1L, request));
        assertEquals("当前密码错误", exception.getMessage());
    }

    @Test
    void createUser_Success() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setStatus(1);
        request.setRoles(Arrays.asList(1L));

        when(userMapper.countByUsername("newuser")).thenReturn(0);
        when(userMapper.countByEmail("newuser@example.com")).thenReturn(0);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userMapper.findByIdWithRoles(any())).thenReturn(testUser);

        // When
        UserResponse result = userService.createUser(request, 1L);

        // Then
        assertNotNull(result);
        verify(userMapper).insert(any(User.class));
        verify(userRoleMapper).insert(any(UserRole.class));
    }

    @Test
    void createUser_UsernameExists() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("existinguser");
        request.setEmail("newuser@example.com");

        when(userMapper.countByUsername("existinguser")).thenReturn(1);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.createUser(request, 1L));
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void updateUserStatus_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        userService.updateUserStatus(1L, 0, 2L);

        // Then
        verify(userMapper).updateStatus(eq(1L), eq(0), any(LocalDateTime.class));
    }

    @Test
    void updateUserStatus_CannotDisableSelf() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.updateUserStatus(1L, 0, 1L));
        assertEquals("不能禁用自己的账户", exception.getMessage());
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        userService.deleteUser(1L, 2L);

        // Then
        verify(userRoleMapper).deleteByUserId(1L);
        verify(userMapper).deleteById(1L);
    }

    @Test
    void deleteUser_CannotDeleteSelf() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.deleteUser(1L, 1L));
        assertEquals("不能删除自己的账户", exception.getMessage());
    }

    @Test
    void getUserList_Success() {
        // Given
        Page<User> pageParam = new Page<>(1, 10);
        IPage<User> userPage = new Page<>(1, 10);
        userPage.setRecords(Arrays.asList(testUser));
        userPage.setTotal(1);

        when(userMapper.findUsersWithPagination(any(Page.class), any(), any(), any()))
            .thenReturn(userPage);

        // When
        PageResponse<UserResponse> result = userService.getUserList(1, 10, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getUserStatistics_Success() {
        // Given
        when(userMapper.getTotalCount()).thenReturn(100L);
        when(userMapper.getEnabledUserCount()).thenReturn(80L);
        when(userMapper.getDisabledUserCount()).thenReturn(20L);

        // When
        Map<String, Object> result = userService.getUserStatistics();

        // Then
        assertNotNull(result);
        assertEquals(100L, result.get("totalUsers"));
        assertEquals(80L, result.get("enabledUsers"));
        assertEquals(20L, result.get("disabledUsers"));
        assertEquals(80.0, result.get("enabledPercentage"));
    }

    @Test
    void isUsernameAvailable_Available() {
        // Given
        when(userMapper.countByUsername("newuser")).thenReturn(0);

        // When
        boolean result = userService.isUsernameAvailable("newuser", null);

        // Then
        assertTrue(result);
    }

    @Test
    void isUsernameAvailable_NotAvailable() {
        // Given
        when(userMapper.countByUsername("existinguser")).thenReturn(1);

        // When
        boolean result = userService.isUsernameAvailable("existinguser", null);

        // Then
        assertFalse(result);
    }

    @Test
    void isEmailAvailable_Available() {
        // Given
        when(userMapper.countByEmail("new@example.com")).thenReturn(0);

        // When
        boolean result = userService.isEmailAvailable("new@example.com", null);

        // Then
        assertTrue(result);
    }

    @Test
    void resetUserPassword_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // When
        userService.resetUserPassword(1L, "newPassword", 2L);

        // Then
        verify(userMapper).updatePassword(eq(1L), eq("encodedNewPassword"), any(LocalDateTime.class));
    }

    @Test
    void resetUserPassword_CannotResetSelf() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.resetUserPassword(1L, "newPassword", 1L));
        assertEquals("请使用修改密码功能修改自己的密码", exception.getMessage());
    }

    @Test
    void batchUpdateUserStatus_Success() {
        // Given
        List<Long> userIds = Arrays.asList(2L, 3L);

        // When
        userService.batchUpdateUserStatus(userIds, 0, 1L);

        // Then
        verify(userMapper, times(2)).updateStatus(any(Long.class), eq(0), any(LocalDateTime.class));
    }

    @Test
    void batchUpdateUserStatus_CannotDisableSelf() {
        // Given
        List<Long> userIds = Arrays.asList(1L, 2L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.batchUpdateUserStatus(userIds, 0, 1L));
        assertEquals("不能禁用自己的账户", exception.getMessage());
    }
}