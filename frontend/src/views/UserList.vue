<template>
  <div class="page-container">
    <page-header title="用户管理" description="管理系统中的所有用户账户">
      <template #extra>
        <el-button type="primary" icon="el-icon-refresh" @click="handleRefresh">
          刷新
        </el-button>
      </template>
    </page-header>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.search"
            placeholder="用户名或邮箱"
            prefix-icon="el-icon-search"
            clearable
            @keyup.enter.native="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="角色">
          <el-select v-model="searchForm.roleId" placeholder="全部角色" clearable>
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 用户统计 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.totalUsers }}</div>
            <div class="stats-label">总用户数</div>
          </div>
          <i class="el-icon-user stats-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.enabledUsers }}</div>
            <div class="stats-label">启用用户</div>
          </div>
          <i class="el-icon-check stats-icon success"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.disabledUsers }}</div>
            <div class="stats-label">禁用用户</div>
          </div>
          <i class="el-icon-close stats-icon danger"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.enabledPercentage.toFixed(1) }}%</div>
            <div class="stats-label">启用率</div>
          </div>
          <i class="el-icon-pie-chart stats-icon info"></i>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 用户表格 -->
    <el-card>
      <div slot="header" class="table-header">
        <span>用户列表</span>
        <div class="table-actions">
          <el-button
            v-if="selectedUsers.length > 0"
            type="success"
            size="small"
            @click="handleBatchEnable"
          >
            批量启用
          </el-button>
          <el-button
            v-if="selectedUsers.length > 0"
            type="warning"
            size="small"
            @click="handleBatchDisable"
          >
            批量禁用
          </el-button>
        </div>
      </div>
      
      <el-table
        v-loading="loading"
        :data="userList.content"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="username" label="用户名" min-width="120">
          <template slot-scope="scope">
            <div class="user-info">
              <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
              <span class="username">{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱" min-width="180" />
        
        <el-table-column prop="roles" label="角色" min-width="120">
          <template slot-scope="scope">
            <el-tag
              v-for="role in scope.row.roles"
              :key="role.id"
              :type="role.name === 'ADMIN' ? 'danger' : 'primary'"
              size="small"
              style="margin-right: 4px;"
            >
              {{ role.name }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="注册时间" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="lastLoginAt" label="最后登录" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.lastLoginAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="handleViewUser(scope.row)"
            >
              查看
            </el-button>
            
            <el-button
              v-if="scope.row.status === 1"
              type="text"
              size="small"
              @click="handleDisableUser(scope.row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="text"
              size="small"
              @click="handleEnableUser(scope.row)"
            >
              启用
            </el-button>
            
            <el-button
              type="text"
              size="small"
              @click="handleManageRoles(scope.row)"
            >
              角色
            </el-button>
            
            <el-button
              type="text"
              size="small"
              style="color: #f56c6c;"
              @click="handleDeleteUser(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="pagination.currentPage + 1"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          :total="pagination.totalElements"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 用户详情对话框 -->
    <user-detail-dialog
      :visible.sync="userDetailVisible"
      :user="selectedUser"
    />
    
    <!-- 角色管理对话框 -->
    <user-role-dialog
      :visible.sync="userRoleVisible"
      :user="selectedUser"
      @success="handleRefresh"
    />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PageHeader from '@/components/common/PageHeader'
import UserDetailDialog from './components/UserDetailDialog'
import UserRoleDialog from './components/UserRoleDialog'
import { getRoleList } from '@/api/role'

export default {
  name: 'UserList',
  components: {
    PageHeader,
    UserDetailDialog,
    UserRoleDialog
  },
  data() {
    return {
      searchForm: {
        search: '',
        status: null,
        roleId: null
      },
      selectedUsers: [],
      selectedUser: null,
      userDetailVisible: false,
      userRoleVisible: false,
      roleList: [],
      statistics: {
        totalUsers: 0,
        enabledUsers: 0,
        disabledUsers: 0,
        enabledPercentage: 0
      }
    }
  },
  computed: {
    ...mapGetters(['userList', 'loading']),
    
    pagination() {
      return {
        currentPage: this.userList.currentPage,
        pageSize: this.userList.pageSize,
        totalElements: this.userList.totalElements,
        totalPages: this.userList.totalPages
      }
    }
  },
  created() {
    this.fetchData()
    this.fetchRoles()
    this.fetchStatistics()
  },
  methods: {
    async fetchData() {
      const params = {
        page: this.pagination.currentPage,
        size: this.pagination.pageSize,
        ...this.searchForm
      }
      
      // 过滤空值
      Object.keys(params).forEach(key => {
        if (params[key] === null || params[key] === '') {
          delete params[key]
        }
      })
      
      await this.$store.dispatch('user/fetchUserList', params)
    },
    
    async fetchRoles() {
      try {
        const response = await getRoleList()
        if (response.data.success) {
          this.roleList = response.data.data
        }
      } catch (error) {
        console.error('获取角色列表失败:', error)
      }
    },
    
    async fetchStatistics() {
      const result = await this.$store.dispatch('user/fetchUserStatistics')
      if (result.success) {
        this.statistics = result.data
      }
    },
    
    handleSearch() {
      this.pagination.currentPage = 0
      this.fetchData()
    },
    
    handleReset() {
      this.searchForm = {
        search: '',
        status: null,
        roleId: null
      }
      this.handleSearch()
    },
    
    handleRefresh() {
      this.fetchData()
      this.fetchStatistics()
    },
    
    handleSizeChange(val) {
      this.$store.commit('user/SET_USER_LIST', {
        ...this.userList,
        pageSize: val,
        currentPage: 0
      })
      this.fetchData()
    },
    
    handleCurrentChange(val) {
      this.$store.commit('user/SET_USER_LIST', {
        ...this.userList,
        currentPage: val - 1
      })
      this.fetchData()
    },
    
    handleSelectionChange(selection) {
      this.selectedUsers = selection
    },
    
    handleViewUser(user) {
      this.selectedUser = user
      this.userDetailVisible = true
    },
    
    async handleEnableUser(user) {
      try {
        const result = await this.$store.dispatch('user/updateUserStatus', {
          userId: user.id,
          status: 1
        })
        
        if (result.success) {
          this.$message.success(result.message)
          this.fetchStatistics()
        } else {
          this.$message.error(result.message)
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    
    async handleDisableUser(user) {
      try {
        await this.$confirm(`确定要禁用用户 "${user.username}" 吗？`, '确认操作', {
          type: 'warning'
        })
        
        const result = await this.$store.dispatch('user/updateUserStatus', {
          userId: user.id,
          status: 0
        })
        
        if (result.success) {
          this.$message.success(result.message)
          this.fetchStatistics()
        } else {
          this.$message.error(result.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }
    },
    
    async handleBatchEnable() {
      try {
        const userIds = this.selectedUsers.map(user => user.id)
        const result = await this.$store.dispatch('user/batchUpdateUserStatus', {
          userIds,
          status: 1
        })
        
        if (result.success) {
          this.$message.success(result.message)
          this.selectedUsers = []
          this.fetchStatistics()
        } else {
          this.$message.error(result.message)
        }
      } catch (error) {
        this.$message.error('批量操作失败')
      }
    },
    
    async handleBatchDisable() {
      try {
        await this.$confirm(`确定要禁用选中的 ${this.selectedUsers.length} 个用户吗？`, '确认操作', {
          type: 'warning'
        })
        
        const userIds = this.selectedUsers.map(user => user.id)
        const result = await this.$store.dispatch('user/batchUpdateUserStatus', {
          userIds,
          status: 0
        })
        
        if (result.success) {
          this.$message.success(result.message)
          this.selectedUsers = []
          this.fetchStatistics()
        } else {
          this.$message.error(result.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('批量操作失败')
        }
      }
    },
    
    handleManageRoles(user) {
      this.selectedUser = user
      this.userRoleVisible = true
    },
    
    async handleDeleteUser(user) {
      try {
        await this.$confirm(`确定要删除用户 "${user.username}" 吗？此操作不可恢复！`, '确认删除', {
          type: 'error'
        })
        
        const result = await this.$store.dispatch('user/deleteUser', user.id)
        
        if (result.success) {
          this.$message.success(result.message)
          this.fetchStatistics()
        } else {
          this.$message.error(result.message)
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
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
.search-card {
  margin-bottom: 16px;
}

.search-form {
  .el-form-item {
    margin-bottom: 0;
  }
}

.stats-row {
  margin-bottom: 16px;
}

.stats-card {
  .el-card__body {
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.stats-content {
  .stats-number {
    font-size: 24px;
    font-weight: 600;
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

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  
  .username {
    margin-left: 8px;
  }
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

// 响应式设计
@media (max-width: 768px) {
  .stats-row {
    .el-col {
      margin-bottom: 16px;
    }
  }
  
  .search-form {
    .el-form-item {
      display: block;
      margin-bottom: 16px;
    }
  }
  
  .table-actions {
    margin-top: 16px;
  }
}
</style>