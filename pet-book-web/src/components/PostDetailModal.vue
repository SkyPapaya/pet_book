<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAppStore } from '../stores/app'
import type { PostDetail, Comment } from '../types'

const appStore = useAppStore()

const currentImageIndex = ref(0)
const commentInput = ref('')
const replyToCommentId = ref<number | null>(null)
const expandedReplies = ref<Set<number>>(new Set())
const swipeStartX = ref<number | null>(null)

// 评论列表：按点赞排序，带子评论
const comments = ref<Comment[]>([])
const commentLoading = ref(false)
const commentHasMore = ref(true)
const commentPage = ref(1)
const COMMENT_PAGE_SIZE = 10

const post = computed(() => appStore.openedPostDetail)
const imageUrls = computed(() => post.value?.imageUrls ?? [])
const currentImage = computed(
  () => imageUrls.value[currentImageIndex.value] ?? imageUrls.value[0] ?? ''
)

function formatLikeCount(n: number): string {
  if (n >= 100000) return '10万+'
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return String(n)
}

function formatTime(createdAt: string): string {
  const d = new Date(createdAt)
  const now = new Date()
  const diff = (now.getTime() - d.getTime()) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  if (diff < 2592000) return `${Math.floor(diff / 86400)}天前`
  return d.toLocaleDateString()
}

function prevImage() {
  if (imageUrls.value.length <= 1) return
  currentImageIndex.value =
    (currentImageIndex.value - 1 + imageUrls.value.length) % imageUrls.value.length
}

function nextImage() {
  if (imageUrls.value.length <= 1) return
  currentImageIndex.value = (currentImageIndex.value + 1) % imageUrls.value.length
}

function onSwipeStart(e: PointerEvent) {
  swipeStartX.value = e.clientX
}

function onSwipeEnd(e: PointerEvent) {
  if (swipeStartX.value == null) return
  const dx = e.clientX - swipeStartX.value
  swipeStartX.value = null
  if (Math.abs(dx) < 50) return
  if (dx < 0) nextImage()
  else prevImage()
}

function close() {
  appStore.closePost()
  currentImageIndex.value = 0
  commentInput.value = ''
  replyToCommentId.value = null
  expandedReplies.value = new Set()
  comments.value = []
  commentPage.value = 1
  commentHasMore.value = true
}

function onMaskClick(e: MouseEvent) {
  if ((e.target as HTMLElement).classList.contains('modal-mask')) {
    close()
  }
}

// 点赞 / 收藏 / 关注（前端切换状态）
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowed = ref(false)
const likeCount = ref(0)
const collectCount = ref(0)

watch(
  post,
  (p) => {
    if (p) {
      likeCount.value = p.likeCount
      collectCount.value = p.collectCount ?? 0
      isLiked.value = !!p.isLiked
      isCollected.value = !!p.isCollected
      isFollowed.value = !!p.isFollowed
      loadComments()
    }
  },
  { immediate: true }
)

function toggleLike() {
  isLiked.value = !isLiked.value
  likeCount.value += isLiked.value ? 1 : -1
}

function toggleCollect() {
  isCollected.value = !isCollected.value
  collectCount.value += isCollected.value ? 1 : -1
}

function toggleFollow() {
  isFollowed.value = !isFollowed.value
}

function firstReply(c: Comment): Comment {
  return (c.replies?.[0] as Comment) ?? ({} as Comment)
}

