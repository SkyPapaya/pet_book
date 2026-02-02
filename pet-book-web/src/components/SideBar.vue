<script setup lang="ts">
import { ref } from 'vue'

const activeMenu = ref('discover') // 默认选中"发现"

const menus = [
  { key: 'discover', label: '发现', icon: '🏠' },
  { key: 'publish', label: '发布', icon: '➕' },
  { key: 'notification', label: '通知', icon: '🔔' },
  { key: 'me', label: '我', icon: '👤' },
]
</script>

<template>
  <aside class="sidebar">
    <div class="logo">
      <span class="logo-text">小红书</span>
    </div>

    <nav class="menu-list">
      <div
        v-for="item in menus"
        :key="item.key"
        class="menu-item"
        :class="{ active: activeMenu === item.key }"
        @click="activeMenu = item.key"
      >
        <span class="icon">{{ item.icon }}</span>
        <span class="label">{{ item.label }}</span>
      </div>
    </nav>

    <div class="sidebar-footer">
      <div class="menu-item">
        <span class="icon">☰</span>
        <span class="label">更多</span>
      </div>
    </div>
  </aside>
</template>

<style scoped lang="scss">
.sidebar {
  width: 240px; /* 小红书标准侧边栏宽度 */
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: #fff;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #f0f0f0;
  z-index: 10;
}

.logo {
  height: 72px;
  display: flex;
  align-items: center;
  padding-left: 12px;
  .logo-text {
    color: #e6a23c;
    font-size: 30px;
    font-weight: bold;
    font-family: serif; /* 模拟小红书Logo字体 */
  }
}

.menu-list {
  flex: 1;
  margin-top: 10px;
}

.menu-item {
  display: flex;
  align-items: center;
  height: 48px;
  padding-left: 12px;
  margin-bottom: 8px;
  border-radius: 24px;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #333;
  font-size: 16px;
  font-weight: 600;

  .icon {
    font-size: 20px;
    margin-right: 12px;
  }

  &:hover {
    background-color: #f5f5f5;
  }

  &.active {
    background-color: #e6a23c;
    color: white;
  }
}
</style>
