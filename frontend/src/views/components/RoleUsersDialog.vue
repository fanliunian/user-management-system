<template>
  <el-dialog
    :title="`角色用户 - ${role ? role.name : ''}`"
    :visible.sync="dialogVisible"
    width="800px"
    @close="handleClose"
  >
    <div v-if="role" class="role-users">
      <div class="role-info">
        <el-tag
          :type="role.name === 'ADMIN' ? 'danger' : 'primary'"
          size="medium"
        >
          {{ role.name }}
        </el-tag>
        <span v-if="role.description" class="role-description">
          {{ role.description }}
        </span>
      </div>
      
      <el-divider />
      
      <div class="users-section">
        <div class="section-header">
          <h4>拥有此角色的用户 ({{ userList.length }})</h4>
          <el-button
            type="primary"
            size="small"
            icon="el-icon-plus"
            @click="handleAssignUsers"
          >
            分配用户
          </el-button>
        </div>
        
        <div v-loading="loading" class="users-list">
          <div v-if="userList.length === 0" class="empty-users">
            <empty-state
              icon="el-icon-user"
              title="暂无用户"
              description="该角色还没有分配给任何用户"
            />
          </div>
          
          <div v-else class="users-grid">
            <div
              v-for="user in userList"
              :key="user.id"
              class="user-card"
            >
              <div class="user-info">
                <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
                <div class="user-details">
                  <div class="user-name">{{ user.username }}</div>
                  <div class="user-email">{{ user.email }}</div>
                </div>
              </div>
              <div class="user-actions">
                <el-tag
                  :type="user.status === 1 ? 'success' : 'danger'"
                  size="small"
                >
                  {{ user.status === 1 ? '启用' : '禁用' }}
                </el-tag>
                <el-button
                  type="text"
                  size="small"
                  style="color: #f56c6c;"
                  @click="handleRemoveUser(user)"
                >
                  移除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">关闭</el-button>
    </div>
    
    <!-- 分配用户对话框 -->
    <assign-users-dialog
      :visible.sync="assignUsersVisible"
      :role="role"
      :existing-user-ids="existingUserIds"
      @success="fetchUsers"
    />
  </el-dialog>
</template>

<script>
import EmptyState from '@/components/common/EmptyState'
import AssignUsersDialog from './AssignUsersDialog'
import { getUserIdsByRole, removeRoleFromUser } from '@/api/role'
import { getUserById } from '@/api/user'

export default {
  name: 'RoleUsersDialog',
  components: {
    EmptyState,
    AssignUsersDialog
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    role: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      userList: [],
      loading: false,
      assignUsersVisible: false
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
    },
    
    existingUserIds() {
      return this.userList.map(user => user.id)
    }
  },
  watch: {
    visible(val) {
      if (val && this.role) {
        this.fetchUsers()
      }
    }
  },
  methods: {
    async fetchUsers() {
      if (!this.role) return
      
      this.loading = true
      try {
        const response = await getUserIdsByRole(this.role.id)
        if (response.data.success) {
          const userIds = response.data.data
          
          // 获取用户详细信息
          const userPromises = userIds.map(userId => getUserById(userId))
          const userResponses = await Promise.all(userPromises)
          
          this.userList = userResponses
            .filter(res => res.data.success)
            .map(res => res.data.data)
        }
      } catch (error) {
        console.error('获取角色用户失败:', error)
        this.$message.error('获取角色用户失败')
      } finally {
        this.loading = false
      }
    },
    
    handleAssignUsers() {
      this.assignUsersVisible = true
    },
    
    async handleRemoveUser(user) {
      try {
        await this.$confirm(`确定要从角色 "${this.role.name}" 中移除用户 "${user.username}" 吗？`, '确认移除', {
          type: 'warning'
        })
        
        const response = await removeRoleFromUser(user.id, this.role.id)
        if (response.data.success) {
          this.$message.success('用户移除成功')
          this.fetchUsers()
        } else {
          this.$message.error(response.data.message || '用户移除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('移除用户失败:', error)
          this.$message.error(error.response?.data?.message || '移除用户失败')
        }
      }
    },
    
    handleClose() {
      this.dialogVisible = false
      this.userList = []
    }
  }
}
</script>

<style lang="scss" scoped>
.role-users {
  .role-info {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .role-description {
      color: $text-secondary;
      font-size: 14px;
    }
  }
  
  .users-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h4 {
        margin: 0;
        font-size: 16px;
        color: $text-primary;
      }
    }
    
    .users-list {
      min-height: 200px;
      
      .empty-users {
        padding: 40px 0;
      }
      
      .users-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 16px;
      }
      
      .user-card {
        border: 1px solid $border-light;
        border-radius: $border-radius-base;
        padding: 16px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        transition: all 0.3s ease;
        
        &:hover {
          border-color: $primary-color;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
        }
        
        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .user-details {
            .user-name {
              font-weight: 500;
              color: $text-primary;
              margin-bottom: 4px;
            }
            
            .user-email {
              font-size: 12px;
              color: $text-secondary;
            }
          }
        }
        
        .user-actions {
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }
    }
  }
}

.dialog-footer {
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .users-grid {
    grid-template-columns: 1fr !important;
  }
  
  .section-header {
    flex-direction: column;
    align-items: stretch !important;
    gap: 12px;
    
    .el-button {
      align-self: flex-start;
    }
  }
}
</style>