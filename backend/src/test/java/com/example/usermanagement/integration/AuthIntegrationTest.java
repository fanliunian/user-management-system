package com.example.usermanagement.integration;

import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerAndLogin_Success() throws Exception {
        // 1. 注册新用户
        RegisterRequest registerRequest = new RegisterRequest("testuser", "test@example.com", "password123");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        // 2. 使用注册的用户登录
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));
    }

    @Test
    void register_DuplicateUsername() throws Exception {
        // 1. 注册第一个用户
        RegisterRequest firstUser = new RegisterRequest("duplicateuser", "first@example.com", "password123");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isOk());

        // 2. 尝试注册相同用户名的用户
        RegisterRequest secondUser = new RegisterRequest("duplicateuser", "second@example.com", "password456");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void register_DuplicateEmail() throws Exception {
        // 1. 注册第一个用户
        RegisterRequest firstUser = new RegisterRequest("firstuser", "duplicate@example.com", "password123");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isOk());

        // 2. 尝试注册相同邮箱的用户
        RegisterRequest secondUser = new RegisterRequest("seconduser", "duplicate@example.com", "password456");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("邮箱已被注册"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        // 1. 先注册用户
        RegisterRequest registerRequest = new RegisterRequest("testuser", "test@example.com", "password123");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. 使用错误密码登录
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void login_NonexistentUser() throws Exception {
        // 尝试登录不存在的用户
        LoginRequest loginRequest = new LoginRequest("nonexistent", "password123");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void register_ValidationErrors() throws Exception {
        // 测试各种验证错误
        RegisterRequest invalidRequest = new RegisterRequest("ab", "invalid-email", "123");
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("参数验证失败"));
    }

    @Test
    void login_ValidationErrors() throws Exception {
        // 测试空用户名和密码
        LoginRequest invalidRequest = new LoginRequest("", "");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("参数验证失败"));
    }

    @Test
    void checkUsername_Available() throws Exception {
        mockMvc.perform(get("/auth/check-username")
                .param("username", "availableuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void checkUsername_NotAvailable() throws Exception {
        // 1. 先注册用户
        RegisterRequest registerRequest = new RegisterRequest("existinguser", "existing@example.com", "password123");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. 检查用户名是否可用
        mockMvc.perform(get("/auth/check-username")
                .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    void checkEmail_Available() throws Exception {
        mockMvc.perform(get("/auth/check-email")
                .param("email", "available@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void checkEmail_NotAvailable() throws Exception {
        // 1. 先注册用户
        RegisterRequest registerRequest = new RegisterRequest("testuser", "existing@example.com", "password123");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. 检查邮箱是否可用
        mockMvc.perform(get("/auth/check-email")
                .param("email", "existing@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    void logout_Success() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    void validateToken_WithValidToken() throws Exception {
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("令牌有效"));
    }

    @Test
    void validateToken_WithInvalidHeader() throws Exception {
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Invalid-Header"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无效的认证头"));
    }

    @Test
    void validateToken_WithoutHeader() throws Exception {
        mockMvc.perform(post("/auth/validate"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无效的认证头"));
    }
}