<template>
  <el-dialog
    title="添加用户"
    :visible.sync="dialogVisible"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="userForm"
      :model="userForm"
      :rules="userRules"
      label-width="100px"
      label-position="left"
    >
      <el-form-item label="用户名" prop="username">
        <el-input 
          v-model="userForm.username" 
          placeholder="请输入用户名"
          @blur="checkUsername"
        />
        <div v-if="usernameCheckResult" class="field-hint" :class="usernameCheckResult.type">
          {{ usernameCheckResult.message }}
        </div>
      </el-form-item>
      
      <el-form-item label="邮箱" prop="email">
        <el-input 
          v-model="userForm.email" 
          placeholder="请输入邮箱地址"
          @blur="checkEmail"
        />
        <div v-if="emailCheckResult" class="field-hint" :class="emailCheckResult.type">
          {{ emailCheckResult.message }}
        </div>
      </el-form-item>
      
      <el-form-item label="密码" prop="password">
        <el-input 
          v-model="userForm.password" 
          type="password" 
          placeholder="请输入密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input 
          v-model="userForm.confirmPassword" 
          type="password" 
          placeholder="请确认密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="角色" prop="roles">
        <el-select
          v-model="userForm.roles"
          multiple
          placeholder="请选择角色"
          style="width: 100%"
        >
          <el-option
            v-for="role in roleList"
            :key="role.id"
            :label="role.name + (role.description ? ` (${role.description})` : '')"
            :value="role.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="状态">
        <el-switch
          v-model="userForm.status"
          :active-value="1"
          :inactive-value="0"
          active-text="启用"
          inactive-text="禁用"
        />
      </el-form-item>
    </el-form>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确认</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { usernameRules, emailRules, passwordRules, confirmPasswordRules } from '@/utils/validation'
import { checkUsername, checkEmail } from '@/api/auth'
import { getRoleList } from '@/api/role'
import { createUser } from '@/api/user'

export default {
  name: 'AddUserDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    // 创建自定义的确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.userForm.password) {
        callback(new Error('两次输入密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      userForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        roles: [],
        status: 1
      },
      userRules: {
        username: usernameRules,
        email: emailRules,
        password: passwordRules,
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        roles: [
          { required: true, message: '请至少选择一个角色', trigger: 'change' }
        ]
      },
      roleList: [],
      loading: false,
      usernameCheckResult: null,
      emailCheckResult: null
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
        this.fetchRoles()
      }
    }
  },
  methods: {
    async fetchRoles() {
      try {
        const response = await getRoleList()
        if (response.data.success) {
          this.roleList = response.data.data
        }
      } catch (error) {
        console.error('获取角色列表失败:', error)
        this.$message.error('获取角色列表失败')
      }
    },
    
    async checkUsername() {
      if (!this.userForm.username) {
        this.usernameCheckResult = null
        return
      }
      
      try {
        const response = await checkUsername(this.userForm.username)
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
      if (!this.userForm.email) {
        this.emailCheckResult = null
        return
      }
      
      try {
        const response = await checkEmail(this.userForm.email)
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
    
    handleClose() {
      this.resetForm()
      this.dialogVisible = false
    },
    
    resetForm() {
      this.userForm = {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        roles: [],
        status: 1
      }
      this.usernameCheckResult = null
      this.emailCheckResult = null
      
      if (this.$refs.userForm) {
        this.$refs.userForm.resetFields()
      }
    },
    
    async handleSubmit() {
      this.$refs.userForm.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
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
          const response = await createUser({
            username: this.userForm.username,
            email: this.userForm.email,
            password: this.userForm.password,
            roles: this.userForm.roles,
            status: this.userForm.status
          })
          
          if (response.data.success) {
            this.$message.success('用户添加成功')
            this.handleClose()
            this.$emit('success')
          } else {
            this.$message.error(response.data.message || '添加用户失败')
          }
        } catch (error) {
          console.error('添加用户失败:', error)
          this.$message.error(error.response?.data?.message || '添加用户失败')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
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

.dialog-footer {
  text-align: right;
}
</style>