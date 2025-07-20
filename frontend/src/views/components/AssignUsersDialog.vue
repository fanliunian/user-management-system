<template>
  <el-dialog
    title="分配用户"
    :visible.sync="dialogVisible"
    width="600px"
    @close="handleClose"
  >
    <div class="assign-users">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名或邮箱"
          prefix-icon="el-icon-search"
          clearable
          @input="handleSearch"
        />
      </div>
      
      <div class="users-section">
        <div class="section-title">
          选择要分配给角色 "{{ role ? role.name : '' }}" 的用户
        </div>
        
        <div v-loading="loading" class="users-list">
          <el-checkbox-group v-model="selectedUserIds">
            <div
              v-for="user in filteredUsers"
              :key="user.id"
              class="user-item"
            >
              <el-checkbox :label="user.id" :disabled="existingUserIds.includes(user.id)">
                <div class="user-info">
                  <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
                  <div class="user-details">
                    <div class="user-name">{{ user.username }}</div>
                    <div class="user-email">{{ user.email }}</div>
                  </div>
                  <div class="user-status">
                    <el-tag
                      :type="user.status === 1 ? 'success' : 'danger'"
                      size="small"
                    >
                      {{ user.status === 1 ? '启用' : '禁用' }}
                    </el-tag>
                    <span v-if="existingUserIds.includes(user.id)" class="already-assigned">
                      已分配
                    </span>
                  </div>
                </div>
              </el-checkbox>
            </div>
          </el-checkbox-group>
          
          <div v-if="filteredUsers.length === 0" class="empty-users">
            <empty-state
              icon="el-icon-user"
              title="暂无用户"
              description="没有找到符合条件的用户"
            />
          </div>
        </div>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button
        type="primary"
        :loading="saving"
        :disabled="selectedUserIds.length === 0"
        @click="handleSave"
      >
        分配 ({{ selectedUserIds.length }})
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import EmptyState from '@/components/common/EmptyState'
import { getUserList } from '@/api/user'
import { addRoleToUser } from '@/api/role'

export default {
  name: 'AssignUsersDialog',
  components: {
    EmptyState
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    role: {
      type: Object,
      default: null
    },
    existingUserIds: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      allUsers: [],
      filteredUsers: [],
      selectedUserIds: [],
      searchKeyword: '',
      loading: false,
      saving: false
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
  watch: {
    visible(val) {
      if (val) {
        this.fetchUsers()
      }
    }
  },
  methods: {
    async fetchUsers() {
      this.loading = true
      try {
        const response = await getUserList({
          page: 0,
          size: 1000 // 获取所有用户
        })
        
        if (response.data.success) {
          this.allUsers = response.data.data.content
          this.filteredUsers = [...this.allUsers]
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.$message.error('获取用户列表失败')
      } finally {
        this.loading = false
      }
    },
    
    handleSearch() {
      if (!this.searchKeyword.trim()) {
        this.filteredUsers = [...this.allUsers]
      } else {
        const keyword = this.searchKeyword.toLowerCase()
        this.filteredUsers = this.allUsers.filter(user =>
          user.username.toLowerCase().includes(keyword) ||
          user.email.toLowerCase().includes(keyword)
        )
      }
    },
    
    async handleSave() {
      if (this.selectedUserIds.length === 0) {
        this.$message.warning('请选择要分配的用户')
        return
      }
      
      this.saving = true
      try {
        const promises = this.selectedUserIds.map(userId =>
          addRoleToUser(userId, this.role.id)
        )
        
        const results = await Promise.allSettled(promises)
        
        const successCount = results.filter(result => result.status === 'fulfilled').length
        const failCount = results.length - successCount
        
        if (successCount > 0) {
          this.$message.success(`成功分配 ${successCount} 个用户`)
          this.$emit('success')
          this.handleClose()
        }
        
        if (failCount > 0) {
          this.$message.warning(`${failCount} 个用户分配失败`)
        }
      } catch (error) {
        console.error('分配用户失败:', error)
        this.$message.error('分配用户失败')
      } finally {
        this.saving = false
      }
    },
    
    handleClose() {
      this.dialogVisible = false
      this.selectedUserIds = []
      this.searchKeyword = ''
      this.allUsers = []
      this.filteredUsers = []
    }
  }
}
</script>

<style lang="scss" scoped>
.assign-users {
  .search-section {
    margin-bottom: 20px;
  }
  
  .users-section {
    .section-title {
      font-size: 14px;
      color: $text-regular;
      margin-bottom: 16px;
    }
    
    .users-list {
      max-height: 400px;
      overflow-y: auto;
      border: 1px solid $border-light;
      border-radius: $border-radius-base;
      
      .user-item {
        padding: 12px 16px;
        border-bottom: 1px solid $border-extra-light;
        
        &:last-child {
          border-bottom: none;
        }
        
        &:hover {
          background-color: $gray-color;
        }
        
        .el-checkbox {
          width: 100%;
          
          .el-checkbox__label {
            width: 100%;
            padding-left: 8px;
          }
        }
        
        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .user-details {
            flex: 1;
            
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
          
          .user-status {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .already-assigned {
              font-size: 12px;
              color: $text-placeholder;
            }
          }
        }
      }
      
      .empty-users {
        padding: 40px 0;
      }
    }
  }
}

.dialog-footer {
  text-align: right;
}
</style>