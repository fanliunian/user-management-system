const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  // 禁用ESLint
  lintOnSave: false,
  
  // 开发服务器配置
  devServer: {
    port: 8081,
    host: 'localhost',
    open: true,
    // 代理配置（可选，如果需要代理到后端）
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  },

  // 生产环境配置
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  outputDir: 'dist',
  assetsDir: 'static',

  // CSS配置
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@import "@/styles/variables.scss";`
      }
    }
  },

  // 链式操作配置
  chainWebpack: config => {
    // 设置别名
    config.resolve.alias
      .set('@', require('path').resolve(__dirname, 'src'))
      .set('components', require('path').resolve(__dirname, 'src/components'))
      .set('views', require('path').resolve(__dirname, 'src/views'))
      .set('utils', require('path').resolve(__dirname, 'src/utils'))
      .set('api', require('path').resolve(__dirname, 'src/api'))
      .set('store', require('path').resolve(__dirname, 'src/store'))
  }
})