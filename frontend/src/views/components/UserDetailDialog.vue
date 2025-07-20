<template>
  <el-dialog
    title="用户详情"
    :visible.sync="dialogVisible"
    width="600px"
    @close="handleClose"
  >
    <div v-if="user" class="user-detail">
      <div class="user-header">
        <el-avatar :size="60" icon="el-icon-user-solid"></el-avatar>
        <div class="user-basic">
          <h3 class="user-name">{{ user.username }}</h3>
          <p class="user-email">{{ user.email }}</p>
          <el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small">
            {{ user.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </div>
      </div>
      
      <el-divider />
      
      <div class="user-info">
        <el-row :gutter="16">
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">用户ID</span>
              <span class="info-value">{{ user.id }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ user.username }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">邮箱地址</span>
              <span class="info-value">{{ user.email }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">账户状态</span>
              <span class="info-value">
                <el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small">
                  {{ user.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">注册时间</span>
              <span class="info-value">{{ formatDate(user.createdAt) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">最后更新</span>
              <span class="info-value">{{ formatDate(user.updatedAt) }}</span>
            </div>
          </el-col>
          <el-col :span="24">
            <div class="info-item">
              <span class="info-label">最后登录</span>
              <span class="info-value">{{ formatDate(user.lastLoginAt) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <el-divider />
      
      <div class="user-roles">
        <h4 class="section-title">用户角色</h4>
        <div class="roles-list">
          <el-tag
            v-for="role in user.roles"
            :key="role.id"
            :type="role.name === 'ADMIN' ? 'danger' : 'primary'"
            size="medium"
            style="margin-right: 8px; margin-bottom: 8px;"
          >
            {{ role.name }}
            <span v-if="role.description" class="role-desc">
              ({{ role.description }})
            </span>
          </el-tag>
          <span v-if="!user.roles || user.roles.length === 0" class="no-roles">
            暂无角色
          </span>
        </div>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'UserDetailDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    user: {
      type: Object,
      default: null
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
    },
    
    formatDate(dateString) {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-detail {
  .user-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    
    .user-basic {
      margin-left: 16px;
      flex: 1;
      
      .user-name {
        margin: 0 0 8px 0;
        font-size: 18px;
        font-weight: 600;
        color: $text-primary;
      }
      
      .user-email {
        margin: 0 0 8px 0;
        color: $text-secondary;
        font-size: 14px;
      }
    }
  }
  
  .user-info {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      
      .info-label {
        font-weight: 500;
        color: $text-regular;
        font-size: 14px;
      }
      
      .info-value {
        color: $text-secondary;
        font-size: 14px;
        text-align: right;
      }
    }
  }
  
  .user-roles {
    .section-title {
      margin: 0 0 16px 0;
      font-size: 16px;
      font-weight: 600;
      color: $text-primary;
    }
    
    .roles-list {
      .role-desc {
        font-size: 12px;
        opacity: 0.8;
      }
      
      .no-roles {
        color: $text-placeholder;
        font-style: italic;
      }
    }
  }
}

.dialog-footer {
  text-align: right;
}
</style>