package com.example.usermanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.usermanagement.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 用户数据访问层
 */
@Repository
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户（包含角色信息）
     */
    @Select("SELECT u.*, r.id as role_id, r.name as role_name, r.description as role_description " +
            "FROM users u " +
            "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
            "LEFT JOIN roles r ON ur.role_id = r.id " +
            "WHERE u.username = #{username}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "lastLoginAt", column = "last_login_at"),
        @Result(property = "roles", column = "id", 
                many = @Many(select = "com.example.usermanagement.mapper.RoleMapper.findByUserId"))
    })
    User findByUsernameWithRoles(@Param("username") String username);

    /**
     * 根据ID查找用户（包含角色信息）
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "lastLoginAt", column = "last_login_at"),
        @Result(property = "roles", column = "id", 
                many = @Many(select = "com.example.usermanagement.mapper.RoleMapper.findByUserId"))
    })
    User findByIdWithRoles(@Param("id") Long id);

    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    /**
     * 检查邮箱是否被其他用户使用
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email} AND id != #{userId}")
    int countByEmailExcludingUser(@Param("email") String email, @Param("userId") Long userId);

    /**
     * 更新用户最后登录时间
     */
    @Update("UPDATE users SET last_login_at = #{lastLoginAt}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateLastLoginTime(@Param("id") Long id, 
                           @Param("lastLoginAt") LocalDateTime lastLoginAt,
                           @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新用户状态
     */
    @Update("UPDATE users SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新用户密码
     */
    @Update("UPDATE users SET password = #{password}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 分页查询用户列表（支持搜索和筛选）
     */
    @Select("<script>" +
            "SELECT u.*, " +
            "STRING_AGG(r.name, ',') as role_names " +
            "FROM users u " +
            "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
            "LEFT JOIN roles r ON ur.role_id = r.id " +
            "WHERE 1=1 " +
            "<if test='search != null and search != \"\"'>" +
            "AND (u.username LIKE CONCAT('%', #{search}, '%') OR u.email LIKE CONCAT('%', #{search}, '%')) " +
            "</if>" +
            "<if test='status != null'>" +
            "AND u.status = #{status} " +
            "</if>" +
            "<if test='roleId != null'>" +
            "AND ur.role_id = #{roleId} " +
            "</if>" +
            "GROUP BY u.id " +
            "ORDER BY u.created_at DESC" +
            "</script>")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "lastLoginAt", column = "last_login_at"),
        @Result(property = "roles", column = "id", 
                many = @Many(select = "com.example.usermanagement.mapper.RoleMapper.findByUserId"))
    })
    IPage<User> findUsersWithPagination(Page<User> page, 
                                       @Param("search") String search,
                                       @Param("status") Integer status,
                                       @Param("roleId") Long roleId);

    /**
     * 获取用户总数
     */
    @Select("SELECT COUNT(*) FROM users")
    long getTotalCount();

    /**
     * 获取启用用户数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = 1")
    long getEnabledUserCount();

    /**
     * 获取禁用用户数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = 0")
    long getDisabledUserCount();
}