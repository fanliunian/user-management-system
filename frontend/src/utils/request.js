import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8082/api',
  timeout: 15000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 设置Content-Type
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }
    
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    const res = response.data
    
    // 如果自定义代码不是200，则判断为错误
    if (res.success === false) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 特定错误码处理
      if (res.errorCode === 'ACCESS_DENIED') {
        MessageBox.confirm('您没有权限访问此资源，是否重新登录？', '权限不足', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('auth/logout').then(() => {
            location.reload()
          })
        })
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return response
    }
  },
  async error => {
    console.error('响应错误:', error)
    
    const { response } = error
    
    if (response) {
      const { status, data } = response
      
      switch (status) {
        case 401:
          // token过期或无效
          Message({
            message: '登录状态已过期，请重新登录',
            type: 'error',
            duration: 5 * 1000
          })
          
          // 尝试刷新token
          try {
            await store.dispatch('auth/refreshToken')
            // 重新发送原请求
            return service(error.config)
          } catch (refreshError) {
            // 刷新失败，跳转到登录页
            store.dispatch('auth/resetToken')
            location.href = '/login'
          }
          break
          
        case 403:
          Message({
            message: data?.message || '权限不足',
            type: 'error',
            duration: 5 * 1000
          })
          break
          
        case 404:
          Message({
            message: '请求的资源不存在',
            type: 'error',
            duration: 5 * 1000
          })
          break
          
        case 500:
          Message({
            message: data?.message || '服务器内部错误',
            type: 'error',
            duration: 5 * 1000
          })
          break
          
        default:
          Message({
            message: data?.message || `请求失败 (${status})`,
            type: 'error',
            duration: 5 * 1000
          })
      }
    } else if (error.code === 'ECONNABORTED') {
      Message({
        message: '请求超时，请稍后重试',
        type: 'error',
        duration: 5 * 1000
      })
    } else {
      Message({
        message: '网络错误，请检查网络连接',
        type: 'error',
        duration: 5 * 1000
      })
    }
    
    return Promise.reject(error)
  }
)

export default service