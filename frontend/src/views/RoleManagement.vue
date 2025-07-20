<template>
  <div class="page-container">
    <page-header title="角色管理" description="管理系统中的角色和权限">
      <template #extra>
        <el-button type="primary" icon="el-icon-plus" @click="handleCreateRole">
          新建角色
        </el-button>
        <el-button icon="el-icon-refresh" @click="handleRefresh">
          刷新
        </el-button>
      </template>
    </page-header>
    
    <!-- 角色列表 -->
    <el-card>
      <div slot="header" class="table-header">
        <span>角色列表</span>
        <div class="table-actions">
          <el-button
            v-if="selectedRoles.length > 0"
            type="danger"
            size="small"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
        </div>
      </div>
      
      <el-table
        v-loading="loading"
        :data="roleList"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="角色名称" min-width="120">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.name === 'ADMIN' ? 'danger' : 'primary'"
              size="medium"
            >
              {{ scope.row.name }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="角色描述" min-width="200">
          <template slot-scope="scope">
            <span>{{ scope.row.description || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="userCount" label="用户数量" width="100">
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="handleViewUsers(scope.row)"
            >
              {{ scope.row.userCount || 0 }}
            </el-button>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="updatedAt" label="更新时间" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.updatedAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handleEditRole(scope.row)"
            >
              编辑
            </el-button>
            
            <el-button
              type="text"
              size="small"
              @click="handleViewUsers(scope.row)"
            >
              用户
            </el-button>
            
            <el-button
              type="text"
              size="small"
              style="color: #f56c6c;"
              :disabled="!scope.row.canDelete"
              @click="handleDeleteRole(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="roleList.length === 0" class="empty-state">
        <empty-state
          icon="el-icon-s-custom"
          title="暂无角色"
          description="系统中还没有创建任何角色"
        >
          <template #action>
            <el-button type="primary" @click="handleCreateRole">
              创建第一个角色
            </el-button>
          </template>
        </empty-state>
      </div>
    </el-card>
    
    <!-- 角色编辑对话框 -->
    <role-edit-dialog
      :visible.sync="roleEditVisible"
      :role="selectedRole"
      @success="handleRefresh"
    />
    
    <!-- 角色用户列表对话框 -->
    <role-users-dialog
      :visible.sync="roleUsersVisible"
      :role="selectedRole"
    />
  </div>
</template>

<script>
import PageHeader from '@/components/common/PageHeader'
import EmptyState from '@/components/common/EmptyState'
import RoleEditDialog from './components/RoleEditDialog'
import RoleUsersDialog from './components/RoleUsersDialog'
import { getRoleList, deleteRole, batchDeleteRoles, getRoleUsage, canDeleteRole } from '@/api/role'

export default {
  name: 'RoleManagement',
  components: {
    PageHeader,
    EmptyState,
    RoleEditDialog,
    RoleUsersDialog
  },
  data() {
    return {
      roleList: [],
      selectedRoles: [],
      selectedRole: null,
      roleEditVisible: false,
      roleUsersVisible: false,
      loading: false
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const response = await getRoleList()
        if (response.data.success) {
          this.roleList = response.data.data
          
          // 获取每个角色的用户数量和删除权限
          await Promise.all(this.roleList.map(async (role) => {
            try {
              const [usageResponse, canDeleteResponse] = await Promise.all([
                getRoleUsage(role.id),
                canDeleteRole(role.id)
              ])
              
              if (usageResponse.data.success) {
                role.userCount = usageResponse.data.data
              }
              
              if (canDeleteResponse.data.success) {
                role.canDelete = canDeleteResponse.data.data
              }
            } catch (error) {
              console.error(`获取角色 ${role.name} 信息失败:`, error)
              role.userCount = 0
              role.canDelete = false
            }
          }))
        }
      } catch (error) {
        console.error('获取角色列表失败:', error)
        this.$message.error('获取角色列表失败')
      } finally {
        this.loading = false
      }
    },
    
    handleRefresh() {
      this.fetchData()
    },
    
    handleSelectionChange(selection) {
      this.selectedRoles = selection
    },
    
    handleCreateRole() {
      this.selectedRole = null
      this.roleEditVisible = true
    },
    
    handleEditRole(role) {
      this.selectedRole = role
      this.roleEditVisible = true
    },
    
    handleViewUsers(role) {
      this.selectedRole = role
      this.roleUsersVisible = true
    },
    
    async handleDeleteRole(role) {
      try {
        let confirmMessage = `确定要删除角色 "${role.name}" 吗？`
        if (role.userCount > 0) {
          confirmMessage += `\n该角色当前有 ${role.userCount} 个用户，删除后这些用户将失去该角色。`
        }
        
        await this.$confirm(confirmMessage, '确认删除', {
          type: 'error',
          dangerouslyUseHTMLString: true
        })
        
        const response = await deleteRole(role.id)
        if (response.data.success) {
          this.$message.success('角色删除成功')
          this.fetchData()
        } else {
          this.$message.error(response.data.message || '角色删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除角色失败:', error)
          this.$message.error(error.response?.data?.message || '删除角色失败')
        }
      }
    },
    
    async handleBatchDelete() {
      try {
        const roleNames = this.selectedRoles.map(role => role.name).join('、')
        const totalUsers = this.selectedRoles.reduce((sum, role) => sum + (role.userCount || 0), 0)
        
        let confirmMessage = `确定要删除选中的 ${this.selectedRoles.length} 个角色（${roleNames}）吗？`
        if (totalUsers > 0) {
          confirmMessage += `\n这些角色总共有 ${totalUsers} 个用户，删除后这些用户将失去相应角色。`
        }
        
        await this.$confirm(confirmMessage, '确认批量删除', {
          type: 'error',
          dangerouslyUseHTMLString: true
        })
        
        const roleIds = this.selectedRoles.map(role => role.id)
        const response = await batchDeleteRoles({ roleIds })
        
        if (response.data.success) {
          this.$message.success('角色批量删除成功')
          this.selectedRoles = []
          this.fetchData()
        } else {
          this.$message.error(response.data.message || '角色批量删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除角色失败:', error)
          this.$message.error(error.response?.data?.message || '批量删除角色失败')
        }
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    }
  }
}
</script>

<style lang="scss" scoped>
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.empty-state {
  padding: 40px 0;
}

// 响应式设计
@media (max-width: 768px) {
  .table-actions {
    margin-top: 16px;
  }
}
</style>