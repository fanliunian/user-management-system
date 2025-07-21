package com.example.usermanagement.exception;

import com.example.usermanagement.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单异常处理器测试
 */
public class SimpleExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleBusinessException() {
        // 创建业务异常
        BusinessException exception = new BusinessException("TEST_CODE", "测试错误消息");
        
        // 处理异常
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleBusinessException(exception);
        
        // 验证响应
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("测试错误消息", response.getBody().getMessage());
        assertEquals("TEST_CODE", response.getBody().getError());
    }
    
    @Test
    void testHandleIllegalArgumentException() {
        // 创建非法参数异常
        IllegalArgumentException exception = new IllegalArgumentException("非法参数");
        
        // 处理异常
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleIllegalArgumentException(exception);
        
        // 验证响应
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("非法参数", response.getBody().getMessage());
        assertEquals("ILLEGAL_ARGUMENT", response.getBody().getError());
    }
    
    @Test
    void testHandleNullPointerException() {
        // 创建空指针异常
        NullPointerException exception = new NullPointerException("空指针");
        
        // 处理异常
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleNullPointerException(exception);
        
        // 验证响应
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("系统内部错误", response.getBody().getMessage());
        assertEquals("NULL_POINTER", response.getBody().getError());
    }
    
    @Test
    void testHandleGenericException() {
        // 创建通用异常
        Exception exception = new Exception("通用异常");
        
        // 处理异常
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleException(exception);
        
        // 验证响应
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("系统内部错误", response.getBody().getMessage());
        assertEquals("INTERNAL_ERROR", response.getBody().getError());
    }
}