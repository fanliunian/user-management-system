<template>
  <div class="page-header">
    <div class="page-header-content">
      <div class="page-header-left">
        <h1 class="page-title">{{ title }}</h1>
        <p v-if="description" class="page-description">{{ description }}</p>
      </div>
      <div v-if="$slots.extra" class="page-header-extra">
        <slot name="extra"></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PageHeader',
  props: {
    title: {
      type: String,
      required: true
    },
    description: {
      type: String,
      default: ''
    }
  }
}
</script>

<style lang="scss" scoped>
.page-header {
  background-color: $white-color;
  padding: $padding-large;
  margin-bottom: $padding-base;
  border-radius: $border-radius-base;
  box-shadow: $box-shadow-base;
}

.page-header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.page-header-left {
  flex: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: $text-primary;
  margin: 0 0 8px 0;
}

.page-description {
  font-size: 14px;
  color: $text-secondary;
  margin: 0;
  line-height: 1.5;
}

.page-header-extra {
  margin-left: $padding-base;
}

// 响应式设计
@media (max-width: 768px) {
  .page-header {
    padding: $padding-base;
  }
  
  .page-header-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .page-header-extra {
    margin-left: 0;
    margin-top: $padding-base;
  }
  
  .page-title {
    font-size: 20px;
  }
}
</style>