package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 简单用户服务测试
 */
@ExtendWith(MockitoExtension.class)
public class SimpleUserServiceTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testUserExists() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.selectById(2L)).thenReturn(null);

        // When & Then
        assertTrue(userService.userExists(1L));
        assertFalse(userService.userExists(2L));
        verify(userMapper).selectById(1L);
        verify(userMapper).selectById(2L);
    }

    @Test
    void testIsUsernameAvailable() {
        // Given
        when(userMapper.countByUsername("available")).thenReturn(0);
        when(userMapper.countByUsername("taken")).thenReturn(1);

        // When & Then
        assertTrue(userService.isUsernameAvailable("available", null));
        assertFalse(userService.isUsernameAvailable("taken", null));
        verify(userMapper).countByUsername("available");
        verify(userMapper).countByUsername("taken");
    }

    @Test
    void testIsEmailAvailable() {
        // Given
        when(userMapper.countByEmail("available@example.com")).thenReturn(0);
        when(userMapper.countByEmail("taken@example.com")).thenReturn(1);

        // When & Then
        assertTrue(userService.isEmailAvailable("available@example.com", null));
        assertFalse(userService.isEmailAvailable("taken@example.com", null));
        verify(userMapper).countByEmail("available@example.com");
        verify(userMapper).countByEmail("taken@example.com");
    }
}