package com.example.usermanagement.controller;

import com.example.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 简单控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SimpleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser")
    void testCheckUsername() throws Exception {
        // Given
        when(userService.isUsernameAvailable("available", null)).thenReturn(true);
        when(userService.isUsernameAvailable("taken", null)).thenReturn(false);

        // When & Then - 测试用户名可用
        mockMvc.perform(get("/users/check-username")
                .param("username", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        // When & Then - 测试用户名不可用
        mockMvc.perform(get("/users/check-username")
                .param("username", "taken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
    }
}