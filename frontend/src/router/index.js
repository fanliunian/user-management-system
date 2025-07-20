import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'
import { getToken } from '@/utils/auth'

Vue.use(VueRouter)

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { 
      title: '登录',
      requiresAuth: false 
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { 
      title: '注册',
      requiresAuth: false 
    }
  },
  {
    path: '/',
    component: () => import('@/components/Layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { 
          title: '仪表盘',
          requiresAuth: true 
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { 
          title: '个人资料',
          requiresAuth: true 
        }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/UserList.vue'),
        meta: { 
          title: '用户管理',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'roles',
        name: 'RoleManagement',
        component: () => import('@/views/RoleManagement.vue'),
        meta: { 
          title: '角色管理',
          requiresAuth: true,
          requiresAdmin: true
        }
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { 
      title: '权限不足',
      requiresAuth: false 
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { 
      title: '页面不存在',
      requiresAuth: false 
    }
  },
  {
    path: '*',
    redirect: '/404'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 解决Vue Router在3.1.0版本后重复导航报错的问题
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalPush.call(this, location, onResolve, onReject)
  }
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      return Promise.reject(err)
    }
    return Promise.resolve()
  })
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 用户管理系统` : '用户管理系统'

  const token = getToken()
  const hasToken = !!token

  // 如果是登录页
  if (to.path === '/login') {
    if (hasToken) {
      next({ path: '/' })
    } else {
      next()
    }
    return
  }
  
  // 如果没有token
  if (!hasToken) {
    if (to.meta.requiresAuth !== false) {
      next(`/login?redirect=${to.path}`)
    } else {
      next()
    }
    return
  }
  
  // 如果有token但没有用户信息
  if (!store.getters.userInfo || !store.getters.userInfo.id) {
    try {
      // 获取用户信息
      await store.dispatch('auth/getUserInfo')
    } catch (error) {
      // 获取用户信息失败，清除token并跳转到登录页
      await store.dispatch('auth/logout')
      next(`/login?redirect=${to.path}`)
      return
    }
  }
  
  // 检查管理员权限
  if (to.meta.requiresAdmin && !store.getters.isAdmin) {
    next('/403')
    return
  }
  
  // 正常导航
  next()
})

export default router