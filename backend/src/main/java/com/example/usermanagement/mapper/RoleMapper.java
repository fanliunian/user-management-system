package com.example.usermanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.usermanagement.entity.Role;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色数据访问层
 */
@Repository
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查找角色列表
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色名称查找角色
     */
    @Select("SELECT * FROM roles WHERE name = #{name}")
    Role findByName(@Param("name") String name);

    /**
     * 检查角色名称是否存在
     */
    @Select("SELECT COUNT(*) FROM roles WHERE name = #{name}")
    int countByName(@Param("name") String name);

    /**
     * 检查角色名称是否被其他角色使用
     */
    @Select("SELECT COUNT(*) FROM roles WHERE name = #{name} AND id != #{roleId}")
    int countByNameExcludingRole(@Param("name") String name, @Param("roleId") Long roleId);

    /**
     * 检查角色是否被用户使用
     */
    @Select("SELECT COUNT(*) FROM user_roles WHERE role_id = #{roleId}")
    int countUsersWithRole(@Param("roleId") Long roleId);

    /**
     * 获取所有角色列表
     */
    @Select("SELECT * FROM roles ORDER BY created_at ASC")
    List<Role> findAllRoles();

    /**
     * 根据角色ID列表查找角色
     */
    @Select("<script>" +
            "SELECT * FROM roles WHERE id IN " +
            "<foreach item='id' collection='roleIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Role> findByIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 获取默认用户角色
     */
    @Select("SELECT * FROM roles WHERE name = 'USER'")
    Role findDefaultUserRole();

    /**
     * 获取管理员角色
     */
    @Select("SELECT * FROM roles WHERE name = 'ADMIN'")
    Role findAdminRole();
}