// 评论：mock 数据
function getMockComments(postId: number, page: number, size: number): Comment[] {
  const list: Comment[] = []
  const contents = [
    '好可爱！想领养',
    '楼主在哪座城市？',
    '已私信，求回复',
    '我家也有一只，可以交流',
    '感谢分享，收藏了',
    '请问疫苗打了吗？',
  ]
  const base = (page - 1) * size
  for (let i = 0; i < size; i++) {
    const id = base + i + 1
    const replyCount = Math.random() > 0.55 ? Math.floor(Math.random() * 5) + 2 : 0
    const replies: Comment[] | undefined = replyCount
      ? Array.from({ length: replyCount }, (_, ri) => {
          const rid = 1000 * postId + id + 500 + ri
          return {
            id: rid,
            postId,
            userId: id + 100 + ri,
            userName: `用户${id + 100 + ri}`,
            userAvatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${id + 100 + ri}`,
            content: (['同感', '已私信', '求地址', '太可爱了', '谢谢分享'] as const)[ri % 5] ?? '',
            likeCount: Math.floor(Math.random() * 60),
            createdAt: new Date(Date.now() - Math.random() * 86400000 * 3).toISOString(),
            parentId: 1000 * postId + id,
          }
        }).sort((a, b) => b.likeCount - a.likeCount)
      : undefined
    list.push({
      id: 1000 * postId + id,
      postId,
      userId: id,
      userName: `用户${id}`,
      userAvatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`,
      content: contents[i % contents.length] ?? '',
      likeCount: Math.floor(Math.random() * 200),
      createdAt: new Date(Date.now() - Math.random() * 86400000 * 7).toISOString(),
      replyCount,
      replies,
    })
  }
  return list.sort((a, b) => b.likeCount - a.likeCount)
}

function loadComments(append = false) {
  if (!post.value || commentLoading.value) return
  commentLoading.value = true
  setTimeout(() => {
    const list = getMockComments(post.value!.id, append ? commentPage.value : 1, COMMENT_PAGE_SIZE)
    if (append) {
      comments.value = [...comments.value, ...list]
      commentPage.value += 1
    } else {
      comments.value = list
      commentPage.value = 2
    }
    commentHasMore.value = list.length >= COMMENT_PAGE_SIZE
    commentLoading.value = false
  }, 300)
}

function loadMoreComments() {
  if (commentLoading.value || !commentHasMore.value) return
  loadComments(true)
}

function toggleReplies(commentId: number) {
  const set = new Set(expandedReplies.value)
  if (set.has(commentId)) set.delete(commentId)
  else set.add(commentId)
  expandedReplies.value = set
}

function startReply(commentId: number) {
  replyToCommentId.value = commentId
}

function submitComment() {
  if (!commentInput.value.trim()) return
  const newComment: Comment = {
    id: Date.now(),
    postId: post.value!.id,
    userId: appStore.user?.id ?? 0,
    userName: appStore.user?.nickname ?? '我',
    userAvatar: appStore.user?.avatar ?? '',
    content: commentInput.value.trim(),
    likeCount: 0,
    createdAt: new Date().toISOString(),
  }
  comments.value = [newComment, ...comments.value]
  commentInput.value = ''
  replyToCommentId.value = null
}

function likeComment(c: Comment) {
  c.likeCount = (c.likeCount ?? 0) + (c.isLiked ? -1 : 1)
  c.isLiked = !c.isLiked
  comments.value.sort((a, b) => (b.likeCount ?? 0) - (a.likeCount ?? 0))
}

function likeReply(r: Comment) {
  r.likeCount = (r.likeCount ?? 0) + (r.isLiked ? -1 : 1)
  r.isLiked = !r.isLiked
}
</script>

<template>
  <Teleport to="body">
    <div
      v-if="appStore.isPostModalOpen && post"
      class="modal-mask"
      @click="onMaskClick"
    >
      <div class="modal-box" @click.stop>
        <!-- 左上角关闭 -->
        <button type="button" class="btn-close" title="关闭" @click="close">
          ✕
        </button>

        <div class="modal-body">
          <!-- 左侧：图片轮播（占大半） -->
          <div class="left-panel">
            <div class="image-area" @pointerdown="onSwipeStart" @pointerup="onSwipeEnd">
              <img :src="currentImage" alt="" class="main-image" />
              <template v-if="imageUrls.length > 1">
                <button type="button" class="nav-btn prev" @click="prevImage">‹</button>
                <button type="button" class="nav-btn next" @click="nextImage">›</button>
              </template>
            </div>
          </div>

          <!-- 右侧：固定作者栏 + 内容 + 评论 + 底部输入 -->
          <div class="right-panel">
            <!-- 右侧顶部作者栏：固定 -->
            <div class="right-author sticky">
              <img :src="post.authorAvatar" alt="" class="author-avatar" />
              <div class="author-meta">
                <div class="author-name">{{ post.authorName }}</div>
                <div class="post-sub">
                  <span v-if="post.createdAt">{{ formatTime(post.createdAt) }}</span>
                  <span v-if="post.publishIp">· IP: {{ post.publishIp }}</span>
                </div>
              </div>
              <button
                type="button"
                class="btn-follow"
                :class="{ followed: isFollowed }"
                @click="toggleFollow"
              >
                {{ isFollowed ? '已关注' : '关注' }}
              </button>
            </div>

            <!-- 右侧正文 -->
            <div class="intro-section">
              <h2 class="post-title">{{ post.title }}</h2>
              <p class="post-desc">{{ post.desc }}</p>
            </div>

            <div class="comments-section">
              <div class="comments-header">评论 {{ post.commentCount ?? 0 }}</div>
              <div class="comments-list" @scroll="(e: any) => {
                const el = e.target;
                if (el.scrollHeight - el.scrollTop - el.clientHeight < 80) loadMoreComments();
              }">
                <div
                  v-for="c in comments"
                  :key="c.id"
                  class="comment-item"
                >
                  <img :src="c.userAvatar" alt="" class="comment-avatar" />
                  <div class="comment-body">
                    <div class="comment-meta">
                      <span class="comment-user">{{ c.userName }}</span>
                      <span class="comment-time">{{ formatTime(c.createdAt) }}</span>
                    </div>
                    <p class="comment-content">{{ c.content }}</p>
                    <div class="comment-actions">
                      <button
                        type="button"
                        class="action-btn"
                        :class="{ liked: c.isLiked }"
                        @click="likeComment(c)"
                      >
                        ♥ {{ formatLikeCount(c.likeCount) }}
                      </button>
                      <button type="button" class="action-btn" @click="startReply(c.id)">
                        回复
                      </button>
                    </div>
                    <!-- 子评论：默认展示最新或点赞最高，其余折叠 -->
                    <template v-if="c.replies?.length">
                      <!-- 默认只展示点赞最多的一条 -->
                      <div v-if="!expandedReplies.has(c.id)" class="replies-list">
                        <div class="reply-item">
                          <span class="reply-user">{{ firstReply(c).userName }}</span>
                          <span class="reply-content">{{ firstReply(c).content }}</span>
                          <button
                            type="button"
                            class="reply-like"
                            :class="{ liked: firstReply(c).isLiked }"
                            @click="likeReply(firstReply(c))"
                          >
                            ♥ {{ formatLikeCount(firstReply(c).likeCount) }}
                          </button>
                        </div>
                      </div>
                      <button
                        v-if="!expandedReplies.has(c.id) && (c.replyCount ?? c.replies.length) > 1"
                        type="button"
                        class="expand-replies"
                        @click="toggleReplies(c.id)"
                      >
                        展开 {{ (c.replyCount ?? c.replies.length) - 1 }} 条回复
                      </button>
                      <!-- 展开后展示全部子评论 -->
                      <div v-if="expandedReplies.has(c.id)" class="replies-list">
                        <div v-for="r in c.replies" :key="r.id" class="reply-item">
                          <span class="reply-user">{{ r.userName }}</span>
                          <span class="reply-content">{{ r.content }}</span>
                          <button
                            type="button"
                            class="reply-like"
                            :class="{ liked: r.isLiked }"
                            @click="likeReply(r)"
                          >
                            ♥ {{ formatLikeCount(r.likeCount) }}
                          </button>
                        </div>
                      </div>
                      <button
                        v-if="expandedReplies.has(c.id)"
                        type="button"
                        class="collapse-replies"
                        @click="toggleReplies(c.id)"
                      >
                        收起回复
                      </button>
                    </template>
                  </div>
                </div>
                <div v-if="commentLoading" class="comment-loading">加载中...</div>
              </div>
            </div>

            <!-- 底部：自己的头像 + 父级评论输入框 -->
            <div class="bottom-bar">
              <img
                :src="appStore.user?.avatar ?? ''"
                alt=""
                class="me-avatar"
              />
              <input
                v-model="commentInput"
                type="text"
                class="comment-input"
                placeholder="发表评论..."
                @keyup.enter="submitComment"
              />
              <button type="button" class="btn-send" @click="submitComment">发送</button>
            </div>

            <!-- 右侧按钮条：点赞/收藏/评论/转发（显示数量） -->
            <div class="right-actions" aria-label="actions">
              <button
                type="button"
                class="action-pill"
                :class="{ active: isLiked }"
                @click="toggleLike"
              >
                ♥<span class="num">{{ formatLikeCount(likeCount) }}</span>
              </button>
              <button
                type="button"
                class="action-pill"
                :class="{ active: isCollected }"
                @click="toggleCollect"
              >
                ☆<span class="num">{{ formatLikeCount(collectCount) }}</span>
              </button>
              <button type="button" class="action-pill">
                💬<span class="num">{{ formatLikeCount(post.commentCount ?? 0) }}</span>
              </button>
              <button type="button" class="action-pill">
                ↗<span class="num">0</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style lang="scss" scoped>
