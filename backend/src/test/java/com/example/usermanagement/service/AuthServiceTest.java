package com.example.usermanagement.service;

import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.RegisterRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.mapper.UserMapper;
import com.example.usermanagement.mapper.UserRoleMapper;
import com.example.usermanagement.security.JwtTokenProvider;
import com.example.usermanagement.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserPrincipal userPrincipal;
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
        userPrincipal = UserPrincipal.create(testUser);
    }

    @Test
    void login_Success() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(tokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(authentication)).thenReturn("refresh-token");
        when(userMapper.findByIdWithRoles(1L)).thenReturn(testUser);

        // When
        Map<String, Object> result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("access-token", result.get("accessToken"));
        assertEquals("refresh-token", result.get("refreshToken"));
        assertEquals("Bearer", result.get("tokenType"));
        assertNotNull(result.get("user"));
        
        verify(userMapper).updateLastLoginTime(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void login_InvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginRequest));
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void login_AccountDisabled() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        testUser.setStatus(0); // 禁用账户
        UserPrincipal disabledUserPrincipal = UserPrincipal.create(testUser);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(disabledUserPrincipal);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginRequest));
        assertEquals("账户已被禁用", exception.getMessage());
    }

    @Test
    void register_Success() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("newuser", "new@example.com", "password123");
        
        when(userMapper.countByUsername("newuser")).thenReturn(0);
        when(userMapper.countByEmail("new@example.com")).thenReturn(0);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userMapper.findByIdWithRoles(any())).thenReturn(testUser);

        // When
        UserResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        verify(userMapper).insert(any(User.class));
        verify(userRoleMapper).assignDefaultRole(any(Long.class));
    }

    @Test
    void register_UsernameExists() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("existinguser", "new@example.com", "password123");
        
        when(userMapper.countByUsername("existinguser")).thenReturn(1);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.register(registerRequest));
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void register_EmailExists() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("newuser", "existing@example.com", "password123");
        
        when(userMapper.countByUsername("newuser")).thenReturn(0);
        when(userMapper.countByEmail("existing@example.com")).thenReturn(1);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.register(registerRequest));
        assertEquals("邮箱已被注册", exception.getMessage());
    }

    @Test
    void refreshToken_Success() {
        // Given
        String refreshToken = "valid-refresh-token";
        
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(userMapper.findByIdWithRoles(1L)).thenReturn(testUser);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("new-access-token");
        when(tokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("new-refresh-token");

        // When
        Map<String, Object> result = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(result);
        assertEquals("new-access-token", result.get("accessToken"));
        assertEquals("new-refresh-token", result.get("refreshToken"));
        assertEquals("Bearer", result.get("tokenType"));
    }

    @Test
    void refreshToken_InvalidToken() {
        // Given
        String refreshToken = "invalid-refresh-token";
        
        when(tokenProvider.validateToken(refreshToken)).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("刷新令牌无效", exception.getMessage());
    }

    @Test
    void refreshToken_UserNotFound() {
        // Given
        String refreshToken = "valid-refresh-token";
        
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(userMapper.findByIdWithRoles(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("用户不存在或已被禁用", exception.getMessage());
    }

    @Test
    void refreshToken_UserDisabled() {
        // Given
        String refreshToken = "valid-refresh-token";
        testUser.setStatus(0); // 禁用用户
        
        when(tokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(userMapper.findByIdWithRoles(1L)).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("用户不存在或已被禁用", exception.getMessage());
    }

    @Test
    void validateCredentials_Success() {
        // Given
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // When
        boolean result = authService.validateCredentials("testuser", "password");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCredentials_WrongPassword() {
        // Given
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When
        boolean result = authService.validateCredentials("testuser", "wrongpassword");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCredentials_UserNotFound() {
        // Given
        when(userMapper.findByUsernameWithRoles("nonexistent")).thenReturn(null);

        // When
        boolean result = authService.validateCredentials("nonexistent", "password");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCredentials_UserDisabled() {
        // Given
        testUser.setStatus(0); // 禁用用户
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        boolean result = authService.validateCredentials("testuser", "password");

        // Then
        assertFalse(result);
    }

    @Test
    void isUserActiveByUsername_Active() {
        // Given
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        boolean result = authService.isUserActiveByUsername("testuser");

        // Then
        assertTrue(result);
    }

    @Test
    void isUserActiveByUsername_Inactive() {
        // Given
        testUser.setStatus(0); // 禁用用户
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        boolean result = authService.isUserActiveByUsername("testuser");

        // Then
        assertFalse(result);
    }

    @Test
    void isUserActiveById_Active() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        boolean result = authService.isUserActiveById(1L);

        // Then
        assertTrue(result);
    }

    @Test
    void isEmailAvailable_Available() {
        // Given
        when(userMapper.countByEmail("new@example.com")).thenReturn(0);

        // When
        boolean result = authService.isEmailAvailable("new@example.com");

        // Then
        assertTrue(result);
    }

    @Test
    void isEmailAvailable_NotAvailable() {
        // Given
        when(userMapper.countByEmail("existing@example.com")).thenReturn(1);

        // When
        boolean result = authService.isEmailAvailable("existing@example.com");

        // Then
        assertFalse(result);
    }
}