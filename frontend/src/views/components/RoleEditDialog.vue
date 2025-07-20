<template>
  <el-dialog
    :title="isEdit ? '编辑角色' : '新建角色'"
    :visible.sync="dialogVisible"
    width="500px"
    @close="handleClose"
  >
    <el-form
      ref="roleForm"
      :model="roleForm"
      :rules="roleRules"
      label-width="100px"
    >
      <el-form-item label="角色名称" prop="name">
        <el-input
          v-model="roleForm.name"
          placeholder="请输入角色名称"
          :disabled="isEdit && isSystemRole"
        />
        <div v-if="isSystemRole" class="form-hint">
          系统内置角色，不允许修改名称
        </div>
      </el-form-item>
      
      <el-form-item label="角色描述" prop="description">
        <el-input
          v-model="roleForm.description"
          type="textarea"
          :rows="3"
          placeholder="请输入角色描述"
        />
      </el-form-item>
    </el-form>
    
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSave">
        {{ isEdit ? '保存' : '创建' }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { createRole, updateRole } from '@/api/role'
import { roleNameRules, roleDescriptionRules } from '@/utils/validation'

export default {
  name: 'RoleEditDialog',
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
      roleForm: {
        name: '',
        description: ''
      },
      roleRules: {
        name: roleNameRules,
        description: roleDescriptionRules
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
    },
    
    isEdit() {
      return !!this.role
    },
    
    isSystemRole() {
      return this.role && ['ADMIN', 'USER'].includes(this.role.name)
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.initForm()
      }
    }
  },
  methods: {
    initForm() {
      if (this.role) {
        this.roleForm = {
          name: this.role.name,
          description: this.role.description || ''
        }
      } else {
        this.roleForm = {
          name: '',
          description: ''
        }
      }
      
      this.$nextTick(() => {
        if (this.$refs.roleForm) {
          this.$refs.roleForm.clearValidate()
        }
      })
    },
    
    async handleSave() {
      this.$refs.roleForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            let response
            if (this.isEdit) {
              response = await updateRole(this.role.id, this.roleForm)
            } else {
              response = await createRole(this.roleForm)
            }
            
            if (response.data.success) {
              this.$message.success(this.isEdit ? '角色更新成功' : '角色创建成功')
              this.$emit('success')
              this.handleClose()
            } else {
              this.$message.error(response.data.message || '操作失败')
            }
          } catch (error) {
            console.error('保存角色失败:', error)
            this.$message.error(error.response?.data?.message || '操作失败')
          } finally {
            this.loading = false
          }
        }
      })
    },
    
    handleClose() {
      this.dialogVisible = false
      this.roleForm = {
        name: '',
        description: ''
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.form-hint {
  font-size: 12px;
  color: $text-secondary;
  margin-top: 4px;
}

.dialog-footer {
  text-align: right;
}
</style>