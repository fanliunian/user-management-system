package com.example.usermanagement.controller;

import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.RegisterRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setStatus(1);
    }

    @Test
    void login_Success() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        
        Map<String, Object> loginResult = new HashMap<>();
        loginResult.put("accessToken", "access-token");
        loginResult.put("refreshToken", "refresh-token");
        loginResult.put("tokenType", "Bearer");
        loginResult.put("user", userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResult);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("用户名或密码错误"));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_ValidationError() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("", ""); // 空用户名和密码

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    void register_Success() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("newuser", "new@example.com", "password123");
        
        when(authService.register(any(RegisterRequest.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_UsernameExists() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("existinguser", "new@example.com", "password123");
        
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("用户名已存在"));

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_ValidationError() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("ab", "invalid-email", "123"); // 无效数据

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void refreshToken_Success() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "valid-refresh-token");
        
        Map<String, Object> refreshResult = new HashMap<>();
        refreshResult.put("accessToken", "new-access-token");
        refreshResult.put("refreshToken", "new-refresh-token");
        refreshResult.put("tokenType", "Bearer");

        when(authService.refreshToken("valid-refresh-token")).thenReturn(refreshResult);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("令牌刷新成功"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));

        verify(authService).refreshToken("valid-refresh-token");
    }

    @Test
    void refreshToken_InvalidToken() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "invalid-refresh-token");
        
        when(authService.refreshToken("invalid-refresh-token"))
            .thenThrow(new RuntimeException("刷新令牌无效"));

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("刷新令牌无效"));

        verify(authService).refreshToken("invalid-refresh-token");
    }

    @Test
    void refreshToken_EmptyToken() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "");

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("刷新令牌不能为空"));

        verify(authService, never()).refreshToken(anyString());
    }

    @Test
    void logout_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    void validateToken_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("令牌有效"));
    }

    @Test
    void validateToken_InvalidHeader() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Invalid-Header"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无效的认证头"));
    }

    @Test
    void validateToken_NoHeader() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/validate"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无效的认证头"));
    }

    @Test
    void checkUsername_Available() throws Exception {
        // Given
        when(authService.isUserActiveByUsername("newuser")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/auth/check-username")
                .param("username", "newuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true)); // 不存在则可用

        verify(authService).isUserActiveByUsername("newuser");
    }

    @Test
    void checkUsername_NotAvailable() throws Exception {
        // Given
        when(authService.isUserActiveByUsername("existinguser")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/auth/check-username")
                .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false)); // 存在则不可用

        verify(authService).isUserActiveByUsername("existinguser");
    }

    @Test
    void checkEmail_Available() throws Exception {
        // Given
        when(authService.isEmailAvailable("new@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/auth/check-email")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        verify(authService).isEmailAvailable("new@example.com");
    }

    @Test
    void checkEmail_NotAvailable() throws Exception {
        // Given
        when(authService.isEmailAvailable("existing@example.com")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/auth/check-email")
                .param("email", "existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));

        verify(authService).isEmailAvailable("existing@example.com");
    }
}