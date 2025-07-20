import request from '@/utils/request'

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取用户详情
 */
export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

/**
 * 更新用户状态
 */
export function updateUserStatus(id, data) {
  return request({
    url: `/users/${id}/status`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

/**
 * 批量更新用户状态
 */
export function batchUpdateUserStatus(data) {
  return request({
    url: '/users/batch/status',
    method: 'put',
    data
  })
}

/**
 * 获取用户统计信息
 */
export function getUserStatistics() {
  return request({
    url: '/users/statistics',
    method: 'get'
  })
}