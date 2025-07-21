package com.example.usermanagement.security;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private UserPrincipal userPrincipal;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // 设置JWT配置
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "mySecretKeyForJWTTokenGenerationAndValidationThatIsLongEnough");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 86400000); // 24小时
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationInMs", 604800000); // 7天

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
    void generateToken_Success() {
        // When
        String token = jwtTokenProvider.generateToken(authentication);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateRefreshToken_Success() {
        // When
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
    }

    @Test
    void getUserIdFromToken_Success() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // Then
        assertEquals(1L, userId);
    }

    @Test
    void getUsernameFromToken_Success() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_ValidToken() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken() {
        // Given
        // 创建一个已过期的token
        String jwtSecret = "mySecretKeyForJWTTokenGenerationAndValidationThatIsLongEnough";
        Date expiredDate = new Date(System.currentTimeMillis() - 1000); // 1秒前过期
        
        String expiredToken = Jwts.builder()
                .setSubject(Long.toString(testUser.getId()))
                .claim("username", testUser.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        // When
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_NullToken() {
        // When
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_EmptyToken() {
        // When
        boolean isValid = jwtTokenProvider.validateToken("");

        // Then
        assertFalse(isValid);
    }

    @Test
    void getExpirationDateFromToken_Success() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        // Then
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void getAllClaimsFromToken_Success() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        Claims claims = jwtTokenProvider.getAllClaimsFromToken(token);

        // Then
        assertNotNull(claims);
        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("username"));
    }

    @Test
    void tokenContainsCorrectClaims() {
        // Given
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        Claims claims = jwtTokenProvider.getAllClaimsFromToken(token);

        // Then
        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("username"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void refreshTokenHasLongerExpiration() {
        // Given
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // When
        Date accessTokenExpiration = jwtTokenProvider.getExpirationDateFromToken(accessToken);
        Date refreshTokenExpiration = jwtTokenProvider.getExpirationDateFromToken(refreshToken);

        // Then
        assertTrue(refreshTokenExpiration.after(accessTokenExpiration));
    }

    @Test
    void getUserIdFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtTokenProvider.getUserIdFromToken(invalidToken);
        });
    }

    @Test
    void getUsernameFromToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtTokenProvider.getUsernameFromToken(invalidToken);
        });
    }
}