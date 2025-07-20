package com.example.usermanagement.exception;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    
    // 用户相关错误
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),
    USERNAME_EXISTS("USERNAME_EXISTS", "用户名已存在"),
    EMAIL_EXISTS("EMAIL_EXISTS", "邮箱已被注册"),
    INVALID_PASSWORD("INVALID_PASSWORD", "密码长度至少8位"),
    INVALID_EMAIL("INVALID_EMAIL", "邮箱格式无效"),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "当前密码错误"),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "账户已被禁用"),
    CANNOT_DISABLE_SELF("CANNOT_DISABLE_SELF", "不能禁用自己的账户"),
    CANNOT_DELETE_SELF("CANNOT_DELETE_SELF", "不能删除自己的账户"),
    
    // 认证相关错误
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "用户名或密码错误"),
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED", "认证失败"),
    INVALID_TOKEN("INVALID_TOKEN", "令牌无效"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "令牌已过期"),
    ACCESS_DENIED("ACCESS_DENIED", "权限不足"),
    
    // 角色相关错误
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "角色不存在"),
    ROLE_NAME_EXISTS("ROLE_NAME_EXISTS", "角色名称已存在"),
    CANNOT_DELETE_ROLE_IN_USE("CANNOT_DELETE_ROLE_IN_USE", "角色正在使用中，无法删除"),
    ROLE_ALREADY_ASSIGNED("ROLE_ALREADY_ASSIGNED", "用户已拥有该角色"),
    ROLE_NOT_ASSIGNED("ROLE_NOT_ASSIGNED", "用户没有该角色"),
    DEFAULT_ROLE_NOT_FOUND("DEFAULT_ROLE_NOT_FOUND", "默认用户角色不存在"),
    ADMIN_ROLE_NOT_FOUND("ADMIN_ROLE_NOT_FOUND", "管理员角色不存在"),
    
    // 验证相关错误
    VALIDATION_ERROR("VALIDATION_ERROR", "参数验证失败"),
    BIND_ERROR("BIND_ERROR", "参数绑定失败"),
    CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "约束验证失败"),
    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "非法参数"),
    
    // 系统相关错误
    INTERNAL_ERROR("INTERNAL_ERROR", "系统内部错误"),
    RUNTIME_ERROR("RUNTIME_ERROR", "系统运行时错误"),
    NULL_POINTER("NULL_POINTER", "空指针异常"),
    DATABASE_ERROR("DATABASE_ERROR", "数据库操作错误"),
    
    // 业务相关错误
    OPERATION_FAILED("OPERATION_FAILED", "操作失败"),
    DATA_NOT_FOUND("DATA_NOT_FOUND", "数据不存在"),
    DUPLICATE_DATA("DUPLICATE_DATA", "数据重复"),
    INVALID_OPERATION("INVALID_OPERATION", "无效操作");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}