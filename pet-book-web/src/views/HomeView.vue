<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import axios from 'axios'
import { useAppStore } from '../stores/app'
import type { PostCard, PostDetail } from '../types'
import {
  getDemoPostsForChannel,
  getDemoPostByIndex,
  type ChannelId,
} from '../data/demoFeed'

const appStore = useAppStore()

const channels: { id: ChannelId; name: string }[] = [
  { id: 'all', name: '推荐' },
  { id: 'adopt', name: '宠物领养' },
  { id: 'knowledge', name: '养宠知识' },
  { id: 'help', name: '宠物求助' },
]
const activeChannel = ref<ChannelId>('all')

const postList = ref<PostCard[]>([])
const loading = ref(false)
const hasMore = ref(true)
const page = ref(1)
const pageSize = 16
const likedIds = ref<Set<number>>(new Set())
const channelBarRef = ref<HTMLElement | null>(null)

/** 频道 ID → 展示文案（与后端 channel 字符串一致：adopt/knowledge/help） */
const channelLabel: Record<ChannelId, string> = {
  all: '',
  adopt: '领养',
  knowledge: '养宠知识',
  help: '求助',
}

/** 卡片上的 channel 可能来自 API（adopt/knowledge/help）或 mock（中文），统一转成展示文案 */
function displayChannel(ch: string | undefined): string {
  if (!ch) return ''
  return channelLabel[ch as ChannelId] ?? ch
}

function formatLikeCount(n: number): string {
  if (n >= 100000) return '10万+'
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return String(n)
}

function buildImageUrl(seed: number | string, w = 600, h = 600): string {
  return `https://picsum.photos/seed/${seed}/${w}/${h}`
}

async function fetchFeed(append = false) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await axios.get('/api/post/feed', {
      params: {
        channel: activeChannel.value === 'all' ? undefined : activeChannel.value,
        page: append ? page.value : 1,
        size: pageSize,
      },
    })
    if (res.data.code === 200) {
      let list = res.data.data ?? []
      // 接口返回空且非“加载更多”时，使用演示数据，确保首页有内容
      if (list.length === 0 && !append) {
        list = getMockPosts(activeChannel.value, 0, pageSize)
        page.value = 2
      }
      if (append) {
        postList.value = [...postList.value, ...list]
        if (list.length > 0) page.value += 1
      } else {
        postList.value = list
        if (list.length > 0) page.value = 2
      }
      hasMore.value = list.length >= pageSize
    } else {
      // 非 200 时也用演示数据
      useMockFeed(append)
    }
  } catch {
    useMockFeed(append)
  } finally {
    loading.value = false
  }
}

function useMockFeed(append: boolean) {
  const mock = getMockPosts(activeChannel.value, append ? page.value - 1 : 0, pageSize)
  if (append) {
    postList.value = [...postList.value, ...mock]
    page.value += 1
  } else {
    postList.value = mock
    page.value = 2
  }
  hasMore.value = mock.length >= pageSize
}

