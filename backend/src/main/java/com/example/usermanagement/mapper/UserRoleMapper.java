package com.example.usermanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usermanagement.entity.UserRole;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联数据访问层
 */
@Repository
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID删除所有角色关联
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除所有用户关联
     */
    @Delete("DELETE FROM user_roles WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除特定的用户角色关联
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 检查用户角色关联是否存在
     */
    @Select("SELECT COUNT(*) FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    int countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据用户ID查找所有角色关联
     */
    @Select("SELECT * FROM user_roles WHERE user_id = #{userId}")
    List<UserRole> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查找所有用户关联
     */
    @Select("SELECT * FROM user_roles WHERE role_id = #{roleId}")
    List<UserRole> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关联
     */
    @Insert("<script>" +
            "INSERT INTO user_roles (user_id, role_id, created_at) VALUES " +
            "<foreach collection='userRoles' item='userRole' separator=','>" +
            "(#{userRole.userId}, #{userRole.roleId}, #{userRole.createdAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("userRoles") List<UserRole> userRoles);

    /**
     * 为用户分配默认角色
     */
    @Insert("INSERT INTO user_roles (user_id, role_id, created_at) " +
            "SELECT #{userId}, r.id, NOW() FROM roles r WHERE r.name = 'USER'")
    int assignDefaultRole(@Param("userId") Long userId);

    /**
     * 获取用户的角色ID列表
     */
    @Select("SELECT role_id FROM user_roles WHERE user_id = #{userId}")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 获取拥有特定角色的用户ID列表
     */
    @Select("SELECT user_id FROM user_roles WHERE role_id = #{roleId}")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);
}