<template>
  <div class="page-container">
    <page-header title="仪表盘" description="系统概览和统计信息" />
    
    <!-- 欢迎信息 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userInfo.username }}！</h2>
          <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
        </div>
        <div class="welcome-avatar">
          <el-avatar :size="60" icon="el-icon-user-solid"></el-avatar>
        </div>
      </div>
    </el-card>
    
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :lg="6" :md="12" :sm="24">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-info">
              <div class="stats-number">{{ statistics.totalUsers }}</div>
              <div class="stats-label">总用户数</div>
            </div>
            <div class="stats-icon">
              <i class="el-icon-user"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :lg="6" :md="12" :sm="24">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-info">
              <div class="stats-number">{{ statistics.enabledUsers }}</div>
              <div class="stats-label">启用用户</div>
            </div>
            <div class="stats-icon success">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :lg="6" :md="12" :sm="24">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-info">
              <div class="stats-number">{{ statistics.disabledUsers }}</div>
              <div class="stats-label">禁用用户</div>
            </div>
            <div class="stats-icon danger">
              <i class="el-icon-close"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :lg="6" :md="12" :sm="24">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-info">
              <div class="stats-number">{{ roleCount }}</div>
              <div class="stats-label">系统角色</div>
            </div>
            <div class="stats-icon info">
              <i class="el-icon-s-custom"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 内容区域 -->
    <el-row :gutter="16">
      <!-- 快速操作 -->
      <el-col :lg="12" :md="24">
        <el-card>
          <div slot="header" class="card-header">
            <span>快速操作</span>
          </div>
          
          <div class="quick-actions">
            <div class="action-item" @click="goToProfile">
              <div class="action-icon">
                <i class="el-icon-user"></i>
              </div>
              <div class="action-content">
                <div class="action-title">个人资料</div>
                <div class="action-desc">查看和编辑个人信息</div>
              </div>
              <i class="el-icon-arrow-right action-arrow"></i>
            </div>
            
            <div v-if="isAdmin" class="action-item" @click="goToUsers">
              <div class="action-icon">
                <i class="el-icon-user-solid"></i>
              </div>
              <div class="action-content">
                <div class="action-title">用户管理</div>
                <div class="action-desc">管理系统用户账户</div>
              </div>
              <i class="el-icon-arrow-right action-arrow"></i>
            </div>
            
            <div v-if="isAdmin" class="action-item" @click="goToRoles">
              <div class="action-icon">
                <i class="el-icon-s-custom"></i>
              </div>
              <div class="action-content">
                <div class="action-title">角色管理</div>
                <div class="action-desc">管理系统角色权限</div>
              </div>
              <i class="el-icon-arrow-right action-arrow"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 系统信息 -->
      <el-col :lg="12" :md="24">
        <el-card>
          <div slot="header" class="card-header">
            <span>系统信息</span>
          </div>
          
          <div class="system-info">
            <div class="info-item">
              <span class="info-label">系统版本</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">当前用户</span>
              <span class="info-value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">用户角色</span>
              <span class="info-value">
                <el-tag
                  v-for="role in userInfo.roles"
                  :key="role.id"
                  :type="role.name === 'ADMIN' ? 'danger' : 'primary'"
                  size="small"
                  style="margin-right: 4px;"
                >
                  {{ role.name }}
                </el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">注册时间</span>
              <span class="info-value">{{ formatDate(userInfo.createdAt) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">最后登录</span>
              <span class="info-value">{{ formatDate(userInfo.lastLoginAt) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PageHeader from '@/components/common/PageHeader'
import { getRoleList } from '@/api/role'

export default {
  name: 'Dashboard',
  components: {
    PageHeader
  },
  data() {
    return {
      roleCount: 0,
      statistics: {
        totalUsers: 0,
        enabledUsers: 0,
        disabledUsers: 0,
        enabledPercentage: 0
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo', 'isAdmin']),
    
    currentDate() {
      return new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      await Promise.all([
        this.fetchUserStatistics(),
        this.fetchRoleCount()
      ])
    },
    
    async fetchUserStatistics() {
      if (this.isAdmin) {
        const result = await this.$store.dispatch('user/fetchUserStatistics')
        if (result.success) {
          this.statistics = result.data
        }
      }
    },
    
    async fetchRoleCount() {
      try {
        const response = await getRoleList()
        if (response.data.success) {
          this.roleCount = response.data.data.length
        }
      } catch (error) {
        console.error('获取角色数量失败:', error)
      }
    },
    
    goToProfile() {
      this.$router.push('/profile')
    },
    
    goToUsers() {
      this.$router.push('/users')
    },
    
    goToRoles() {
      this.$router.push('/roles')
    },
    
    formatDate(dateString) {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    }
  }
}
</script>

<style lang="scss" scoped>
.welcome-card {
  margin-bottom: 16px;
  
  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .welcome-text {
      h2 {
        margin: 0 0 8px 0;
        font-size: 24px;
        color: $text-primary;
      }
      
      p {
        margin: 0;
        color: $text-secondary;
        font-size: 14px;
      }
    }
  }
}

.stats-row {
  margin-bottom: 16px;
  
  .el-col {
    margin-bottom: 16px;
  }
}

.stats-card {
  .stats-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .stats-info {
      .stats-number {
        font-size: 28px;
        font-weight: 700;
        color: $text-primary;
        margin-bottom: 4px;
      }
      
      .stats-label {
        font-size: 14px;
        color: $text-secondary;
      }
    }
    
    .stats-icon {
      font-size: 32px;
      color: $text-placeholder;
      
      &.success {
        color: $success-color;
      }
      
      &.danger {
        color: $danger-color;
      }
      
      &.info {
        color: $info-color;
      }
    }
  }
}

.card-header {
  font-weight: 600;
  color: $text-primary;
}

.quick-actions {
  .action-item {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid $border-extra-light;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:hover {
      background-color: $gray-color;
      margin: 0 -20px;
      padding: 16px 20px;
      border-radius: $border-radius-base;
    }
    
    .action-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background-color: $primary-color;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      
      i {
        font-size: 18px;
      }
    }
    
    .action-content {
      flex: 1;
      
      .action-title {
        font-weight: 500;
        color: $text-primary;
        margin-bottom: 4px;
      }
      
      .action-desc {
        font-size: 12px;
        color: $text-secondary;
      }
    }
    
    .action-arrow {
      color: $text-placeholder;
      font-size: 14px;
    }
  }
}

.system-info {
  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid $border-extra-light;
    
    &:last-child {
      border-bottom: none;
    }
    
    .info-label {
      font-weight: 500;
      color: $text-regular;
    }
    
    .info-value {
      color: $text-secondary;
      font-size: 14px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
    
    .welcome-text {
      margin-bottom: 16px;
      
      h2 {
        font-size: 20px;
      }
    }
  }
  
  .stats-card {
    margin-bottom: 16px;
  }
  
  .quick-actions .action-item:hover {
    margin: 0;
    padding: 16px 0;
    background-color: transparent;
  }
}
</style>