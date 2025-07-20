package com.example.usermanagement.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * 更新用户信息请求DTO
 */
public class UpdateUserRequest {
    
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;
    
    @Email(message = "邮箱格式无效")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    // 构造函数
    public UpdateUserRequest() {}

    public UpdateUserRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getter和Setter方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}