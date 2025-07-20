import Cookies from 'js-cookie'

const TokenKey = 'user_token'

/**
 * 获取token
 */
export function getToken() {
  return Cookies.get(TokenKey)
}

/**
 * 设置token
 */
export function setToken(token) {
  return Cookies.set(TokenKey, token, { expires: 7 }) // 7天过期
}

/**
 * 移除token
 */
export function removeToken() {
  return Cookies.remove(TokenKey)
}

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
  return !!getToken()
}

/**
 * 检查用户是否有指定角色
 */
export function hasRole(userRoles, targetRole) {
  if (!userRoles || !Array.isArray(userRoles)) {
    return false
  }
  return userRoles.some(role => role.name === targetRole)
}

/**
 * 检查用户是否是管理员
 */
export function isAdmin(userRoles) {
  return hasRole(userRoles, 'ADMIN')
}

/**
 * 格式化用户角色显示
 */
export function formatRoles(roles) {
  if (!roles || !Array.isArray(roles)) {
    return '无角色'
  }
  return roles.map(role => role.name).join(', ')
}

/**
 * 获取用户状态文本
 */
export function getUserStatusText(status) {
  return status === 1 ? '启用' : '禁用'
}

/**
 * 获取用户状态标签类型
 */
export function getUserStatusType(status) {
  return status === 1 ? 'success' : 'danger'
}