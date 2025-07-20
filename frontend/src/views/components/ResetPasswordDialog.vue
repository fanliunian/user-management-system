<template>
  <el-dialog
    title="重置密码"
    :visible.sync="dialogVisible"
    width="400px"
    @close="handleClose"
  >
    <div v-if="user" class="reset-password-content">
      <p class="user-info">
        用户: <strong>{{ user.username }}</strong>
      </p>
      
      <el-form
        ref="passwordForm"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="0"
      >
        <el-form-item prop="password">
          <el-input
            v-model="passwordForm.password"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <div class="password-tips">
        <p><i class="el-icon-warning-outline"></i> 密码重置后将立即生效，请妥善保管新密码。</p>
      </div>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认重置</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { passwordRules } from '@/utils/validation'
import { resetUserPassword } from '@/api/user'

export default {
  name: 'ResetPasswordDialog',
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
    // 创建自定义的确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.password) {
        callback(new Error('两次输入密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      passwordForm: {
        password: '',
        confirmPassword: ''
      },
      passwordRules: {
        password: passwordRules,
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
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
  methods: {
    handleClose() {
      this.resetForm()
      this.dialogVisible = false
    },
    
    resetForm() {
      this.passwordForm = {
        password: '',
        confirmPassword: ''
      }
      
      if (this.$refs.passwordForm) {
        this.$refs.passwordForm.resetFields()
      }
    },
    
    async handleSubmit() {
      if (!this.user) {
        this.$message.error('用户信息不存在')
        return
      }
      
      this.$refs.passwordForm.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        this.loading = true
        
        try {
          const response = await resetUserPassword(this.user.id, {
            password: this.passwordForm.password
          })
          
          if (response.data.success) {
            this.$message.success('密码重置成功')
            this.handleClose()
            this.$emit('success')
          } else {
            this.$message.error(response.data.message || '密码重置失败')
          }
        } catch (error) {
          console.error('密码重置失败:', error)
          this.$message.error(error.response?.data?.message || '密码重置失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.reset-password-content {
  .user-info {
    margin-bottom: 20px;
    font-size: 14px;
    
    strong {
      color: $text-primary;
    }
  }
  
  .password-tips {
    margin-top: 20px;
    padding: 10px;
    background-color: #fdf6ec;
    border-radius: 4px;
    
    p {
      margin: 0;
      font-size: 12px;
      color: #e6a23c;
      display: flex;
      align-items: center;
      
      i {
        margin-right: 5px;
      }
    }
  }
}

.dialog-footer {
  text-align: right;
}
</style>