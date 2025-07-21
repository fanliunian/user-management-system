package com.example.usermanagement.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void success_WithData() {
        // Given
        String data = "test data";

        // When
        ApiResponse<String> response = ApiResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("操作成功", response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getError());
    }

    @Test
    void success_WithMessageAndData() {
        // Given
        String message = "自定义成功消息";
        String data = "test data";

        // When
        ApiResponse<String> response = ApiResponse.success(message, data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getError());
    }

    @Test
    void success_WithMessageOnly() {
        // Given
        String message = "操作完成";

        // When
        ApiResponse<String> response = ApiResponse.success(message);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNull(response.getError());
    }

    @Test
    void error_WithMessage() {
        // Given
        String errorMessage = "操作失败";

        // When
        ApiResponse<String> response = ApiResponse.error(errorMessage);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void error_WithMessageAndError() {
        // Given
        String message = "请求失败";
        String error = "详细错误信息";

        // When
        ApiResponse<String> response = ApiResponse.error(message, error);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertEquals(error, response.getError());
    }

    @Test
    void constructor_AllFields() {
        // Given
        boolean success = true;
        String message = "测试消息";
        String data = "测试数据";
        String error = "测试错误";

        // When
        ApiResponse<String> response = new ApiResponse<>(success, message, data, error);

        // Then
        assertEquals(success, response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(error, response.getError());
    }

    @Test
    void settersAndGetters() {
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
    void success_NullData() {
        // When
        ApiResponse<String> response = ApiResponse.success(null);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("操作成功", response.getMessage());
        assertNull(response.getData());
        assertNull(response.getError());
    }

    @Test
    void error_NullMessage() {
        // When
        ApiResponse<String> response = ApiResponse.error(null);

        // Then
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
        assertNull(response.getError());
    }

    @Test
    void genericType_Integer() {
        // Given
        Integer data = 123;

        // When
        ApiResponse<Integer> response = ApiResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(data, response.getData());
        assertEquals(Integer.class, response.getData().getClass());
    }

    @Test
    void genericType_Object() {
        // Given
        Object data = new Object();

        // When
        ApiResponse<Object> response = ApiResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(data, response.getData());
    }
}