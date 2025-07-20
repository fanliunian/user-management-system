import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 刷新token
 */
export function refreshToken(data) {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data
  })
}

/**
 * 验证token
 */
export function validateToken() {
  return request({
    url: '/auth/validate',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getUserProfile() {
  return request({
    url: '/users/profile',
    method: 'get'
  })
}

/**
 * 更新当前用户信息
 */
export function updateUserProfile(data) {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return request({
    url: '/users/password',
    method: 'put',
    data
  })
}

/**
 * 检查用户名是否可用
 */
export function checkUsername(username, excludeUserId = null) {
  return request({
    url: '/auth/check-username',
    method: 'get',
    params: {
      username,
      excludeUserId
    }
  })
}

/**
 * 检查邮箱是否可用
 */
export function checkEmail(email, excludeUserId = null) {
  return request({
    url: '/auth/check-email',
    method: 'get',
    params: {
      email,
      excludeUserId
    }
  })
}