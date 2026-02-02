<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAppStore } from '../stores/app'
import type { UserProfile, Pet } from '../types'

const appStore = useAppStore()
const user = computed(() => appStore.user)

const activeTab = ref<'posts' | 'collect' | 'likes'>('posts')

// 宠物：最多展示 2 个，其余折叠可展开
const petExpand = ref(false)
const PET_DISPLAY_LIMIT = 2

const pets = computed(() => user.value?.pets ?? [])
const petsDisplay = computed(() => {
  const list = pets.value
  if (petExpand.value || list.length <= PET_DISPLAY_LIMIT) return list
  return list.slice(0, PET_DISPLAY_LIMIT)
})
const petsFoldedCount = computed(() => {
  const n = pets.value.length
  if (n <= PET_DISPLAY_LIMIT) return 0
  return petExpand.value ? 0 : n - PET_DISPLAY_LIMIT
})

function petGenderText(pet: Pet): string {
  return pet.gender === 'male' ? '公' : '母'
}

// 性别展示文案
function genderText(profile: UserProfile | null): string {
  if (!profile?.gender) return ''
  if (profile.gender === 'male') return '♂'
  if (profile.gender === 'female') return '♀'
  return ''
}

// 我发布的帖子 / 收藏 / 点赞（演示数据，与首页风格一致）
const postList = ref([
  { id: 1, coverUrl: 'https://picsum.photos/seed/1011/400/400', title: '小区流浪猫求领养，已驱虫疫苗', likeCount: 2840 },
  { id: 2, coverUrl: 'https://picsum.photos/seed/2011/400/400', title: '狗狗日常护理小知识分享', likeCount: 15600 },
  { id: 3, coverUrl: 'https://picsum.photos/seed/1012/400/400', title: '领养代替购买，给毛孩子一个家', likeCount: 3200 },
  { id: 4, coverUrl: 'https://picsum.photos/seed/2012/400/400', title: '猫咪呕吐怎么办？家庭处理指南', likeCount: 8900 },
  { id: 5, coverUrl: 'https://picsum.photos/seed/3011/400/400', title: '幼犬喂养注意事项', likeCount: 4200 },
  { id: 6, coverUrl: 'https://picsum.photos/seed/1013/400/400', title: '养猫必知的十大常识', likeCount: 11200 },
])

const collectList = ref([
  { id: 101, coverUrl: 'https://picsum.photos/seed/2013/400/400', title: '收藏的养宠知识', likeCount: 9800 },
  { id: 102, coverUrl: 'https://picsum.photos/seed/2014/400/400', title: '猫狗驱虫药怎么选', likeCount: 5600 },
])

const likeList = ref([
  { id: 201, coverUrl: 'https://picsum.photos/seed/3012/400/400', title: '点赞的求助帖', likeCount: 7200 },
])

function formatCount(n: number): string {
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return String(n)
}

function openPost(id: number) {
  console.log('open post', id)
}

function formatBirthday(birthday?: string): string {
  if (!birthday) return ''
  const d = new Date(birthday)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}
</script>

