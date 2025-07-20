<template>
  <div class="page-container">
    <page-header title="个人资料" description="查看和编辑您的个人信息" />
    
    <div class="profile-container">
      <el-row :gutter="24">
        <!-- 个人信息卡片 -->
        <el-col :lg="8" :md="24">
          <el-card class="profile-card">
            <div class="profile-avatar">
              <el-avatar :size="80" icon="el-icon-user-solid"></el-avatar>
              <h3 class="profile-name">{{ userInfo.username }}</h3>
              <p class="profile-email">{{ userInfo.email }}</p>
              <div class="profile-roles">
                <el-tag
                  v-for="role in userInfo.roles"
                  :key="role.id"
                  :type="role.name === 'ADMIN' ? 'danger' : 'primary'"
                  size="small"
                >
                  {{ role.name }}
                </el-tag>
              </div>
            </div>
            
            <div class="profile-info">
              <div class="info-item">
                <span class="info-label">注册时间</span>
                <span class="info-value">{{ formatDate(userInfo.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">最后登录</span>
                <span class="info-value">{{ formatDate(userInfo.lastLoginAt) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">账户状态</span>
                <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'" size="small">
                  {{ userInfo.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 编辑表单 -->
        <el-col :lg="16" :md="24">
          <el-card>
            <div slot="header" class="card-header">
              <span>编辑个人信息</span>
            </div>
            
            <el-form
              ref="profileForm"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="profileForm.username"
                  placeholder="请输入用户名"
                  @blur="checkUsername"
                />
                <div v-if="usernameCheckResult" class="field-hint" :class="usernameCheckResult.type">
                  {{ usernameCheckResult.message }}
                </div>
              </el-form-item>
              
              <el-form-item label="邮箱地址" prop="email">
                <el-input
                  v-model="profileForm.email"
                  placeholder="请输入邮箱地址"
                  @blur="checkEmail"
                />
                <div v-if="emailCheckResult" class="field-hint" :class="emailCheckResult.type">
                  {{ emailCheckResult.message }}
                </div>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleUpdateProfile" :loading="updateLoading">
                  保存修改
                </el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
          
          <!-- 修改密码 -->
          <el-card style="margin-top: 24px;">
            <div slot="header" class="card-header">
              <span>修改密码</span>
            </div>
            
            <el-form
              ref="passwordForm"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="password-form"
            >
              <el-form-item label="当前密码" prop="currentPassword">
                <el-input
                  v-model="passwordForm.currentPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                  @input="checkPasswordStrength"
                />
                <div v-if="passwordStrength" class="password-strength">
                  <div class="strength-bar">
                    <div 
                      class="strength-fill" 
                      :class="passwordStrength.level"
                      :style="{ width: passwordStrength.percentage + '%' }"
                    ></div>
                  </div>
                  <span class="strength-text" :class="passwordStrength.level">
                    {{ passwordStrength.text }}
                  </span>
                </div>
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请确认新密码"
                  show-password
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                  修改密码
                </el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PageHeader from '@/components/common/PageHeader'
import { updateUserProfile, changePassword, checkUsername, checkEmail } from '@/api/auth'
import { usernameRules, emailRules, passwordRules, confirmPasswordRules } from '@/utils/validation'
import { validatePassword } from '@/utils/validation'

export default {
  name: 'Profile',
  components: {
    PageHeader
  },
  data() {
    return {
      profileForm: {
        username: '',
        email: ''
      },
      profileRules: {
        username: usernameRules,
        email: emailRules
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        currentPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: passwordRules,
        confirmPassword: confirmPasswordRules(() => this.passwordForm.newPassword)
      },
      updateLoading: false,
      passwordLoading: false,
      usernameCheckResult: null,
      emailCheckResult: null,
      passwordStrength: null
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created() {
    this.initForm()
  },
  methods: {
    initForm() {
      if (this.userInfo) {
        this.profileForm = {
          username: this.userInfo.username,
          email: this.userInfo.email
        }
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    },
    
    async checkUsername() {
      if (!this.profileForm.username || this.profileForm.username === this.userInfo.username) {
        this.usernameCheckResult = null
        return
      }
      
      try {
        const response = await checkUsername(this.profileForm.username, this.userInfo.id)
        if (response.data.success) {
          const available = response.data.data
          this.usernameCheckResult = {
            type: available ? 'success' : 'error',
            message: available ? '用户名可用' : '用户名已存在'
          }
        }
      } catch (error) {
        this.usernameCheckResult = {
          type: 'error',
          message: '检查用户名失败'
        }
      }
    },
    
    async checkEmail() {
      if (!this.profileForm.email || this.profileForm.email === this.userInfo.email) {
        this.emailCheckResult = null
        return
      }
      
      try {
        const response = await checkEmail(this.profileForm.email, this.userInfo.id)
        if (response.data.success) {
          const available = response.data.data
          this.emailCheckResult = {
            type: available ? 'success' : 'error',
            message: available ? '邮箱可用' : '邮箱已被注册'
          }
        }
      } catch (error) {
        this.emailCheckResult = {
          type: 'error',
          message: '检查邮箱失败'
        }
      }
    },
    
    checkPasswordStrength() {
      const password = this.passwordForm.newPassword
      if (!password) {
        this.passwordStrength = null
        return
      }
      
      const result = validatePassword(password)
      if (result.valid) {
        let score = 0
        let level = 'weak'
        let text = '弱'
        let percentage = 25
        
        if (password.length >= 8) score += 25
        if (password.length >= 12) score += 25
        if (/[a-z]/.test(password)) score += 10
        if (/[A-Z]/.test(password)) score += 10
        if (/\d/.test(password)) score += 15
        if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score += 15
        
        if (score >= 80) {
          level = 'strong'
          text = '强'
          percentage = 100
        } else if (score >= 60) {
          level = 'medium'
          text = '中'
          percentage = 75
        } else if (score >= 40) {
          level = 'fair'
          text = '一般'
          percentage = 50
        }
        
        this.passwordStrength = { level, text, percentage }
      } else {
        this.passwordStrength = {
          level: 'weak',
          text: result.message,
          percentage: 25
        }
      }
    },
    
    handleUpdateProfile() {
      this.$refs.profileForm.validate(async (valid) => {
        if (valid) {
          if (this.usernameCheckResult && this.usernameCheckResult.type === 'error') {
            this.$message.error('用户名不可用')
            return
          }
          
          if (this.emailCheckResult && this.emailCheckResult.type === 'error') {
            this.$message.error('邮箱不可用')
            return
          }
          
          this.updateLoading = true
          try {
            const response = await updateUserProfile(this.profileForm)
            if (response.data.success) {
              this.$message.success('个人信息更新成功')
              // 更新store中的用户信息
              await this.$store.dispatch('auth/getUserInfo')
              this.usernameCheckResult = null
              this.emailCheckResult = null
            }
          } catch (error) {
            this.$message.error(error.response?.data?.message || '更新失败')
          } finally {
            this.updateLoading = false
          }
        }
      })
    },
    
    handleChangePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.passwordLoading = true
          try {
            const response = await changePassword({
              currentPassword: this.passwordForm.currentPassword,
              newPassword: this.passwordForm.newPassword
            })
            
            if (response.data.success) {
              this.$message.success('密码修改成功')
              this.resetPasswordForm()
            }
          } catch (error) {
            this.$message.error(error.response?.data?.message || '密码修改失败')
          } finally {
            this.passwordLoading = false
          }
        }
      })
    },
    
    resetForm() {
      this.initForm()
      this.usernameCheckResult = null
      this.emailCheckResult = null
      this.$refs.profileForm.clearValidate()
    },
    
    resetPasswordForm() {
      this.passwordForm = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.passwordStrength = null
      this.$refs.passwordForm.clearValidate()
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  .el-card {
    margin-bottom: 24px;
  }
}

.profile-card {
  .profile-avatar {
    text-align: center;
    padding: 20px 0;
    border-bottom: 1px solid $border-lighter;
    margin-bottom: 20px;
    
    .profile-name {
      margin: 16px 0 8px 0;
      font-size: 20px;
      font-weight: 600;
      color: $text-primary;
    }
    
    .profile-email {
      margin: 0 0 16px 0;
      color: $text-secondary;
      font-size: 14px;
    }
    
    .profile-roles {
      .el-tag + .el-tag {
        margin-left: 8px;
      }
    }
  }
  
  .profile-info {
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
}

.card-header {
  font-weight: 600;
  color: $text-primary;
}

.field-hint {
  font-size: 12px;
  margin-top: 4px;
  
  &.success {
    color: $success-color;
  }
  
  &.error {
    color: $danger-color;
  }
}

.password-strength {
  margin-top: 8px;
}

.strength-bar {
  height: 4px;
  background-color: $border-lighter;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  
  &.weak {
    background-color: $danger-color;
  }
  
  &.fair {
    background-color: $warning-color;
  }
  
  &.medium {
    background-color: #f39c12;
  }
  
  &.strong {
    background-color: $success-color;
  }
}

.strength-text {
  font-size: 12px;
  
  &.weak {
    color: $danger-color;
  }
  
  &.fair {
    color: $warning-color;
  }
  
  &.medium {
    color: #f39c12;
  }
  
  &.strong {
    color: $success-color;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-form,
  .password-form {
    .el-form-item {
      .el-form-item__label {
        width: 80px !important;
      }
    }
  }
}
</style>