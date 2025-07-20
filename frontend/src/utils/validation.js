/**
 * 表单验证规则
 */

// 用户名验证
export const usernameRules = [
  { required: true, message: '请输入用户名', trigger: 'blur' },
  { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' },
  { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
]

// 邮箱验证
export const emailRules = [
  { required: true, message: '请输入邮箱地址', trigger: 'blur' },
  { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
]

// 密码验证
export const passwordRules = [
  { required: true, message: '请输入密码', trigger: 'blur' },
  { min: 8, message: '密码长度至少8位', trigger: 'blur' },
  { max: 50, message: '密码长度不能超过50位', trigger: 'blur' }
]

// 确认密码验证
export const confirmPasswordRules = (password) => [
  { required: true, message: '请确认密码', trigger: 'blur' },
  {
    validator: (rule, value, callback) => {
      if (value !== password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }
]

// 角色名称验证
export const roleNameRules = [
  { required: true, message: '请输入角色名称', trigger: 'blur' },
  { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' },
  { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '角色名称只能包含字母、数字、下划线和中文', trigger: 'blur' }
]

// 角色描述验证
export const roleDescriptionRules = [
  { max: 200, message: '角色描述长度不能超过200个字符', trigger: 'blur' }
]

/**
 * 验证工具函数
 */

// 验证用户名格式
export function validateUsername(username) {
  const pattern = /^[a-zA-Z0-9_]+$/
  return pattern.test(username) && username.length >= 3 && username.length <= 50
}

// 验证邮箱格式
export function validateEmail(email) {
  const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return pattern.test(email) && email.length <= 100
}

// 验证密码强度
export function validatePassword(password) {
  if (password.length < 8) {
    return { valid: false, message: '密码长度至少8位' }
  }
  
  if (password.length > 50) {
    return { valid: false, message: '密码长度不能超过50位' }
  }
  
  // 检查是否包含字母和数字
  const hasLetter = /[a-zA-Z]/.test(password)
  const hasNumber = /\d/.test(password)
  
  if (!hasLetter || !hasNumber) {
    return { valid: false, message: '密码应包含字母和数字' }
  }
  
  return { valid: true, message: '密码强度良好' }
}

// 验证手机号格式
export function validatePhone(phone) {
  const pattern = /^1[3-9]\d{9}$/
  return pattern.test(phone)
}

// 验证身份证号格式
export function validateIdCard(idCard) {
  const pattern = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return pattern.test(idCard)
}

// 通用非空验证
export function validateRequired(value, fieldName = '此字段') {
  if (value === null || value === undefined || value === '') {
    return { valid: false, message: `${fieldName}不能为空` }
  }
  return { valid: true }
}

// 长度验证
export function validateLength(value, min = 0, max = Infinity, fieldName = '此字段') {
  if (typeof value !== 'string') {
    value = String(value)
  }
  
  if (value.length < min) {
    return { valid: false, message: `${fieldName}长度不能少于${min}个字符` }
  }
  
  if (value.length > max) {
    return { valid: false, message: `${fieldName}长度不能超过${max}个字符` }
  }
  
  return { valid: true }
}