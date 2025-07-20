import { 
  getUserList, 
  getUserById, 
  updateUserStatus, 
  deleteUser, 
  getUserStatistics,
  batchUpdateUserStatus
} from '@/api/user'

const state = {
  userList: {
    content: [],
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 10
  },
  currentUser: null,
  statistics: {
    totalUsers: 0,
    enabledUsers: 0,
    disabledUsers: 0,
    enabledPercentage: 0
  },
  loading: false
}

const mutations = {
  SET_USER_LIST(state, userList) {
    state.userList = userList
  },
  SET_CURRENT_USER(state, user) {
    state.currentUser = user
  },
  SET_STATISTICS(state, statistics) {
    state.statistics = statistics
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  UPDATE_USER_IN_LIST(state, updatedUser) {
    const index = state.userList.content.findIndex(user => user.id === updatedUser.id)
    if (index !== -1) {
      state.userList.content.splice(index, 1, updatedUser)
    }
  },
  REMOVE_USER_FROM_LIST(state, userId) {
    const index = state.userList.content.findIndex(user => user.id === userId)
    if (index !== -1) {
      state.userList.content.splice(index, 1)
      state.userList.totalElements--
    }
  }
}

const actions = {
  // 获取用户列表
  async fetchUserList({ commit }, params = {}) {
    commit('SET_LOADING', true)
    try {
      const response = await getUserList(params)
      const { data } = response
      
      if (data.success) {
        commit('SET_USER_LIST', data.data)
        return { success: true, data: data.data }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('获取用户列表失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '获取用户列表失败' 
      }
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取用户详情
  async fetchUserById({ commit }, userId) {
    try {
      const response = await getUserById(userId)
      const { data } = response
      
      if (data.success) {
        commit('SET_CURRENT_USER', data.data)
        return { success: true, data: data.data }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('获取用户详情失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '获取用户详情失败' 
      }
    }
  },

  // 更新用户状态
  async updateUserStatus({ commit }, { userId, status }) {
    try {
      const response = await updateUserStatus(userId, { status })
      const { data } = response
      
      if (data.success) {
        // 更新列表中的用户状态
        const updatedUser = { ...state.currentUser, status }
        commit('UPDATE_USER_IN_LIST', updatedUser)
        
        return { success: true, message: data.message }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('更新用户状态失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '更新用户状态失败' 
      }
    }
  },

  // 批量更新用户状态
  async batchUpdateUserStatus({ commit }, { userIds, status }) {
    try {
      const response = await batchUpdateUserStatus({ userIds, status })
      const { data } = response
      
      if (data.success) {
        // 更新列表中的用户状态
        userIds.forEach(userId => {
          const user = state.userList.content.find(u => u.id === userId)
          if (user) {
            const updatedUser = { ...user, status }
            commit('UPDATE_USER_IN_LIST', updatedUser)
          }
        })
        
        return { success: true, message: data.message }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('批量更新用户状态失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '批量更新用户状态失败' 
      }
    }
  },

  // 删除用户
  async deleteUser({ commit }, userId) {
    try {
      const response = await deleteUser(userId)
      const { data } = response
      
      if (data.success) {
        commit('REMOVE_USER_FROM_LIST', userId)
        return { success: true, message: data.message }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '删除用户失败' 
      }
    }
  },

  // 获取用户统计信息
  async fetchUserStatistics({ commit }) {
    try {
      const response = await getUserStatistics()
      const { data } = response
      
      if (data.success) {
        commit('SET_STATISTICS', data.data)
        return { success: true, data: data.data }
      } else {
        return { success: false, message: data.message }
      }
    } catch (error) {
      console.error('获取用户统计失败:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '获取用户统计失败' 
      }
    }
  },

  // 清空用户列表
  clearUserList({ commit }) {
    commit('SET_USER_LIST', {
      content: [],
      totalElements: 0,
      totalPages: 0,
      currentPage: 0,
      pageSize: 10
    })
  },

  // 清空当前用户
  clearCurrentUser({ commit }) {
    commit('SET_CURRENT_USER', null)
  }
}

const getters = {
  userList: state => state.userList,
  currentUser: state => state.currentUser,
  statistics: state => state.statistics,
  loading: state => state.loading,
  totalUsers: state => state.userList.totalElements,
  enabledUsers: state => state.userList.content.filter(user => user.status === 1).length,
  disabledUsers: state => state.userList.content.filter(user => user.status === 0).length
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}