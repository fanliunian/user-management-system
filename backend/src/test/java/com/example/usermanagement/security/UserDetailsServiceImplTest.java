package com.example.usermanagement.security;

import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;
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
    }

    @Test
    void loadUserByUsername_Success() {
        // Given
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertEquals(1, userDetails.getAuthorities().size());
        
        verify(userMapper).findByUsernameWithRoles("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Given
        when(userMapper.findByUsernameWithRoles("nonexistent")).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername("nonexistent"));
        
        assertEquals("用户不存在: nonexistent", exception.getMessage());
        verify(userMapper).findByUsernameWithRoles("nonexistent");
    }

    @Test
    void loadUserByUsername_DisabledUser() {
        // Given
        testUser.setStatus(0); // 禁用用户
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled()); // 应该是禁用状态
        verify(userMapper).findByUsernameWithRoles("testuser");
    }

    @Test
    void loadUserByUsername_UserWithMultipleRoles() {
        // Given
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");
        adminRole.setDescription("管理员");

        testUser.setRoles(Arrays.asList(testRole, adminRole));
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        verify(userMapper).findByUsernameWithRoles("testuser");
    }

    @Test
    void loadUserByUsername_UserWithNoRoles() {
        // Given
        testUser.setRoles(Arrays.asList()); // 空角色列表
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals(0, userDetails.getAuthorities().size());
        verify(userMapper).findByUsernameWithRoles("testuser");
    }

    @Test
    void loadUserByUsername_UserWithNullRoles() {
        // Given
        testUser.setRoles(null); // null角色列表
        when(userMapper.findByUsernameWithRoles("testuser")).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals(0, userDetails.getAuthorities().size());
        verify(userMapper).findByUsernameWithRoles("testuser");
    }

    @Test
    void loadUserById_Success() {
        // Given
        when(userMapper.findByIdWithRoles(1L)).thenReturn(testUser);

        // When
        UserDetails userDetails = userDetailsService.loadUserById(1L);

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        
        verify(userMapper).findByIdWithRoles(1L);
    }

    @Test
    void loadUserById_UserNotFound() {
        // Given
        when(userMapper.findByIdWithRoles(999L)).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserById(999L));
        
        assertEquals("用户不存在: 999", exception.getMessage());
        verify(userMapper).findByIdWithRoles(999L);
    }

    @Test
    void loadUserByUsername_EmptyUsername() {
        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(""));
        
        assertEquals("用户不存在: ", exception.getMessage());
        verify(userMapper).findByUsernameWithRoles("");
    }

    @Test
    void loadUserByUsername_NullUsername() {
        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(null));
        
        assertEquals("用户不存在: null", exception.getMessage());
        verify(userMapper).findByUsernameWithRoles(null);
    }
}