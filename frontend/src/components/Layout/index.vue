<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <sidebar 
      :class="{ 'sidebar-hide': !sidebar.opened }"
      class="sidebar-container"
    />
    
    <!-- 主内容区域 -->
    <div class="main-container" :class="{ 'sidebar-hide': !sidebar.opened }">
      <!-- 顶部导航栏 -->
      <div class="fixed-header">
        <navbar />
      </div>
      
      <!-- 页面内容 -->
      <app-main />
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Sidebar from './components/Sidebar'
import Navbar from './components/Navbar'
import AppMain from './components/AppMain'

export default {
  name: 'Layout',
  components: {
    Sidebar,
    Navbar,
    AppMain
  },
  computed: {
    ...mapGetters(['sidebar'])
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
  
  &::after {
    content: '';
    display: table;
    clear: both;
  }
}

.sidebar-container {
  transition: width 0.28s;
  width: $sidebar-width !important;
  background-color: $white-color;
  height: 100%;
  position: fixed;
  font-size: 0;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1001;
  overflow: hidden;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  
  &.sidebar-hide {
    width: $sidebar-collapsed-width !important;
  }
}

.main-container {
  min-height: 100%;
  transition: margin-left 0.28s;
  margin-left: $sidebar-width;
  position: relative;
  
  &.sidebar-hide {
    margin-left: $sidebar-collapsed-width;
  }
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$sidebar-width});
  transition: width 0.28s;
  
  .main-container.sidebar-hide & {
    width: calc(100% - #{$sidebar-collapsed-width});
  }
}

// 响应式设计
@media (max-width: 768px) {
  .sidebar-container {
    transition: transform 0.28s;
    width: $sidebar-width !important;
    
    &.sidebar-hide {
      pointer-events: none;
      transition-duration: 0.3s;
      transform: translate3d(-#{$sidebar-width}, 0, 0);
      width: $sidebar-width !important;
    }
  }
  
  .main-container {
    margin-left: 0;
    
    &.sidebar-hide {
      margin-left: 0;
    }
  }
  
  .fixed-header {
    width: 100%;
    
    .main-container.sidebar-hide & {
      width: 100%;
    }
  }
}
</style>