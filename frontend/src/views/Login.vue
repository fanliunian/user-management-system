<template>
  <div class="login-container">
    <div class="login-form-container">
      <div class="login-header">
        <h2 class="login-title">用户管理系统</h2>
        <p class="login-subtitle">请登录您的账户</p>
      </div>
      
      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        auto-complete="on"
        label-position="left"
      >
        <el-form-item prop="username">
          <el-input
            ref="username"
            v-model="loginForm.username"
            placeholder="请输入用户名"
            name="username"
            type="text"
            tabindex="1"
            auto-complete="on"
            prefix-icon="el-icon-user"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            ref="password"
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="请输入密码"
            name="password"
            tabindex="2"
            auto-complete="on"
            prefix-icon="el-icon-lock"
            size="large"
            @keyup.enter.native="handleLogin"
          >
            <i
              slot="suffix"
              :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-hide'"
              class="show-pwd"
              @click="showPwd"
            />
          </el-input>
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          size="large"
          style="width: 100%; margin-bottom: 30px;"
          @click.native.prevent="handleLogin"
        >
          {{ loading ? '登录中...' : '登录' }}
        </el-button>

        <div class="login-footer">
          <span>还没有账户？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { usernameRules, passwordRules } from '@/utils/validation'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: usernameRules,
        password: passwordRules
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    
    handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const result = await this.$store.dispatch('auth/login', this.loginForm)
            
            if (result.success) {
              this.$message.success('登录成功')
              this.$router.push({ path: this.redirect || '/' })
            } else {
              this.$message.error(result.message || '登录失败')
            }
          } catch (error) {
            console.error('登录错误:', error)
            this.$message.error('登录失败，请稍后重试')
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
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-form-container {
  width: 100%;
  max-width: 400px;
  background: $white-color;
  border-radius: $border-radius-large;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 8px 0;
}

.login-subtitle {
  font-size: 14px;
  color: $text-secondary;
  margin: 0;
}

.login-form {
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

.login-footer {
  text-align: center;
  font-size: 14px;
  color: $text-secondary;
  
  .register-link {
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
  .login-form-container {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 24px;
  }
}
</style>