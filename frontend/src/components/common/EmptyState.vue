<template>
  <div class="empty-state">
    <div class="empty-image">
      <i :class="icon" class="empty-icon"></i>
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p v-if="description" class="empty-description">{{ description }}</p>
    <div v-if="$slots.action" class="empty-action">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    icon: {
      type: String,
      default: 'el-icon-box'
    },
    title: {
      type: String,
      default: '暂无数据'
    },
    description: {
      type: String,
      default: ''
    }
  }
}
</script>

<style lang="scss" scoped>
.empty-state {
  text-align: center;
  padding: $padding-large * 2;
  color: $text-secondary;
}

.empty-image {
  margin-bottom: $padding-base;
}

.empty-icon {
  font-size: 64px;
  color: $text-placeholder;
}

.empty-title {
  font-size: 16px;
  font-weight: 500;
  color: $text-regular;
  margin: 0 0 8px 0;
}

.empty-description {
  font-size: 14px;
  color: $text-secondary;
  margin: 0 0 $padding-base 0;
  line-height: 1.5;
}

.empty-action {
  margin-top: $padding-base;
}
</style>