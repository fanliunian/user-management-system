<template>
  <div class="sidebar">
    <!-- Logo区域 -->
    <div class="sidebar-logo">
      <router-link to="/" class="sidebar-logo-link">
        <img v-if="!isCollapse" src="/logo.png" alt="Logo" class="sidebar-logo-img">
        <h1 v-if="!isCollapse" class="sidebar-title">用户管理系统</h1>
        <h1 v-else class="sidebar-title-mini">UMS</h1>
      </router-link>
    </div>
    
    <!-- 导航菜单 -->
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
        background-color="#ffffff"
        text-color="#303133"
        active-text-color="#409EFF"
        router
        @select="handleSelect"
      >
        <!-- 仪表盘 -->
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">仪表盘</span>
        </el-menu-item>
        
        <!-- 个人资料 -->
        <el-menu-item index="/profile">
          <i class="el-icon-user"></i>
          <span slot="title">个人资料</span>
        </el-menu-item>
        
        <!-- 管理员功能 -->
        <template v-if="isAdmin">
          <el-submenu index="management">
            <template slot="title">
              <i class="el-icon-setting"></i>
              <span>系统管理</span>
            </template>
            
            <el-menu-item index="/users">
              <i class="el-icon-user-solid"></i>
              <span slot="title">用户管理</span>
            </el-menu-item>
            
            <el-menu-item index="/roles">
              <i class="el-icon-s-custom"></i>
              <span slot="title">角色管理</span>
            </el-menu-item>
          </el-submenu>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Sidebar',
  computed: {
    ...mapGetters(['sidebar', 'isAdmin']),
    
    isCollapse() {
      return !this.sidebar.opened
    },
    
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      // 如果当前路由与选中的菜单项不同，则进行路由跳转
      if (this.$route.path !== key) {
        this.$router.push(key).catch(err => {
          // 忽略重复导航错误
          if (err.name !== 'NavigationDuplicated') {
            console.error('路由导航错误:', err);
          }
        });
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  background-color: $white-color;
}

.sidebar-logo {
  width: 100%;
  height: 60px;
  line-height: 60px;
  background-color: $primary-color;
  text-align: center;
  overflow: hidden;
  
  .sidebar-logo-link {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    text-decoration: none;
    
    .sidebar-logo-img {
      width: 32px;
      height: 32px;
      margin-right: 8px;
    }
    
    .sidebar-title {
      font-size: 18px;
      font-weight: 600;
      color: $white-color;
      margin: 0;
    }
    
    .sidebar-title-mini {
      font-size: 16px;
      font-weight: 600;
      color: $white-color;
      margin: 0;
    }
  }
}

.scrollbar-wrapper {
  overflow-x: hidden !important;
}

.el-scrollbar__bar.is-vertical {
  right: 0;
}

.el-scrollbar__bar.is-horizontal {
  display: none;
}

// 菜单样式
.el-menu {
  border: none;
  height: calc(100% - 60px);
  width: 100% !important;
}

.el-menu-item {
  &:hover {
    background-color: $gray-color !important;
  }
  
  &.is-active {
    background-color: rgba(64, 158, 255, 0.1) !important;
    border-right: 3px solid $primary-color;
  }
}

.el-submenu {
  .el-submenu__title:hover {
    background-color: $gray-color !important;
  }
}

// 折叠状态样式
.el-menu--collapse {
  .el-menu-item,
  .el-submenu__title {
    text-align: center;
    padding: 0 20px !important;
  }
}
</style>