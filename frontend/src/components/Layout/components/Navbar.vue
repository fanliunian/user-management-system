<template>
  <div class="navbar">
    <!-- 左侧区域 -->
    <div class="navbar-left">
      <!-- 折叠按钮 -->
      <div class="hamburger-container" @click="toggleSidebar">
        <i :class="sidebar.opened ? 'el-icon-s-fold' : 'el-icon-s-unfold'" class="hamburger"></i>
      </div>
      
      <!-- 面包屑导航 -->
      <breadcrumb class="breadcrumb-container" />
    </div>
    
    <!-- 右侧区域 -->
    <div class="navbar-right">
      <!-- 用户信息下拉菜单 -->
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="32" :src="userInfo.avatar" icon="el-icon-user-solid"></el-avatar>
          <span class="username">{{ userInfo.username }}</span>
          <i class="el-icon-caret-bottom"></i>
        </div>
        
        <el-dropdown-menu slot="dropdown">
          <router-link to="/profile">
            <el-dropdown-item>
              <i class="el-icon-user"></i>
              个人资料
            </el-dropdown-item>
          </router-link>
          
          <el-dropdown-item divided @click.native="handleLogout">
            <i class="el-icon-switch-button"></i>
            退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from './Breadcrumb'

export default {
  name: 'Navbar',
  components: {
    Breadcrumb
  },
  computed: {
    ...mapGetters(['sidebar', 'userInfo'])
  },
  methods: {
    toggleSidebar() {
      this.$store.dispatch('toggleSidebar')
    },
    
    async handleLogout() {
      try {
        await this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const result = await this.$store.dispatch('auth/logout')
        if (result.success) {
          this.$message.success('退出登录成功')
          this.$router.push('/login')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('退出登录失败')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: $header-height;
  overflow: hidden;
  position: relative;
  background: $white-color;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.hamburger-container {
  line-height: $header-height;
  height: 100%;
  float: left;
  cursor: pointer;
  transition: background 0.3s;
  -webkit-tap-highlight-color: transparent;
  
  &:hover {
    background: rgba(0, 0, 0, 0.025);
  }
}

.hamburger {
  display: block;
  font-size: 20px;
  color: $text-regular;
  padding: 0 15px;
}

.breadcrumb-container {
  margin-left: 20px;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.avatar-container {
  margin-right: 10px;
  
  .avatar-wrapper {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    .username {
      margin: 0 8px;
      font-size: 14px;
      color: $text-primary;
    }
    
    .el-icon-caret-bottom {
      font-size: 12px;
      color: $text-secondary;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .navbar {
    padding: 0 15px;
  }
  
  .breadcrumb-container {
    display: none;
  }
  
  .avatar-wrapper .username {
    display: none;
  }
}
</style>