package com.example.usermanagement;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 测试工具类
 * 提供常用的测试数据和工具方法
 */
public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建测试用户
     */
    public static User createTestUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        Role role = createTestRole(1L, "USER");
        user.setRoles(Arrays.asList(role));
        
        return user;
    }

    /**
     * 创建测试角色
     */
    public static Role createTestRole(Long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setDescription("测试角色");
        return role;
    }

    /**
     * 创建测试用户主体
     */
    public static UserPrincipal createTestUserPrincipal(Long id, String username) {
        User user = createTestUser(id, username, username + "@example.com");
        return UserPrincipal.create(user);
    }

    /**
     * 将对象转换为JSON字符串
     */
    public static String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("转换JSON失败", e);
        }
    }

    /**
     * 打印调试信息
     */
    public static void debug(String message, Object... args) {
        System.out.printf("[DEBUG] " + message + "%n", args);
    }

    /**
     * 打印测试分隔线
     */
    public static void printSeparator(String testName) {
        String separator = repeatString("=", 50);
        System.out.println(separator);
        System.out.println("测试: " + testName);
        System.out.println(separator);
    }

    /**
     * 重复字符串（Java 8兼容）
     */
    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}