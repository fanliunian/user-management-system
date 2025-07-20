<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-image">
        <i class="el-icon-warning-outline error-icon"></i>
      </div>
      <h1 class="error-title">403</h1>
      <h2 class="error-subtitle">权限不足</h2>
      <p class="error-description">
        抱歉，您没有权限访问此页面。<br>
        请联系管理员获取相应权限。
      </p>
      <div class="error-actions">
        <el-button type="primary" @click="goBack">返回上页</el-button>
        <el-button @click="goHome">回到首页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Error403',
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: $bg-color;
}

.error-content {
  text-align: center;
  max-width: 500px;
  padding: 40px;
}

.error-image {
  margin-bottom: 24px;
}

.error-icon {
  font-size: 120px;
  color: $warning-color;
}

.error-title {
  font-size: 72px;
  font-weight: 700;
  color: $text-primary;
  margin: 0 0 16px 0;
}

.error-subtitle {
  font-size: 24px;
  font-weight: 600;
  color: $text-regular;
  margin: 0 0 16px 0;
}

.error-description {
  font-size: 16px;
  color: $text-secondary;
  line-height: 1.6;
  margin: 0 0 32px 0;
}

.error-actions {
  .el-button + .el-button {
    margin-left: 16px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .error-content {
    padding: 20px;
  }
  
  .error-icon {
    font-size: 80px;
  }
  
  .error-title {
    font-size: 48px;
  }
  
  .error-subtitle {
    font-size: 20px;
  }
  
  .error-actions {
    .el-button {
      display: block;
      width: 100%;
      margin: 0 0 12px 0 !important;
    }
  }
}
</style>