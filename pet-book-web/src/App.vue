<script setup lang="ts">
import MainLayout from './components/MainLayout.vue'
import PostDetailModal from './components/PostDetailModal.vue'
</script>

<template>
  <MainLayout />
  <PostDetailModal />
</template>

<style lang="scss">
*,
*::before,
*::after {
  box-sizing: border-box;
}

body {
  margin: 0;
  background: #f5f5f5;
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