<template>
  <div class="profile-view">
    <div class="profile-header">
      <img
        v-if="user?.avatar"
        :src="user.avatar"
        alt=""
        class="avatar"
      />
      <div v-else class="avatar-placeholder">我</div>
      <div class="info">
        <h1 class="nickname">{{ user?.nickname ?? '未登录' }}</h1>
        <p class="meta-line">
          <span class="label">宠物书号: {{ user?.accountId ?? '-' }}</span>
          <span class="divider">·</span>
          <span class="label">IP属地: {{ user?.ip ?? '-' }}</span>
        </p>
        <p v-if="user?.signature" class="signature">{{ user.signature }}</p>
        <div v-if="user?.gender || user?.age || user?.location || user?.profession" class="basic-info">
          <template v-if="user?.gender">{{ genderText(user) }}</template>
          <template v-if="user?.age">{{ user.age }}岁</template>
          <template v-if="user?.location">{{ user.location }}</template>
          <template v-if="user?.profession">{{ user.profession }}</template>
        </div>
        <div class="stats">
          <span class="stat-item"><strong>{{ user?.followingCount ?? 0 }}</strong> 关注</span>
          <span class="stat-item"><strong>{{ user?.followersCount ?? 0 }}</strong> 粉丝</span>
          <span class="stat-item"><strong>{{ formatCount(user?.likesAndCollectCount ?? 0) }}</strong> 获赞与收藏</span>
        </div>
      </div>
    </div>

    <!-- 我的宠物：最多展示 2 个，多余折叠可展开 -->
    <section v-if="pets.length" class="pets-section">
      <h2 class="section-title">我的宠物</h2>
      <div class="pets-grid">
        <div
          v-for="pet in petsDisplay"
          :key="pet.id"
          class="pet-card"
        >
          <div class="pet-avatar-wrap">
            <img :src="pet.avatar" :alt="pet.name" class="pet-avatar" />
          </div>
          <div class="pet-info">
            <div class="pet-name">{{ pet.name }}</div>
            <div class="pet-meta">
              <span>{{ pet.species }}</span>
              <span v-if="pet.breed"> · {{ pet.breed }}</span>
              <span> · {{ petGenderText(pet) }}</span>
            </div>
            <div class="pet-meta">
              <span v-if="pet.ageText">年龄 {{ pet.ageText }}</span>
              <span v-if="pet.birthday"> · 生日 {{ formatBirthday(pet.birthday) }}</span>
            </div>
            <div class="pet-health">
              <span class="health-label">健康：</span>{{ pet.health }}
            </div>
            <div v-if="pet.neutered !== undefined" class="pet-meta">
              {{ pet.neutered ? '已绝育' : '未绝育' }}
            </div>
          </div>
        </div>
      </div>
      <button
        v-if="petsFoldedCount > 0"
        type="button"
        class="btn-expand-pets"
        @click="petExpand = true"
      >
        展开更多（{{ petsFoldedCount }} 只）
      </button>
      <button
        v-else-if="pets.length > PET_DISPLAY_LIMIT"
        type="button"
        class="btn-expand-pets"
        @click="petExpand = false"
      >
        收起
      </button>
    </section>

    <div class="tabs-bar">
      <button
        type="button"
        class="tab-btn"
        :class="{ active: activeTab === 'posts' }"
        @click="activeTab = 'posts'"
      >
        笔记
      </button>
      <button
        type="button"
        class="tab-btn"
        :class="{ active: activeTab === 'collect' }"
        @click="activeTab = 'collect'"
      >
        收藏
      </button>
      <button
        type="button"
        class="tab-btn"
        :class="{ active: activeTab === 'likes' }"
        @click="activeTab = 'likes'"
      >
        点赞
      </button>
    </div>

    <div class="grid-wrap">
      <template v-if="activeTab === 'posts'">
        <div
          v-for="item in postList"
          :key="item.id"
          class="grid-card"
          @click="openPost(item.id)"
        >
          <div class="card-cover">
            <img :src="item.coverUrl" alt="" />
          </div>
          <p class="card-title">{{ item.title }}</p>
          <div class="card-footer">
            <img v-if="user?.avatar" :src="user.avatar" alt="" class="footer-avatar" />
            <span class="footer-name">{{ user?.nickname }}</span>
            <span class="footer-likes">♥ {{ formatCount(item.likeCount) }}</span>
          </div>
        </div>
      </template>
      <template v-else-if="activeTab === 'collect'">
        <div
          v-for="item in collectList"
          :key="item.id"
          class="grid-card"
          @click="openPost(item.id)"
        >
          <div class="card-cover">
            <img :src="item.coverUrl" alt="" />
          </div>
          <p class="card-title">{{ item.title }}</p>
          <div class="card-footer">
            <img v-if="user?.avatar" :src="user.avatar" alt="" class="footer-avatar" />
            <span class="footer-name">{{ user?.nickname }}</span>
            <span class="footer-likes">♥ {{ formatCount(item.likeCount) }}</span>
          </div>
        </div>
      </template>
      <template v-else>
        <div
          v-for="item in likeList"
          :key="item.id"
          class="grid-card"
          @click="openPost(item.id)"
        >
          <div class="card-cover">
            <img :src="item.coverUrl" alt="" />
          </div>
          <p class="card-title">{{ item.title }}</p>
          <div class="card-footer">
            <img v-if="user?.avatar" :src="user.avatar" alt="" class="footer-avatar" />
            <span class="footer-name">{{ user?.nickname }}</span>
            <span class="footer-likes">♥ {{ formatCount(item.likeCount) }}</span>
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
$text3: #999;
$border: #eee;

