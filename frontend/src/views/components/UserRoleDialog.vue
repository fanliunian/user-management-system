<template>
  <el-dialog
    title="角色管理"
    :visible.sync="dialogVisible"
    width="500px"
    @close="handleClose"
  >
    <div v-if="user" class="role-management">
      <div class="user-info">
        <el-avatar :size="40" icon="el-icon-user-solid"></el-avatar>
        <div class="user-basic">
          <h4>{{ user.username }}</h4>
          <p>{{ user.email }}</p>
        </div>
      </div>
      
      <el-divider />
      
      <div class="current-roles">
        <h4 class="section-title">当前角色</h4>
        <div class="roles-display">
          <el-tag
            v-for="role in currentRoles"
            :key="role.id"
            :type="role.name === 'ADMIN' ? 'danger' : 'primary'"
            size="medium"
            closable
            style="margin-right: 8px; margin-bottom: 8px;"
            @close="handleRemoveRole(role)"
          >
            {{ role.name }}
          </el-tag>
          <span v-if="currentRoles.length === 0" class="no-roles">
            暂无角色
          </span>
        </div>
      </div>
      
      <div class="assign-roles">
        <h4 class="section-title">分配角色</h4>
        <el-select
          v-model="selectedRoleIds"
          multiple
          placeholder="请选择角色"
          style="width: 100%;"
        >
          <el-option
            v-for="role in availableRoles"
            :key="role.id"
            :label="role.name + (role.description ? ' (' + role.description + ')' : '')"
            :value="role.id"
          />
        </el-select>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">
        保存
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getRoleList, assignRolesToUser, getUserRoles } from '@/api/role'

export default {
  name: 'UserRoleDialog',
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
  data() {
    return {
      currentRoles: [],
      availableRoles: [],
      selectedRoleIds: [],
      loading: false
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
      if (val && this.user) {
        this.fetchData()
      }
    }
  },
  methods: {
    async fetchData() {
      await Promise.all([
        this.fetchUserRoles(),
        this.fetchAvailableRoles()
      ])
      
      // 设置已选中的角色
      this.selectedRoleIds = this.currentRoles.map(role => role.id)
    },
    
    async fetchUserRoles() {
      try {
        const response = await getUserRoles(this.user.id)
        if (response.data.success) {
          this.currentRoles = response.data.data
        }
      } catch (error) {
        console.error('获取用户角色失败:', error)
        this.currentRoles = this.user.roles || []
      }
    },
    
    async fetchAvailableRoles() {
      try {
        const response = await getRoleList()
        if (response.data.success) {
          this.availableRoles = response.data.data
        }
      } catch (error) {
        console.error('获取角色列表失败:', error)
        this.$message.error('获取角色列表失败')
      }
    },
    
    handleRemoveRole(role) {
      this.selectedRoleIds = this.selectedRoleIds.filter(id => id !== role.id)
    },
    
    async handleSave() {
      this.loading = true
      try {
        const response = await assignRolesToUser(this.user.id, {
          roleIds: this.selectedRoleIds
        })
        
        if (response.data.success) {
          this.$message.success('角色分配成功')
          this.$emit('success')
          this.handleClose()
        } else {
          this.$message.error(response.data.message || '角色分配失败')
        }
      } catch (error) {
        console.error('角色分配失败:', error)
        this.$message.error(error.response?.data?.message || '角色分配失败')
      } finally {
        this.loading = false
      }
    },
    
    handleClose() {
      this.dialogVisible = false
      this.currentRoles = []
      this.selectedRoleIds = []
    }
  }
}
</script>

<style lang="scss" scoped>
.role-management {
  .user-info {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    
    .user-basic {
      margin-left: 12px;
      
      h4 {
        margin: 0 0 4px 0;
        font-size: 16px;
        color: $text-primary;
      }
      
      p {
        margin: 0;
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
  
  .current-roles,
  .assign-roles {
    margin-bottom: 20px;
    
    .section-title {
      margin: 0 0 12px 0;
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;
    }
  }
  
  .roles-display {
    min-height: 32px;
    
    .no-roles {
      color: $text-placeholder;
      font-style: italic;
      font-size: 14px;
    }
  }
}

.dialog-footer {
  text-align: right;
}
</style>