package com.example.usermanagement.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单API响应测试
 */
public class SimpleApiResponseTest {

    @Test
    void testSuccessWithData() {
        // Given
        String data = "测试数据";

        // When
        ApiResponse<String> response = ApiResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("操作成功", response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getError());
    }

    @Test
    void testSuccessWithMessageAndData() {
        // Given
        String message = "成功消息";
        String data = "测试数据";

        // When
        ApiResponse<String> response = ApiResponse.success(message, data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getError());
    }

    @Test
    void testSuccessWithMessageOnly() {
        // Given
        String message = "成功消息";

        // When
        ApiResponse<String> response = ApiResponse.success(message);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNull(response.getError());
    }

    @Test
    void testErrorWithMessage() {
        // Given
        String message = "错误消息";

        // When
        ApiResponse<String> response = ApiResponse.error(message);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNull(response.getError());
    }

    @Test
    void testErrorWithMessageAndErrorCode() {
        // Given
        String message = "错误消息";
        String errorCode = "ERROR_CODE";

        // When
        ApiResponse<String> response = ApiResponse.error(message, errorCode);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertEquals(errorCode, response.getError());
    }

    @Test
    void testGetterAndSetter() {
        // Given
        ApiResponse<String> response = new ApiResponse<>();

        // When
        response.setSuccess(true);
        response.setMessage("测试消息");
        response.setData("测试数据");
        response.setError("测试错误");

        // Then
        assertTrue(response.isSuccess());
        assertEquals("测试消息", response.getMessage());
        assertEquals("测试数据", response.getData());
        assertEquals("测试错误", response.getError());
    }

    @Test
    void testConstructor() {
        // Given & When
        ApiResponse<String> response = new ApiResponse<>(true, "测试消息", "测试数据", "测试错误");

        // Then
        assertTrue(response.isSuccess());
        assertEquals("测试消息", response.getMessage());
        assertEquals("测试数据", response.getData());
        assertEquals("测试错误", response.getError());
    }
}