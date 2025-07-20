<template>
  <div class="register-container">
    <div class="register-form-container">
      <div class="register-header">
        <h2 class="register-title">创建新账户</h2>
        <p class="register-subtitle">请填写以下信息完成注册</p>
      </div>
      
      <el-form
        ref="registerForm"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="left"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large"
            @blur="checkUsername"
          />
          <div v-if="usernameCheckResult" class="field-hint" :class="usernameCheckResult.type">
            {{ usernameCheckResult.message }}
          </div>
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱地址"
            prefix-icon="el-icon-message"
            size="large"
            @blur="checkEmail"
          />
          <div v-if="emailCheckResult" class="field-hint" :class="emailCheckResult.type">
            {{ emailCheckResult.message }}
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            :type="passwordType"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            @input="checkPasswordStrength"
          >
            <i
              slot="suffix"
              :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-hide'"
              class="show-pwd"
              @click="showPwd"
            />
          </el-input>
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

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="el-icon-lock"
            size="large"
          />
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          size="large"
          style="width: 100%; margin-bottom: 30px;"
          @click="handleRegister"
        >
          {{ loading ? '注册中...' : '注册' }}
        </el-button>

        <div class="register-footer">
          <span>已有账户？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { usernameRules, emailRules, passwordRules, confirmPasswordRules } from '@/utils/validation'
import { checkUsername, checkEmail } from '@/api/auth'
import { validatePassword } from '@/utils/validation'

export default {
  name: 'Register',
  data() {
    // 创建自定义的确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      registerForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      registerRules: {
        username: usernameRules,
        email: emailRules,
        password: passwordRules,
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      loading: false,
      passwordType: 'password',
      usernameCheckResult: null,
      emailCheckResult: null,
      passwordStrength: null
    }
  },
  watch: {
    // 监听密码变化，当密码变化且确认密码已填写时，重新验证确认密码
    'registerForm.password': function(val) {
      if (this.registerForm.confirmPassword) {
        this.$refs.registerForm.validateField('confirmPassword');
      }
    }
  },
  methods: {
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
    },
    
    async checkUsername() {
      if (!this.registerForm.username) {
        this.usernameCheckResult = null
        return
      }
      
      try {
        const response = await checkUsername(this.registerForm.username)
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
      if (!this.registerForm.email) {
        this.emailCheckResult = null
        return
      }
      
      try {
        const response = await checkEmail(this.registerForm.email)
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
      const password = this.registerForm.password
      if (!password) {
        this.passwordStrength = null
        return
      }
      
      const result = validatePassword(password)
      if (result.valid) {
        // 计算密码强度
        let score = 0
        let level = 'weak'
        let text = '弱'
        let percentage = 25
        
        // 长度检查
        if (password.length >= 8) score += 25
        if (password.length >= 12) score += 25
        
        // 字符类型检查
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
        
        this.passwordStrength = {
          level,
          text,
          percentage
        }
      } else {
        this.passwordStrength = {
          level: 'weak',
          text: result.message,
          percentage: 25
        }
      }
    },
    
    handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          // 检查用户名和邮箱是否可用
          if (this.usernameCheckResult && this.usernameCheckResult.type === 'error') {
            this.$message.error('用户名不可用')
            return
          }
          
          if (this.emailCheckResult && this.emailCheckResult.type === 'error') {
            this.$message.error('邮箱不可用')
            return
          }
          
          this.loading = true
          try {
            const result = await this.$store.dispatch('auth/register', {
              username: this.registerForm.username,
              email: this.registerForm.email,
              password: this.registerForm.password
            })
            
            if (result.success) {
              this.$message.success('注册成功，请登录')
              this.$router.push('/login')
            } else {
              this.$message.error(result.message || '注册失败')
            }
          } catch (error) {
            console.error('注册错误:', error)
            this.$message.error('注册失败，请稍后重试')
          } finally {
            this.loading = false
          }
        } else {
          console.log('表单验证失败')
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-form-container {
  width: 100%;
  max-width: 450px;
  background: $white-color;
  border-radius: $border-radius-large;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-title {
  font-size: 28px;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 8px 0;
}

.register-subtitle {
  font-size: 14px;
  color: $text-secondary;
  margin: 0;
}

.register-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .el-input {
    .el-input__inner {
      height: 48px;
      line-height: 48px;
      border-radius: $border-radius-base;
      border: 1px solid $border-light;
      
      &:focus {
        border-color: $primary-color;
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
      }
    }
  }
}

.show-pwd {
  cursor: pointer;
  user-select: none;
  color: $text-placeholder;
  
  &:hover {
    color: $primary-color;
  }
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

.register-footer {
  text-align: center;
  font-size: 14px;
  color: $text-secondary;
  
  .login-link {
    color: $primary-color;
    text-decoration: none;
    margin-left: 4px;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

// 响应式设计
@media (max-width: 480px) {
  .register-form-container {
    padding: 30px 20px;
  }
  
  .register-title {
    font-size: 24px;
  }
}
</style>