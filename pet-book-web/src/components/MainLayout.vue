<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '../stores/app'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

const searchKeyword = ref('')
// 与 URL query.q 同步，便于从 /?q=xxx 进入时搜索框显示关键词
watch(() => route.query.q, (q) => {
  searchKeyword.value = (typeof q === 'string' ? q : '') || ''
}, { immediate: true })

// 侧边栏：发现、发布、通知（个人中心在右上角，不放在侧边栏）
const sideMenus = [
  { path: '/', name: '发现', icon: '🔍' },
  { path: '/publish', name: '发布', icon: '✏️' },
  { path: '/notification', name: '通知', icon: '🔔' },
]

function goTo(path: string) {
  router.push(path)
}

function onSearch() {
  if (!searchKeyword.value.trim()) return
  router.push({ path: '/', query: { q: searchKeyword.value } })
}

// 个人中心
function goProfile() {
  router.push('/profile')
}

// 悬浮按钮：刷新、回到顶部
const showBackTop = ref(false)
const mainRef = ref<HTMLElement | null>(null)

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function onRefresh() {
  // 由当前页面监听 refresh 事件或通过 event bus；这里用 reload 当前页
  window.dispatchEvent(new CustomEvent('feed-refresh'))
}

function onScroll() {
  showBackTop.value = window.scrollY > 400
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>

<template>
  <div class="main-layout">
    <!-- 左侧边栏：发现、发布、通知 -->
    <aside class="sidebar">
      <router-link to="/" class="logo">宠物书</router-link>
      <nav class="nav">
        <a
          v-for="item in sideMenus"
          :key="item.path"
          :href="item.path"
          class="nav-item"
          :class="{ active: route.path === item.path || (item.path === '/publish' && route.path.startsWith('/publish')) }"
          @click.prevent="goTo(item.path)"
        >
          <span class="icon">{{ item.icon }}</span>
          <span class="label">{{ item.name }}</span>
        </a>
      </nav>
    </aside>

    <!-- 右侧主区：顶栏 + 内容 -->
    <div class="main-area">
      <!-- 顶部：搜索框 + 个人中心 -->
      <header class="top-bar">
        <div class="search-wrap">
          <input
            v-model="searchKeyword"
            type="text"
            class="search-input"
            placeholder="搜索宠物、领养、求助..."
            @keyup.enter="onSearch"
          />
          <button type="button" class="search-btn" @click="onSearch">搜索</button>
        </div>
        <button type="button" class="btn-profile" @click="goProfile">
          <img
            v-if="appStore.user?.avatar"
            :src="appStore.user.avatar"
            alt=""
            class="avatar"
          />
          <span v-else class="avatar-placeholder">我</span>
          <span class="label">个人中心</span>
        </button>
      </header>

      <!-- 主内容 -->
      <main class="content" ref="mainRef">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </main>
    </div>

    <!-- 右下角悬浮：刷新、回到顶部 -->
    <div class="float-actions">
      <button
        v-show="showBackTop"
        type="button"
        class="float-btn"
        title="回到顶部"
        @click="scrollToTop"
      >
        ↑ 顶部
      </button>
      <button
        type="button"
        class="float-btn"
        title="刷新"
        @click="onRefresh"
      >
        🔄 刷新
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$primary: #e6a23c;
$text: #333;
$text2: #666;
$text3: #999;
$bg: #fff;
$border: #eee;

.main-layout {
  display: flex;
  min-height: 100vh;
  width: 100%;
  background: #f8f6f1;
}

.sidebar {
  width: 200px;
  flex-shrink: 0;
  background: $bg;
  border-right: 1px solid $border;
  padding: 20px 0;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 50;
}

.logo {
  display: block;
  padding: 0 24px 24px;
  font-size: 22px;
  font-weight: 700;
  color: $primary;
  text-decoration: none;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 0 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 8px;
  color: $text2;
  font-size: 15px;
  text-decoration: none;
  transition: all 0.2s;
  .icon {
    font-size: 18px;
  }
  &:hover {
    background: #fef9f0;
    color: $primary;
  }
  &.active {
    background: rgba(230, 162, 60, 0.12);
    color: $primary;
    font-weight: 600;
  }
}

.main-area {
  flex: 1;
  margin-left: 200px;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.top-bar {
  position: sticky;
  top: 0;
  z-index: 40;
  background: $bg;
  border-bottom: 1px solid $border;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.search-wrap {
  flex: 1;
  max-width: 400px;
  display: flex;
  border: 1px solid $border;
  border-radius: 20px;
  overflow: hidden;
  background: #fafafa;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: none;
  background: transparent;
  font-size: 14px;
  outline: none;
}

.search-btn {
  padding: 10px 20px;
  background: $primary;
  color: #fff;
  border: none;
  font-size: 14px;
  cursor: pointer;
  &:hover {
    opacity: 0.9;
  }
}

.btn-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: none;
  background: transparent;
  border-radius: 20px;
  cursor: pointer;
  color: $text2;
  font-size: 14px;
  &:hover {
    background: #f5f5f5;
  }
  .avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
  }
  .avatar-placeholder {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: $primary;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
  }
}

.content {
  flex: 1;
  padding: 0;
  min-height: calc(100vh - 60px);
}

.float-actions {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 60;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.float-btn {
  padding: 10px 16px;
  background: $bg;
  border: 1px solid $border;
  border-radius: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  font-size: 14px;
  color: $text2;
  cursor: pointer;
  transition: all 0.2s;
  &:hover {
    background: $primary;
    color: #fff;
    border-color: $primary;
  }
}
</style>