$primary: #e6a23c;
$text: #333;
$text2: #666;
$text3: #999;
$bg: #fff;
$border: #eee;

.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.modal-box {
  width: 100%;
  max-width: 1080px;
  max-height: 90vh;
  background: $bg;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.btn-close {
  position: absolute;
  left: 12px;
  top: 12px;
  z-index: 10;
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  border-radius: 50%;
  font-size: 18px;
  cursor: pointer;
  line-height: 1;
  &:hover {
    background: rgba(0, 0, 0, 0.6);
  }
}

.modal-body {
  display: flex;
  flex: 1;
  min-height: 0;
}

.left-panel {
  width: 62%;
  flex-shrink: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid $border;
}

.image-area {
  position: relative;
  height: 100%;
  overflow: hidden;
  background: #f0f0f0;
  touch-action: pan-y;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  border-radius: 50%;
  font-size: 24px;
  cursor: pointer;
  line-height: 1;
  &.prev {
    left: 8px;
  }
  &.next {
    right: 8px;
  }
  &:hover {
    background: rgba(0, 0, 0, 0.6);
  }
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.btn-follow {
  padding: 6px 16px;
  border: 1px solid $primary;
  background: transparent;
  color: $primary;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  &.followed {
    background: #f5f5f5;
    border-color: $border;
    color: $text2;
  }
}

.right-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  position: relative;
}

.right-author {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: 1px solid $border;
  background: $bg;
  z-index: 2;
}

.right-author.sticky {
  position: sticky;
  top: 0;
}

.author-meta {
  flex: 1;
  min-width: 0;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: $text;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-sub {
  margin-top: 2px;
  font-size: 12px;
  color: $text3;
}

.intro-section {
  padding: 16px;
  border-bottom: 1px solid $border;
  flex-shrink: 0;
}

.post-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: $text;
}

.post-desc {
  margin: 0;
  font-size: 14px;
  color: $text2;
  line-height: 1.5;
}

.comments-section {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.comments-header {
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 600;
  color: $text;
  border-bottom: 1px solid $border;
}

.comments-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 16px;
}

.comment-item {
  display: flex;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  &:last-child {
    border-bottom: none;
  }
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comment-user {
  font-size: 14px;
  font-weight: 500;
  color: $text;
}

.comment-time {
  font-size: 12px;
  color: $text3;
}

.comment-content {
  margin: 0 0 8px;
  font-size: 14px;
  color: $text2;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  padding: 0;
  border: none;
  background: none;
  font-size: 12px;
  color: $text3;
  cursor: pointer;
  &:hover,
  &.liked {
    color: $primary;
  }
}

.replies-list {
  margin-top: 8px;
  padding-left: 12px;
  border-left: 2px solid $border;
}

.reply-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: $text2;
  padding: 6px 0;
}

.reply-user {
  color: $text;
  font-weight: 500;
  flex-shrink: 0;
}

.reply-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.reply-like {
  padding: 0;
  border: none;
  background: none;
  font-size: 12px;
  color: $text3;
  cursor: pointer;
  &.liked,
  &:hover {
    color: $primary;
  }
}

.expand-replies,
.collapse-replies {
  margin-top: 4px;
  padding: 0;
  border: none;
  background: none;
  font-size: 12px;
  color: $primary;
  cursor: pointer;
}

.comment-loading {
  text-align: center;
  padding: 12px;
  font-size: 12px;
  color: $text3;
}

.bottom-bar {
  padding: 12px 16px;
  border-top: 1px solid $border;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.me-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  background: #f0f0f0;
}

.comment-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid $border;
  border-radius: 999px;
  font-size: 14px;
  outline: none;
  background: #fafafa;
  &:focus {
    border-color: $primary;
    background: #fff;
  }
}

.btn-send {
  padding: 10px 18px;
  background: $primary;
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  &:hover {
    opacity: 0.9;
  }
}

.right-actions {
  position: absolute;
  right: 10px;
  bottom: 14px;
  display: flex;
  gap: 10px;
  z-index: 3;
}

.action-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 12px;
  border-radius: 999px;
  border: 1px solid $border;
  background: #fff;
  color: $text2;
  font-size: 13px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  .num {
    font-size: 12px;
    color: $text3;
  }
  &:hover,
  &.active {
    border-color: rgba(230, 162, 60, 0.35);
    color: $primary;
    .num {
      color: $primary;
    }
  }
}

@media (max-width: 768px) {
  .modal-body {
    flex-direction: column;
  }
  .left-panel {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid $border;
  }
  .right-actions {
    position: static;
    padding: 0 16px 16px;
    justify-content: space-between;
  }
}
</style>
