package com.example.usermanagement.exception;

import com.example.usermanagement.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        // 初始化设置
    }

    @Test
    void handleBusinessException() {
        // Given
        BusinessException exception = new BusinessException("USER_NOT_FOUND", "用户不存在");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleBusinessException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("用户不存在", response.getBody().getMessage());
        assertEquals("USER_NOT_FOUND", response.getBody().getError());
    }

    @Test
    void handleAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Access is denied");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleAccessDeniedException(exception);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("权限不足", response.getBody().getMessage());
        assertEquals("ACCESS_DENIED", response.getBody().getError());
    }

    @Test
    void handleBadCredentialsException() {
        // Given
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        // When
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleBadCredentialsException(exception);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("用户名或密码错误", response.getBody().getMessage());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getError());
    }

    @Test
    void handleGenericException() {
        // Given
        RuntimeException exception = new RuntimeException("Something went wrong");

        // When
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("系统内部错误", response.getBody().getMessage());
        assertEquals("INTERNAL_ERROR", response.getBody().getError());
    }

    @Test
    void handleBusinessException_NullMessage() {
        // Given
        BusinessException exception = new BusinessException("ERROR_CODE", null);

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleBusinessException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertNull(response.getBody().getMessage());
        assertEquals("ERROR_CODE", response.getBody().getError());
    }

    @Test
    void handleBusinessException_NullErrorCode() {
        // Given
        BusinessException exception = new BusinessException(null, "错误消息");

        // When
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleBusinessException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("错误消息", response.getBody().getMessage());
        assertNull(response.getBody().getError());
    }

    @Test
    void handleValidationException_EmptyFieldErrors() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleValidationException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数验证失败", response.getBody().getMessage());
        assertEquals("", response.getBody().getError());
    }

    @Test
    void handleValidationException_SingleFieldError() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError = new FieldError("user", "username", "用户名不能为空");
        List<FieldError> fieldErrors = Arrays.asList(fieldError);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // When
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleValidationException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数验证失败", response.getBody().getMessage());
        assertEquals("用户名不能为空; ", response.getBody().getError());
    }

    @Test
    void handleGenericException_NullPointerException() {
        // Given
        NullPointerException exception = new NullPointerException("Null pointer");

        // When
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("系统内部错误", response.getBody().getMessage());
        assertEquals("INTERNAL_ERROR", response.getBody().getError());
    }

    @Test
    void handleGenericException_IllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When
        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("系统内部错误", response.getBody().getMessage());
        assertEquals("INTERNAL_ERROR", response.getBody().getError());
    }
}