import request from '@/utils/request'

/**
 * 获取所有角色列表
 */
export function getRoleList() {
  return request({
    url: '/roles',
    method: 'get'
  })
}

/**
 * 根据ID获取角色详情
 */
export function getRoleById(id) {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

/**
 * 创建角色
 */
export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

/**
 * 更新角色
 */
export function updateRole(id, data) {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除角色
 */
export function batchDeleteRoles(data) {
  return request({
    url: '/roles/batch',
    method: 'delete',
    data
  })
}

/**
 * 为用户分配角色
 */
export function assignRolesToUser(userId, data) {
  return request({
    url: `/roles/assign/${userId}`,
    method: 'put',
    data
  })
}

/**
 * 为用户添加角色
 */
export function addRoleToUser(userId, roleId) {
  return request({
    url: `/roles/add/${userId}/${roleId}`,
    method: 'post'
  })
}

/**
 * 移除用户角色
 */
export function removeRoleFromUser(userId, roleId) {
  return request({
    url: `/roles/remove/${userId}/${roleId}`,
    method: 'delete'
  })
}

/**
 * 获取用户的角色列表
 */
export function getUserRoles(userId) {
  return request({
    url: `/roles/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取拥有特定角色的用户ID列表
 */
export function getUserIdsByRole(roleId) {
  return request({
    url: `/roles/${roleId}/users`,
    method: 'get'
  })
}

/**
 * 检查角色是否可以删除
 */
export function canDeleteRole(roleId) {
  return request({
    url: `/roles/${roleId}/can-delete`,
    method: 'get'
  })
}

/**
 * 获取角色使用统计
 */
export function getRoleUsage(roleId) {
  return request({
    url: `/roles/${roleId}/usage`,
    method: 'get'
  })
}

/**
 * 检查用户是否有特定角色
 */
export function checkUserRole(userId, roleName) {
  return request({
    url: `/roles/check/${userId}/${roleName}`,
    method: 'get'
  })
}