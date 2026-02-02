<script setup lang="ts">
import { ref } from 'vue'

type TabType = 'comment' | 'like' | 'follow'
const activeTab = ref<TabType>('comment')

const tabs = [
  { key: 'comment' as TabType, label: '评论和@' },
  { key: 'like' as TabType, label: '赞和收藏' },
  { key: 'follow' as TabType, label: '新增关注' },
]

// 评论和@ mock
const commentList = ref([
  {
    id: 1,
    userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=1',
    userName: '用户A',
    content: '想领养，怎么联系？',
    time: '2分钟前',
    postTitle: '小区流浪猫求领养',
  },
  {
    id: 2,
    userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=2',
    userName: '用户B',
    content: '@你 回复：谢谢分享！',
    time: '1小时前',
    postTitle: '养猫日常',
  },
])

// 赞和收藏 mock
const likeList = ref([
  {
    id: 1,
    userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=3',
    userName: '用户C',
    action: '赞了你的笔记',
    postTitle: '宠物领养经验分享',
    time: '3小时前',
  },
  {
    id: 2,
    userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=4',
    userName: '用户D',
    action: '收藏了你的笔记',
    postTitle: '狗狗喂养指南',
    time: '昨天',
  },
])

// 新增关注 mock
const followList = ref([
  {
    id: 1,
    userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=5',
    userName: '用户E',
    time: '5分钟前',
  },
])

function replyTo(item: { id: number; userName: string }) {
  console.log('回复', item.userName)
}

function likeItem(item: { id: number }) {
  console.log('点赞', item.id)
}
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
        <div
          v-for="item in commentList"
          :key="item.id"
          class="notice-item"
        >
          <img :src="item.userAvatar" alt="" class="avatar" />
          <div class="body">
            <p class="text">
              <span class="name">{{ item.userName }}</span>
              回复了你：{{ item.content }}
            </p>
            <p class="meta">{{ item.postTitle }} · {{ item.time }}</p>
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
        <div
          v-for="item in likeList"
          :key="item.id"
          class="notice-item"
        >
          <img :src="item.userAvatar" alt="" class="avatar" />
          <div class="body">
            <p class="text">
              <span class="name">{{ item.userName }}</span>
              {{ item.action }}
            </p>
            <p class="meta">{{ item.postTitle }} · {{ item.time }}</p>
          </div>
        </div>
      </template>

      <!-- 新增关注 -->
      <template v-else-if="activeTab === 'follow'">
        <div
          v-for="item in followList"
          :key="item.id"
          class="notice-item"
        >
          <img :src="item.userAvatar" alt="" class="avatar" />
          <div class="body">
            <p class="text">
              <span class="name">{{ item.userName }}</span>
              关注了你
            </p>
            <p class="meta">{{ item.time }}</p>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$primary: #ff2442;
$text: #333;
$text2: #666;
$border: #eee;

.notification-view {
  max-width: 700px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  min-height: calc(100vh - 60px);
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
