<script setup lang="ts">
import MainLayout from './components/MainLayout.vue'
import PostDetailModal from './components/PostDetailModal.vue'
</script>

<template>
  <MainLayout />
  <PostDetailModal />
</template>

<style lang="scss">
/* 全局主题：温馨黄色 */
:root {
  --primary:rgb(255, 229, 62);
  --primary-light: rgba(230, 162, 60, 0.12);
  --primary-border: rgba(230, 162, 60, 0.35);
}

*,
*::before,
*::after {
  box-sizing: border-box;
}

body {
  margin: 0;
  background: #f8f6f1;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  font-size: 14px;
  color: #333;
}

a {
  color: inherit;
  text-decoration: none;
}
</style>
