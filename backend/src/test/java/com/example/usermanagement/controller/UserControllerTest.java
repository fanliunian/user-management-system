package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.security.UserPrincipal;
import com.example.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setEmail("test@example.com");
        userResponse.setStatus(1);

        userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn(1L);
    }

    @Test
    @WithMockUser
    void getCurrentUserProfile_Success() throws Exception {
        // Given
        when(userService.getUserProfile(1L)).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/users/profile")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService).getUserProfile(1L);
    }

    @Test
    @WithMockUser
    void updateCurrentUserProfile_Success() throws Exception {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newusername");
        request.setEmail("newemail@example.com");

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequest.class)))
            .thenReturn(userResponse);

        // When & Then
        mockMvc.perform(put("/users/profile")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("个人信息更新成功"));

        verify(userService).updateUserProfile(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    @WithMockUser
    void changePassword_Success() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");

        doNothing().when(userService).changePassword(eq(1L), any(ChangePasswordRequest.class));

        // When & Then
        mockMvc.perform(put("/users/password")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        verify(userService).changePassword(eq(1L), any(ChangePasswordRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserList_Success() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(userResponse);
        PageResponse<UserResponse> pageResponse = PageResponse.of(users, 1L, 0, 10);
        
        when(userService.getUserList(0, 10, null, null, null))
            .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"));

        verify(userService).getUserList(0, 10, null, null, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_Success() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setStatus(1);
        request.setRoles(Arrays.asList(1L));

        when(userService.createUser(any(CreateUserRequest.class), eq(1L)))
            .thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/users")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户创建成功"));

        verify(userService).createUser(any(CreateUserRequest.class), eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_Success() throws Exception {
        // Given
        Map<String, Integer> request = new HashMap<>();
        request.put("status", 0);

        doNothing().when(userService).updateUserStatus(1L, 0, 1L);

        // When & Then
        mockMvc.perform(put("/users/1/status")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户已禁用"));

        verify(userService).updateUserStatus(1L, 0, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resetUserPassword_Success() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("password", "newPassword123");

        doNothing().when(userService).resetUserPassword(1L, "newPassword123", 1L);

        // When & Then
        mockMvc.perform(put("/users/1/reset-password")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("密码重置成功"));

        verify(userService).resetUserPassword(1L, "newPassword123", 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L, 1L);

        // When & Then
        mockMvc.perform(delete("/users/1")
                .with(user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userService).deleteUser(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void batchUpdateUserStatus_Success() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("userIds", Arrays.asList(1L, 2L));
        request.put("status", 0);

        doNothing().when(userService).batchUpdateUserStatus(anyList(), eq(0), eq(1L));

        // When & Then
        mockMvc.perform(put("/users/batch/status")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户批量禁用成功"));

        verify(userService).batchUpdateUserStatus(anyList(), eq(0), eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserStatistics_Success() throws Exception {
        // Given
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", 100L);
        statistics.put("enabledUsers", 80L);
        statistics.put("disabledUsers", 20L);
        statistics.put("enabledPercentage", 80.0);

        when(userService.getUserStatistics()).thenReturn(statistics);

        // When & Then
        mockMvc.perform(get("/users/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").value(100))
                .andExpect(jsonPath("$.data.enabledUsers").value(80));

        verify(userService).getUserStatistics();
    }

    @Test
    @WithMockUser
    void checkUsername_Available() throws Exception {
        // Given
        when(userService.isUsernameAvailable("newuser", null)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/users/check-username")
                .param("username", "newuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).isUsernameAvailable("newuser", null);
    }

    @Test
    @WithMockUser
    void checkEmail_Available() throws Exception {
        // Given
        when(userService.isEmailAvailable("new@example.com", null)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/users/check-email")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).isEmailAvailable("new@example.com", null);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserList_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());

        verify(userService, never()).getUserList(anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_Forbidden() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");

        // When & Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(userService, never()).createUser(any(), any());
    }
}