function getMockPosts(channel: ChannelId, pageOffset: number, size: number): PostCard[] {
  const list = getDemoPostsForChannel(channel)
  const base = pageOffset * size
  const channelIdNum = { all: 0, adopt: 1, knowledge: 2, help: 3 }[channel]
  const cards: PostCard[] = []
  for (let i = 0; i < size; i++) {
    const demo = getDemoPostByIndex(channel, base + i)
    if (!demo) continue
    const id = channelIdNum * 10000 + base + i + 1
    cards.push({
      id,
      title: demo.title,
      desc: demo.desc,
      coverUrl: buildImageUrl(demo.imageSeed),
      coverAspectRatio: demo.aspectRatio,
      authorName: demo.authorName,
      authorAvatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${demo.authorSeed}`,
      likeCount: demo.likeCount,
      channel: channelLabel[channel] || undefined, // mock 用中文；真实 API 返回 adopt/knowledge/help，前端用 displayChannel 展示
    })
  }
  return cards
}

function openPost(post: PostCard) {
  const detail: PostDetail = {
    id: post.id,
    title: post.title,
    desc: post.desc ?? post.title + '。欢迎交流～',
    imageUrls: [
      post.coverUrl,
      buildImageUrl(`${post.id}-a`, 600, 600),
      buildImageUrl(`${post.id}-b`, 600, 600),
    ],
    authorId: post.id,
    authorName: post.authorName,
    authorAvatar: post.authorAvatar,
    likeCount: post.likeCount,
    collectCount: Math.floor(post.likeCount * 0.35),
    commentCount: Math.floor(post.likeCount * 0.08),
    createdAt: new Date(Date.now() - (post.id % 7) * 86400000).toISOString(),
    publishIp: ['广东', '上海', '北京', '浙江', '四川', '江苏', '山东'][post.id % 7],
  }
  appStore.openPost(post.id, detail)
}

function toggleLikeInFeed(id: number) {
  const set = new Set(likedIds.value)
  if (set.has(id)) set.delete(id)
  else set.add(id)
  likedIds.value = set
}

function onRefresh() {
  hasMore.value = true
  page.value = 1
  fetchFeed(false)
}

function onScrollLoad() {
  if (loading.value) return
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement
  if (scrollTop + clientHeight >= scrollHeight - 200) {
    if (hasMore.value) fetchFeed(true)
    else onRefresh()
  }
}

onMounted(() => {
  fetchFeed(false)
  window.addEventListener('scroll', onScrollLoad, { passive: true })
  window.addEventListener('feed-refresh', onRefresh)
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScrollLoad)
  window.removeEventListener('feed-refresh', onRefresh)
})

watch(activeChannel, (id) => {
  page.value = 1
  hasMore.value = true
  fetchFeed(false)
  // 切换频道时把当前 Tab 滚到可见区域
  nextTick(() => {
    const el = channelBarRef.value?.querySelector(`[data-channel="${id}"]`)
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' })
  })
})
</script>

<template>
  <div class="home-view">
    <!-- 频道横向滚动栏（模仿小红书） -->
    <div class="channel-bar-wrap">
      <div class="channel-bar" ref="channelBarRef">
        <a
          v-for="ch in channels"
          :key="ch.id"
          href="javascript:;"
          class="channel-tab"
          :class="{ active: activeChannel === ch.id }"
          :data-channel="ch.id"
          @click.prevent="activeChannel = ch.id"
        >
          {{ ch.name }}
        </a>
      </div>
    </div>

    <!-- 瀑布流 -->
    <div class="feed-wrap">
      <div class="waterfall">
        <div
          v-for="item in postList"
          :key="item.id"
          class="feed-card"
          @click="openPost(item)"
        >
          <div class="card-cover" :style="{ aspectRatio: String(item.coverAspectRatio ?? 1) }">
            <img :src="item.coverUrl" alt="" />
          </div>
          <div class="card-info">
            <p class="card-title">{{ item.title }}</p>
          </div>
          <div class="card-footer" @click.stop>
            <span class="author">
              <img :src="item.authorAvatar" alt="" class="author-avatar" />
              <span class="author-name">{{ item.authorName }}</span>
            </span>
            <button
              type="button"
              class="like-btn"
              :class="{ liked: likedIds.has(item.id) }"
              @click="toggleLikeInFeed(item.id)"
            >
              ♥ <span class="like-count">{{ formatLikeCount(item.likeCount + (likedIds.has(item.id) ? 1 : 0)) }}</span>
            </button>
          </div>
        </div>
      </div>
      <div v-if="loading" class="loading-tip">加载中...</div>
      <div v-else-if="!hasMore && postList.length" class="no-more">下滑刷新下一批</div>
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

.home-view {
  background: #f8f6f1;
  min-height: calc(100vh - 60px);
  width: 100%;
}

.channel-bar-wrap {
  background: $bg;
  border-bottom: 1px solid $border;
  position: relative;
}

.channel-bar {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 20px;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  -webkit-overflow-scrolling: touch;
  &::-webkit-scrollbar {
    display: none;
  }
}

.channel-tab {
  flex-shrink: 0;
  padding: 14px 18px;
  font-size: 15px;
  color: $text2;
  white-space: nowrap;
  border-bottom: 3px solid transparent;
  margin-bottom: -1px;
  text-decoration: none;
  transition: color 0.2s, border-color 0.2s;
  &:hover {
    color: $text;
  }
  &.active {
    color: $primary;
    font-weight: 600;
    border-bottom-color: $primary;
  }
}

.feed-wrap {
  width: 100%;
  max-width: min(1200px, 100vw - 240px);
  margin: 0 auto;
  padding: 20px clamp(16px, 3vw, 24px) 60px;
  box-sizing: border-box;
}

.waterfall {
  column-count: 4;
  column-gap: 16px;
}

.feed-card {
  break-inside: avoid;
  margin-bottom: 16px;
  background: $bg;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: box-shadow 0.2s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.card-cover {
  overflow: hidden;
  background: #f0f0f0;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.card-info {
  padding: 10px 12px 8px;
}

.card-title {
  margin: 0 0 8px;
  font-size: 14px;
  color: $text;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: $text3;
}

.card-footer {
  padding: 8px 10px 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.author {
  display: flex;
  align-items: center;
  gap: 6px;
  max-width: 72%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.author-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.author-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.like-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border: 1px solid $border;
  background: #fff;
  border-radius: 999px;
  font-size: 12px;
  color: $text2;
  cursor: pointer;
  &:hover {
    border-color: $primary;
    color: $primary;
  }
  &.liked {
    background: rgba(230, 162, 60, 0.12);
    border-color: rgba(230, 162, 60, 0.35);
    color: $primary;
  }
}

.like-count {
  flex-shrink: 0;
}

.loading-tip,
.no-more {
  text-align: center;
  padding: 20px;
  color: $text3;
  font-size: 14px;
}

@media (max-width: 900px) {
  .waterfall {
    column-count: 3;
  }
}
@media (max-width: 600px) {
  .feed-wrap {
    padding: 16px;
  }
  .waterfall {
    column-count: 2;
    column-gap: 10px;
  }
}
</style>
