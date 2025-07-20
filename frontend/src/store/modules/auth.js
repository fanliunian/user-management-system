import { login, register, refreshToken, getUserProfile } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

const state = {
  token: getToken(),
  userInfo: null,
  refreshToken: localStorage.getItem('refreshToken') || ''
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
  },
  SET_REFRESH_TOKEN(state, refreshToken) {
    state.refreshToken = refreshToken
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
  },
  CLEAR_AUTH(state) {
    state.token = ''
    state.refreshToken = ''
    state.userInfo = null
  }
}

const actions = {
  // 用户登录
  async login({ commit }, loginForm) {
    try {
      const response = await login(loginForm)
      const { data } = response
      
      if (data.success) {
        const { accessToken, refreshToken, user } = data.data
        
        // 保存token
        commit('SET_TOKEN', accessToken)
        commit('SET_REFRESH_TOKEN', refreshToken)
        commit('SET_USER_INFO', user)
        
        setToken(accessToken)
        localStorage.setItem('refreshToken', refreshToken)
        
        return { success: true, data: user }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('登录失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '登录失败，请稍后重试' 
      }
    }
  },

  // 用户注册
  async register({ commit }, registerForm) {
    try {
      const response = await register(registerForm)
      const { data } = response
      
      if (data.success) {
        return { success: true, data: data.data }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('注册失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '注册失败，请稍后重试' 
      }
    }
  },

  // 获取用户信息
  async getUserInfo({ commit, state }) {
    try {
      if (!state.token) {
        throw new Error('Token不存在')
      }
      
      const response = await getUserProfile()
      const { data } = response
      
      if (data.success) {
        commit('SET_USER_INFO', data.data)
        return data.data
      } else {
        throw new Error(data.message)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  },

  // 刷新token
  async refreshToken({ commit, state }) {
    try {
      if (!state.refreshToken) {
        throw new Error('Refresh token不存在')
      }
      
      const response = await refreshToken({ refreshToken: state.refreshToken })
      const { data } = response
      
      if (data.success) {
        const { accessToken, refreshToken: newRefreshToken } = data.data
        
        commit('SET_TOKEN', accessToken)
        commit('SET_REFRESH_TOKEN', newRefreshToken)
        
        setToken(accessToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        
        return accessToken
      } else {
        throw new Error(data.message)
      }
    } catch (error) {
      console.error('刷新token失败:', error)
      // 刷新失败，清除所有认证信息
      commit('CLEAR_AUTH')
      removeToken()
      localStorage.removeItem('refreshToken')
      throw error
    }
  },

  // 用户登出
  async logout({ commit }) {
    try {
      // 清除本地存储
      commit('CLEAR_AUTH')
      removeToken()
      localStorage.removeItem('refreshToken')
      
      return { success: true }
    } catch (error) {
      console.error('登出失败:', error)
      return { success: false, message: '登出失败' }
    }
  },

  // 重置token（用于token过期等情况）
  resetToken({ commit }) {
    commit('CLEAR_AUTH')
    removeToken()
    localStorage.removeItem('refreshToken')
  }
}

const getters = {
  token: state => state.token,
  userInfo: state => state.userInfo,
  refreshToken: state => state.refreshToken,
  isLoggedIn: state => !!state.token,
  userId: state => state.userInfo?.id,
  username: state => state.userInfo?.username,
  email: state => state.userInfo?.email,
  roles: state => state.userInfo?.roles || [],
  isAdmin: state => {
    const roles = state.userInfo?.roles || []
    return roles.some(role => role.name === 'ADMIN')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}