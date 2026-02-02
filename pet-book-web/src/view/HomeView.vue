<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 定义数据接口 (对应后端的 VO)
interface PostCard {
  id: number
  title: string
  coverUrl: string
  authorName: string
  authorAvatar: string
  likeCount: number
}

const postList = ref<PostCard[]>([])

// 获取数据
const fetchFeed = async () => {
  try {
    // 注意：这里用 /api 前缀触发代理
    const res = await axios.get('/api/post/feed')
    if (res.data.code === 200) {
      postList.value = res.data.data
    }
  } catch (error) {
    console.error('请求失败:', error)
  }
}

onMounted(() => {
  fetchFeed()
})
</script>

<template>
  <div class="app-container">
    <main class="waterfall-container">
      <div v-for="item in postList" :key="item.id" class="card">
        <div class="card-image">
          <img :src="item.coverUrl" alt="cover" />
        </div>
        <div class="card-title">{{ item.title }}</div>
        <div class="card-footer">
          <div class="author">
            <img :src="item.authorAvatar" alt="avatar" />
            <span>{{ item.authorName }}</span>
          </div>
          <div class="likes">
            ❤️ {{ item.likeCount }}
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style lang="scss">

.app-container {
  max-width: 600px; /* 模拟手机宽度，或者设为 100% 铺满 */
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
}

.header {
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 100;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 16px;

  .tabs {
    font-weight: bold;
    font-size: 16px;
    color: #999;
    .active {
      color: #333;
      font-size: 18px;
      border-bottom: 2px solid #ff2442; /* 小红书红 */
    }
  }
}

.waterfall-container {
  padding: 10px;
  /* 核心：两列瀑布流 */
  column-count: 2;
  column-gap: 10px;
}

.card {
  break-inside: avoid; /* 防止卡片被断开 */
  background: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);

  .card-image img {
    width: 100%;
    display: block; /* 消除图片底部间隙 */
    height: auto;
  }

  .card-title {
    padding: 8px;
    font-size: 14px;
    color: #333;
    font-weight: 500;
    line-height: 1.4;
    // 多行省略
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .card-footer {
    padding: 0 8px 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #666;

    .author {
      display: flex;
      align-items: center;
      gap: 4px;
      img {
        width: 16px;
        height: 16px;
        border-radius: 50%;
      }
    }
  }
}
</style>
