package com.example.usermanagement.security;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT令牌提供者简单测试
 */
public class SimpleJwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User testUser;
    private UserPrincipal userPrincipal;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // 创建JwtTokenProvider实例
        jwtTokenProvider = new JwtTokenProvider();
        
        // 设置JWT配置
        setPrivateField(jwtTokenProvider, "jwtSecret", "mySecretKeyForJWTTokenGenerationAndValidationThatIsLongEnough");
        setPrivateField(jwtTokenProvider, "jwtExpiration", 3600000L); // 1小时
        setPrivateField(jwtTokenProvider, "refreshExpiration", 86400000L); // 24小时
        
        // 初始化JWT密钥
        jwtTokenProvider.init();

        // 创建测试用户
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
        userPrincipal = UserPrincipal.create(testUser);
        authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    @Test
    void testTokenGeneration() {
        // 生成令牌
        String token = jwtTokenProvider.generateToken(authentication);
        
        // 验证令牌不为空
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // 验证令牌有效
        assertTrue(jwtTokenProvider.validateToken(token));
    }
    
    @Test
    void testRefreshTokenGeneration() {
        // 生成刷新令牌
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        // 验证刷新令牌不为空
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        
        // 验证刷新令牌有效
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
    }
    
    @Test
    void testInvalidToken() {
        // 无效令牌
        String invalidToken = "invalid.token.here";
        
        // 验证令牌无效
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
    
    @Test
    void testNullToken() {
        // 验证null令牌无效
        assertFalse(jwtTokenProvider.validateToken(null));
    }
    
    @Test
    void testEmptyToken() {
        // 验证空令牌无效
        assertFalse(jwtTokenProvider.validateToken(""));
    }
    
    /**
     * 通过反射设置私有字段的值
     */
    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("设置私有字段失败", e);
        }
    }
}