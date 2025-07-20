<template>
  <div class="loading-spinner" :class="{ 'full-screen': fullScreen }">
    <div class="spinner-container">
      <div class="spinner">
        <div class="bounce1"></div>
        <div class="bounce2"></div>
        <div class="bounce3"></div>
      </div>
      <p v-if="text" class="loading-text">{{ text }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoadingSpinner',
  props: {
    fullScreen: {
      type: Boolean,
      default: false
    },
    text: {
      type: String,
      default: '加载中...'
    }
  }
}
</script>

<style lang="scss" scoped>
.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: $padding-large;
  
  &.full-screen {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(255, 255, 255, 0.9);
    z-index: 9999;
  }
}

.spinner-container {
  text-align: center;
}

.spinner {
  width: 70px;
  text-align: center;
  margin: 0 auto 16px;
}

.spinner > div {
  width: 18px;
  height: 18px;
  background-color: $primary-color;
  border-radius: 100%;
  display: inline-block;
  animation: sk-bouncedelay 1.4s infinite ease-in-out both;
}

.spinner .bounce1 {
  animation-delay: -0.32s;
}

.spinner .bounce2 {
  animation-delay: -0.16s;
}

.loading-text {
  font-size: 14px;
  color: $text-secondary;
  margin: 0;
}

@keyframes sk-bouncedelay {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1.0);
  }
}
</style>