<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index === levelList.length - 1"
          class="no-redirect"
        >
          {{ item.meta.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">
          {{ item.meta.title }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
export default {
  name: 'Breadcrumb',
  data() {
    return {
      levelList: []
    }
  },
  watch: {
    $route: {
      handler(route) {
        this.getBreadcrumb()
      },
      immediate: true
    }
  },
  methods: {
    getBreadcrumb() {
      // 过滤掉没有meta.title的路由
      let matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      
      const first = matched[0]
      
      // 如果第一个不是仪表盘，则添加仪表盘作为首页
      if (!this.isDashboard(first)) {
        matched = [{ path: '/dashboard', meta: { title: '仪表盘' } }].concat(matched)
      }
      
      this.levelList = matched.filter(item => {
        return item.meta && item.meta.title && item.meta.breadcrumb !== false
      })
    },
    
    isDashboard(route) {
      const name = route && route.name
      if (!name) {
        return false
      }
      return name.trim().toLocaleLowerCase() === 'Dashboard'.toLocaleLowerCase()
    },
    
    handleLink(item) {
      const { redirect, path } = item
      if (redirect) {
        this.$router.push(redirect)
        return
      }
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: $header-height;
  margin-left: 8px;
  
  .no-redirect {
    color: $text-secondary;
    cursor: text;
  }
}

.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.5s;
}

.breadcrumb-enter,
.breadcrumb-leave-active {
  opacity: 0;
  transform: translateX(20px);
}

.breadcrumb-move {
  transition: all 0.5s;
}

.breadcrumb-leave-active {
  position: absolute;
}
</style>