.profile-view {
  width: 100%;
  max-width: min(960px, 100vw - 240px);
  margin: 0 auto;
  padding: clamp(16px, 3vw, 24px);
  background: #fff;
  min-height: calc(100vh - 60px);
  box-sizing: border-box;
}

.profile-header {
  display: flex;
  gap: 28px;
  padding-bottom: 24px;
  border-bottom: 1px solid $border;
}

.avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: $primary;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}

.info {
  flex: 1;
  min-width: 0;
}

.nickname {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 700;
  color: $text;
}

.meta-line {
  margin: 0 0 6px;
  font-size: 14px;
  color: $text2;
  .divider {
    margin: 0 8px;
    color: $text3;
  }
}

.signature {
  margin: 0 0 10px;
  font-size: 14px;
  color: $text2;
  line-height: 1.5;
}

.basic-info {
  margin-bottom: 12px;
  font-size: 14px;
  color: $text2;
  & > * + * {
    margin-left: 12px;
  }
}

.stats {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: $text2;
  .stat-item {
    strong {
      color: $text;
      font-weight: 600;
      margin-right: 4px;
    }
  }
}

.pets-section {
  padding: 20px 0;
  border-bottom: 1px solid $border;
}

.section-title {
  margin: 0 0 16px;
  font-size: 17px;
  font-weight: 600;
  color: $text;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.pet-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #fafaf6;
  border-radius: 12px;
  border: 1px solid $border;
}

.pet-avatar-wrap {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  background: #f0f0f0;
}

.pet-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.pet-info {
  flex: 1;
  min-width: 0;
}

.pet-name {
  font-size: 16px;
  font-weight: 600;
  color: $text;
  margin-bottom: 6px;
}

.pet-meta {
  font-size: 13px;
  color: $text2;
  margin-bottom: 4px;
}

.pet-health {
  font-size: 13px;
  color: $text2;
  margin-top: 6px;
  .health-label {
    color: $text3;
  }
}

.btn-expand-pets {
  margin-top: 12px;
  padding: 8px 16px;
  border: 1px solid $primary;
  background: transparent;
  color: $primary;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  &:hover {
    background: rgba(230, 162, 60, 0.1);
  }
}

.tabs-bar {
  display: flex;
  gap: 4px;
  margin: 20px 0 16px;
  border-bottom: 1px solid $border;
}

.tab-btn {
  padding: 12px 20px;
  border: none;
  background: none;
  font-size: 15px;
  color: $text2;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  margin-bottom: -1px;
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

.grid-wrap {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}

.grid-card {
  border-radius: 12px;
  overflow: hidden;
  background: #fafafa;
  cursor: pointer;
  transition: box-shadow 0.2s;
  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }
}

.card-cover {
  aspect-ratio: 1;
  overflow: hidden;
  background: #f0f0f0;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.card-title {
  margin: 0;
  padding: 10px 12px 6px;
  font-size: 14px;
  color: $text;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px 12px;
  font-size: 12px;
  color: $text3;
}

.footer-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.footer-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.footer-likes {
  flex-shrink: 0;
  color: $primary;
}
</style>
