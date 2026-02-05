<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import axios from 'axios'

type TabType = 'comment' | 'like' | 'follow'
const activeTab = ref<TabType>('comment')

const tabs = [
  { key: 'comment' as TabType, label: '评论和@' },
  { key: 'like' as TabType, label: '赞和收藏' },
  { key: 'follow' as TabType, label: '新增关注' },
]

const loading = ref(false)

const commentList = ref<any[]>([])
const likeList = ref<any[]>([])
const followList = ref<any[]>([])

async function fetchNotifications(type: TabType) {
  loading.value = true
  try {
    const res = await axios.get('/api/notification', {
      params: { type, page: 1, size: 20 },
    })
    if (res.data.code === 200) {
      const list = res.data.data ?? []
      if (type === 'comment') commentList.value = list
      else if (type === 'like') likeList.value = list
      else if (type === 'follow') followList.value = list
    }
  } catch {
    // 失败时保持当前列表（可选：提示）
  } finally {
    loading.value = false
  }
}

function replyTo(item: { id: number; actorName?: string }) {
  console.log('回复', item.actorName ?? '用户')
}

function likeItem(item: { id: number }) {
  console.log('点赞', item.id)
}

onMounted(() => {
  fetchNotifications('comment')
})

watch(activeTab, (t) => {
  fetchNotifications(t)
})
</script>

<template>
  <div class="notification-view">
    <div class="tabs-bar">
      <button
        v-for="t in tabs"
        :key="t.key"
        type="button"
        class="tab-btn"
        :class="{ active: activeTab === t.key }"
        @click="activeTab = t.key"
      >
        {{ t.label }}
      </button>
    </div>

    <div class="content">
      <!-- 评论和@ -->
      <template v-if="activeTab === 'comment'">
        <div v-for="item in commentList" :key="item.id" class="notice-item">
          <img :src="item.actorAvatar" alt="" class="avatar" />
          <div class="body">
            <p class="text">
              <span class="name">{{ item.actorName }}</span>
              回复了你：{{ item.content }}
            </p>
            <p class="meta">{{ item.postTitle }} · {{ item.createdAt }}</p>
            <div class="actions">
              <button type="button" class="btn-reply" @click="replyTo(item)">
                回复
              </button>
              <button type="button" class="btn-like" @click="likeItem(item)">
                赞
              </button>
            </div>
          </div>
        </div>
      </template>

      <!-- 赞和收藏 -->
      <template v-else-if="activeTab === 'like'">
        <div v-for="item in likeList" :key="item.id" class="notice-item">
          <img :src="item.actorAvatar" alt="" class="avatar" />
          <div class="body">
            <p class="text">
              <span class="name">{{ item.actorName }}</span>
              赞了你的笔记
            </p>
            <p class="meta">{{ item.postTitle }} · {{ item.createdAt }}</p>
          </div>
        </div>
      </template>

      <!-- 新增关注 -->
      <template v-else-if="activeTab === 'follow'">
        <div v-for="item in followList" :key="item.id" class="notice-item">
          <img :src="item.actorAvatar" alt="" class="avatar" />
          <div class="body">
            <p class="text">
              <span class="name">{{ item.actorName }}</span>
              关注了你
            </p>
            <p class="meta">{{ item.createdAt }}</p>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$primary: #e6a23c;
$text: #333;
$text2: #666;
$border: #eee;

.notification-view {
  width: 100%;
  max-width: min(700px, 100vw - 240px);
  margin: 0 auto;
  padding: clamp(16px, 3vw, 24px);
  background: #fff;
  min-height: calc(100vh - 60px);
  box-sizing: border-box;
}

.tabs-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 1px solid $border;
}

.tab-btn {
  padding: 12px 20px;
  border: none;
  background: none;
  font-size: 14px;
  color: $text2;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  &:hover {
    color: $text;
  }
  &.active {
    color: $primary;
    font-weight: 600;
    border-bottom-color: $primary;
  }
}

.content {
  padding: 0;
}

.notice-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.body {
  flex: 1;
  min-width: 0;
}

.text {
  margin: 0 0 6px;
  font-size: 14px;
  color: $text;
  line-height: 1.5;
  .name {
    font-weight: 600;
    margin-right: 4px;
  }
}

.meta {
  margin: 0 0 8px;
  font-size: 12px;
  color: #999;
}

.actions {
  display: flex;
  gap: 12px;
}

.btn-reply,
.btn-like {
  padding: 4px 12px;
  border: 1px solid $border;
  background: #fff;
  border-radius: 6px;
  font-size: 12px;
  color: $text2;
  cursor: pointer;
  &:hover {
    border-color: $primary;
    color: $primary;
  }
}
</style>
