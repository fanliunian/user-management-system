package com.example.usermanagement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单测试类，用于验证测试环境
 */
public class SimpleTest {

    @Test
    void simpleTest() {
        // 简单的断言测试
        assertTrue(true);
        assertEquals(2, 1 + 1);
        assertNotNull("Hello");
    }
    
    @Test
    void stringTest() {
        String message = "Hello, World!";
        assertEquals("Hello, World!", message);
        assertTrue(message.startsWith("Hello"));
        assertFalse(message.isEmpty());
    }
}