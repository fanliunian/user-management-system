package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 简单服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class SimpleServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

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
        
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).selectById(2L);
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
}