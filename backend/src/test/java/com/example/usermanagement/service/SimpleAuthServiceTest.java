package com.example.usermanagement.service;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 简单认证服务测试
 */
@ExtendWith(MockitoExtension.class)
public class SimpleAuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

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

        Role testRole = new Role();
        testRole.setId(1L);
        testRole.setName("USER");
        testRole.setDescription("普通用户");

        testUser.setRoles(Arrays.asList(testRole));
    }

    @Test
    void testValidateCredentials_Success() {
        // Given
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // When
        boolean result = authService.validateCredentials("testuser", "password");

        // Then
        assertTrue(result);
        verify(userMapper).findByUsernameWithRoles("testuser");
        verify(passwordEncoder).matches("password", "encodedPassword");
    }

    @Test
    void testValidateCredentials_WrongPassword() {
        // Given
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When
        boolean result = authService.validateCredentials("testuser", "wrongpassword");

        // Then
        assertFalse(result);
        verify(userMapper).findByUsernameWithRoles("testuser");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void testValidateCredentials_UserNotFound() {
        // Given
        when(userMapper.findByUsernameWithRoles("nonexistent")).thenReturn(null);

        // When
        boolean result = authService.validateCredentials("nonexistent", "password");

        // Then
        assertFalse(result);
        verify(userMapper).findByUsernameWithRoles("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testIsUserActiveByUsername_Active() {
        // Given
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        boolean result = authService.isUserActiveByUsername("testuser");

        // Then
        assertTrue(result);
        verify(userMapper).findByUsernameWithRoles("testuser");
    }

    @Test
    void testIsUserActiveByUsername_Inactive() {
        // Given
        testUser.setStatus(0); // 禁用用户
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        boolean result = authService.isUserActiveByUsername("testuser");

        // Then
        assertFalse(result);
        verify(userMapper).findByUsernameWithRoles("testuser");
    }

    @Test
    void testIsUserActiveByUsername_NotFound() {
        // Given
        when(userMapper.findByUsernameWithRoles("nonexistent")).thenReturn(null);

        // When
        boolean result = authService.isUserActiveByUsername("nonexistent");

        // Then
        assertFalse(result);
        verify(userMapper).findByUsernameWithRoles("nonexistent");
    }
}