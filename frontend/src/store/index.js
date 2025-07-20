import Vue from 'vue'
import Vuex from 'vuex'
import auth from './modules/auth'
import user from './modules/user'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    auth,
    user
  },
  
  // 全局状态
  state: {
    loading: false,
    sidebar: {
      opened: true
    }
  },
  
  // 全局mutations
  mutations: {
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    TOGGLE_SIDEBAR(state) {
      state.sidebar.opened = !state.sidebar.opened
    },
    SET_SIDEBAR(state, opened) {
      state.sidebar.opened = opened
    }
  },
  
  // 全局actions
  actions: {
    setLoading({ commit }, loading) {
      commit('SET_LOADING', loading)
    },
    toggleSidebar({ commit }) {
      commit('TOGGLE_SIDEBAR')
    },
    setSidebar({ commit }, opened) {
      commit('SET_SIDEBAR', opened)
    }
  },
  
  // 全局getters
  getters: {
    loading: state => state.loading,
    sidebar: state => state.sidebar,
    
    // 认证相关
    token: state => state.auth.token,
    userInfo: state => state.auth.userInfo,
    isLoggedIn: state => !!state.auth.token,
    isAdmin: state => {
      const userInfo = state.auth.userInfo
      return userInfo && userInfo.roles && 
             userInfo.roles.some(role => role.name === 'ADMIN')
    },
    
    // 用户相关
    userList: state => state.user.userList,
    userStatistics: state => state.user.statistics
  }
})

export default store