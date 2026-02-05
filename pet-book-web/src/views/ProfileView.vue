<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'
import type { UserProfile, Pet, PostCard } from '../types'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const user = computed(() => appStore.user)

/** 是否为当前登录用户自己的主页（可编辑） */
const isSelf = computed(() => !route.query.userId)

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

/** 后端 gender 0/1/2 -> 前端 'male'|'female'|'' */
function normGender(g: number | null | undefined): 'male' | 'female' | '' {
  if (g === 1) return 'male'
  if (g === 2) return 'female'
  return ''
}
/** 前端性别 -> 后端 0/1/2 */
function genderToNum(g: 'male' | 'female' | ''): number {
  if (g === 'male') return 1
  if (g === 'female') return 2
  return 0
}

function petGenderText(pet: Pet): string {
  const ge = typeof pet.gender === 'number' ? normGender(pet.gender) : pet.gender
  return ge === 'male' ? '公' : ge === 'female' ? '母' : ''
}

// 性别展示文案
function genderText(profile: UserProfile | null): string {
  if (!profile?.gender) return ''
  const g = typeof profile.gender === 'number' ? normGender(profile.gender) : profile.gender
  if (g === 'male') return '♂'
  if (g === 'female') return '♀'
  return ''
}

// 我发布的帖子 / 收藏 / 点赞（从后端接口获取）
const postList = ref<PostCard[]>([])
const collectList = ref<PostCard[]>([])
const likeList = ref<PostCard[]>([])

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

/** 将后端返回的 profile 转为前端 UserProfile 形状（性别、宠物字段等） */
function normalizeProfile(raw: Record<string, unknown>): UserProfile {
  const p = raw as UserProfile & { gender?: number; pets?: Array<Record<string, unknown>> }
  const profile: UserProfile = {
    id: p.id,
    nickname: p.nickname ?? '',
    avatar: p.avatar ?? '',
    accountId: String(p.accountId ?? ''),
    ip: p.ip ?? '',
    signature: p.signature ?? (p as Record<string, unknown>).desc ?? '',
    gender: typeof p.gender === 'number' ? normGender(p.gender) : (p.gender ?? ''),
    age: p.age,
    location: p.location,
    profession: p.profession,
    followingCount: p.followingCount ?? 0,
    followersCount: p.followersCount ?? 0,
    likesAndCollectCount: p.likesAndCollectCount ?? 0,
    pets: [],
  }
  const list = raw.pets as Array<Record<string, unknown>> | undefined
  if (Array.isArray(list)) {
    profile.pets = list.map((r) => ({
      id: r.id as number,
      name: (r.name as string) ?? '',
      avatar: (r.avatar as string) ?? '',
      species: (r.species as string) ?? '',
      breed: (r.breed as string) | undefined,
      gender: normGender(r.gender as number) as 'male' | 'female',
      birthday: (r.birthday as string) | undefined,
      ageText: (r.ageText as string) | undefined,
      health: (r.health as string) ?? '',
      neutered: (r.neutered as number) === 1,
    }))
  }
  return profile
}

async function fetchProfile(userId: number) {
  const url = Number(userId) === Number(appStore.user?.id)
    ? '/api/user/profile'
    : `/api/user/${userId}/profile`
  const res = await axios.get(url)
  if (res.data.code === 200 && res.data.data) {
    appStore.user = normalizeProfile(res.data.data)
  }
}

async function fetchLists(userId: number) {
  const [postsRes, collectsRes, likesRes] = await Promise.all([
    axios.get(`/api/user/${userId}/posts`, { params: { page: 1, size: 30 } }),
    axios.get(`/api/user/${userId}/collects`, { params: { page: 1, size: 30 } }),
    axios.get(`/api/user/${userId}/likes`, { params: { page: 1, size: 30 } }),
  ])
  if (postsRes.data.code === 200) postList.value = postsRes.data.data ?? []
  if (collectsRes.data.code === 200) collectList.value = collectsRes.data.data ?? []
  if (likesRes.data.code === 200) likeList.value = likesRes.data.data ?? []
}

onMounted(async () => {
  const userIdParam = route.query.userId
  const userId = userIdParam ? Number(userIdParam) : undefined
  const id = userId || (appStore.user?.id as number | undefined)
  if (!id) return
  await fetchProfile(id)
  await fetchLists(id)
})

watch(
  () => route.query.userId,
  async (val) => {
    const id = val ? Number(val) : (appStore.user?.id as number | undefined)
    if (!id) return
    await fetchProfile(id)
    await fetchLists(id)
  },
)

// ---------- 编辑资料弹窗 ----------
const showEditProfileModal = ref(false)
const editProfileForm = ref({
  nickname: '',
  avatar: '',
  signature: '',
  gender: '' as 'male' | 'female' | '',
  age: undefined as number | undefined,
  location: '',
  profession: '',
})
const editProfileLoading = ref(false)
const editProfileError = ref('')

function openEditProfile() {
  const u = appStore.user
  if (!u) return
  editProfileForm.value = {
    nickname: u.nickname ?? '',
    avatar: u.avatar ?? '',
    signature: u.signature ?? '',
    gender: typeof u.gender === 'number' ? normGender(u.gender) : (u.gender ?? ''),
    age: u.age,
    location: u.location ?? '',
    profession: u.profession ?? '',
  }
  editProfileError.value = ''
  showEditProfileModal.value = true
}

function closeEditProfile() {
  showEditProfileModal.value = false
}

async function submitEditProfile() {
  editProfileError.value = ''
  editProfileLoading.value = true
  try {
    const f = editProfileForm.value
    const res = await axios.put('/api/user/profile', {
      nickname: f.nickname || undefined,
      avatar: f.avatar || undefined,
      signature: f.signature || undefined,
      gender: genderToNum(f.gender),
      age: f.age,
      location: f.location || undefined,
      profession: f.profession || undefined,
    })
    if (res.data.code === 200) {
      const id = appStore.user?.id
      if (id) await fetchProfile(id)
      closeEditProfile()
    } else {
      editProfileError.value = res.data.message || '保存失败'
    }
  } catch (e: unknown) {
    editProfileError.value = (e as { response?: { data?: { message?: string } } })?.response?.data?.message || '网络错误'
  } finally {
    editProfileLoading.value = false
  }
}

// ---------- 添加宠物弹窗 ----------
const showAddPetModal = ref(false)
const addPetForm = ref({
  name: '',
  avatar: '',
  species: '',
  breed: '',
  gender: 'male' as 'male' | 'female',
  birthday: '',
  health: '',
  neutered: false,
})
const addPetLoading = ref(false)
const addPetError = ref('')

function openAddPet() {
  addPetForm.value = {
    name: '',
    avatar: '',
    species: '',
    breed: '',
    gender: 'male',
    birthday: '',
    health: '',
    neutered: false,
  }
  addPetError.value = ''
  showAddPetModal.value = true
}

function closeAddPet() {
  showAddPetModal.value = false
}

async function submitAddPet() {
  if (!addPetForm.value.name.trim()) {
    addPetError.value = '请填写宠物名称'
    return
  }
  addPetError.value = ''
  addPetLoading.value = true
  try {
    const f = addPetForm.value
    const res = await axios.post('/api/pet/add', {
      name: f.name.trim(),
      avatar: f.avatar || undefined,
      species: f.species || undefined,
      breed: f.breed || undefined,
      gender: f.gender === 'male' ? 1 : 2,
      birthday: f.birthday || undefined,
      health: f.health || undefined,
      neutered: f.neutered ? 1 : 0,
    })
    if (res.data.code === 200) {
      const id = appStore.user?.id
      if (id) await fetchProfile(id)
      closeAddPet()
    } else {
      addPetError.value = res.data.message || '添加失败'
    }
  } catch (e: unknown) {
    addPetError.value = (e as { response?: { data?: { message?: string } } })?.response?.data?.message || '网络错误'
  } finally {
    addPetLoading.value = false
  }
}

// ---------- 编辑宠物弹窗 ----------
const showEditPetModal = ref(false)
const editingPetId = ref<number | null>(null)
const editPetForm = ref({
  name: '',
  avatar: '',
  species: '',
  breed: '',
  gender: 'male' as 'male' | 'female',
  birthday: '',
  health: '',
  neutered: false,
})
const editPetLoading = ref(false)
const editPetError = ref('')

function openEditPet(pet: Pet) {
  editingPetId.value = pet.id
  const ge = typeof pet.gender === 'number' ? normGender(pet.gender) : pet.gender
  editPetForm.value = {
    name: pet.name ?? '',
    avatar: pet.avatar ?? '',
    species: pet.species ?? '',
    breed: pet.breed ?? '',
    gender: (ge === 'female' ? 'female' : 'male') as 'male' | 'female',
    birthday: pet.birthday ? String(pet.birthday).slice(0, 10) : '',
    health: pet.health ?? '',
    neutered: !!pet.neutered,
  }
  editPetError.value = ''
  showEditPetModal.value = true
}

function closeEditPet() {
  showEditPetModal.value = false
  editingPetId.value = null
}

async function submitEditPet() {
  const id = editingPetId.value
  if (!id) return
  if (!editPetForm.value.name.trim()) {
    editPetError.value = '请填写宠物名称'
    return
  }
  editPetError.value = ''
  editPetLoading.value = true
  try {
    const f = editPetForm.value
    const res = await axios.put(`/api/pet/${id}`, {
      name: f.name.trim(),
      avatar: f.avatar || undefined,
      species: f.species || undefined,
      breed: f.breed || undefined,
      gender: f.gender === 'male' ? 1 : 2,
      birthday: f.birthday || undefined,
      health: f.health || undefined,
      neutered: f.neutered ? 1 : 0,
    })
    if (res.data.code === 200) {
      const uid = appStore.user?.id
      if (uid) await fetchProfile(uid)
      closeEditPet()
    } else {
      editPetError.value = res.data.message || '保存失败'
    }
  } catch (e: unknown) {
    editPetError.value = (e as { response?: { data?: { message?: string } } })?.response?.data?.message || '网络错误'
  } finally {
    editPetLoading.value = false
  }
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
      <div v-if="isSelf" class="header-actions">
        <button type="button" class="btn-edit-profile" @click="openEditProfile">
          编辑资料
        </button>
        <button
          type="button"
          class="btn-logout"
          @click="() => { appStore.logout(); router.push('/login') }"
        >
          退出登录
        </button>
      </div>
    </div>

    <!-- 我的宠物：最多展示 2 个，多余折叠可展开；本人可添加/编辑 -->
    <section class="pets-section">
      <div class="pets-section-head">
        <h2 class="section-title">我的宠物</h2>
        <button v-if="isSelf" type="button" class="btn-add-pet" @click="openAddPet">
          添加宠物
        </button>
      </div>
      <div v-if="pets.length" class="pets-grid">
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
            <button v-if="isSelf" type="button" class="btn-edit-pet" @click="openEditPet(pet)">
              编辑
            </button>
          </div>
        </div>
      </div>
      <p v-else-if="isSelf" class="pets-empty">暂无宠物，点击上方「添加宠物」添加</p>
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

    <!-- 编辑资料弹窗 -->
    <div v-if="showEditProfileModal" class="modal-mask" @click.self="closeEditProfile">
      <div class="modal-box">
        <h3 class="modal-title">编辑资料</h3>
        <p v-if="editProfileError" class="modal-error">{{ editProfileError }}</p>
        <form class="modal-form" @submit.prevent="submitEditProfile">
          <label>昵称</label>
          <input v-model="editProfileForm.nickname" type="text" placeholder="昵称" />
          <label>头像 URL</label>
          <input v-model="editProfileForm.avatar" type="text" placeholder="头像链接" />
          <label>个性签名</label>
          <textarea v-model="editProfileForm.signature" placeholder="个性签名" rows="2" />
          <label>性别</label>
          <select v-model="editProfileForm.gender">
            <option value="">保密</option>
            <option value="male">男</option>
            <option value="female">女</option>
          </select>
          <label>年龄</label>
          <input v-model.number="editProfileForm.age" type="number" min="0" placeholder="年龄" />
          <label>所在地</label>
          <input v-model="editProfileForm.location" type="text" placeholder="如：江苏南京" />
          <label>职业</label>
          <input v-model="editProfileForm.profession" type="text" placeholder="职业" />
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeEditProfile">取消</button>
            <button type="submit" class="btn-submit" :disabled="editProfileLoading">
              {{ editProfileLoading ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 添加宠物弹窗 -->
    <div v-if="showAddPetModal" class="modal-mask" @click.self="closeAddPet">
      <div class="modal-box">
        <h3 class="modal-title">添加宠物</h3>
        <p v-if="addPetError" class="modal-error">{{ addPetError }}</p>
        <form class="modal-form" @submit.prevent="submitAddPet">
          <label>名字 <span class="required">*</span></label>
          <input v-model="addPetForm.name" type="text" placeholder="宠物名字" />
          <label>头像 URL</label>
          <input v-model="addPetForm.avatar" type="text" placeholder="头像链接" />
          <label>种类</label>
          <input v-model="addPetForm.species" type="text" placeholder="如：猫、狗" />
          <label>品种</label>
          <input v-model="addPetForm.breed" type="text" placeholder="如：橘猫、金毛" />
          <label>性别</label>
          <select v-model="addPetForm.gender">
            <option value="male">公</option>
            <option value="female">母</option>
          </select>
          <label>生日</label>
          <input v-model="addPetForm.birthday" type="date" />
          <label>健康情况</label>
          <input v-model="addPetForm.health" type="text" placeholder="如：健康" />
          <label class="checkbox-label">
            <input v-model="addPetForm.neutered" type="checkbox" /> 已绝育
          </label>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeAddPet">取消</button>
            <button type="submit" class="btn-submit" :disabled="addPetLoading">
              {{ addPetLoading ? '添加中...' : '添加' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 编辑宠物弹窗 -->
    <div v-if="showEditPetModal" class="modal-mask" @click.self="closeEditPet">
      <div class="modal-box">
        <h3 class="modal-title">编辑宠物</h3>
        <p v-if="editPetError" class="modal-error">{{ editPetError }}</p>
        <form class="modal-form" @submit.prevent="submitEditPet">
          <label>名字 <span class="required">*</span></label>
          <input v-model="editPetForm.name" type="text" placeholder="宠物名字" />
          <label>头像 URL</label>
          <input v-model="editPetForm.avatar" type="text" placeholder="头像链接" />
          <label>种类</label>
          <input v-model="editPetForm.species" type="text" placeholder="如：猫、狗" />
          <label>品种</label>
          <input v-model="editPetForm.breed" type="text" placeholder="如：橘猫、金毛" />
          <label>性别</label>
          <select v-model="editPetForm.gender">
            <option value="male">公</option>
            <option value="female">母</option>
          </select>
          <label>生日</label>
          <input v-model="editPetForm.birthday" type="date" />
          <label>健康情况</label>
          <input v-model="editPetForm.health" type="text" placeholder="如：健康" />
          <label class="checkbox-label">
            <input v-model="editPetForm.neutered" type="checkbox" /> 已绝育
          </label>
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeEditPet">取消</button>
            <button type="submit" class="btn-submit" :disabled="editPetLoading">
              {{ editPetLoading ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
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

.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-edit-profile {
  padding: 6px 14px;
  border-radius: 16px;
  border: 1px solid $primary;
  background: #fff;
  font-size: 13px;
  color: $primary;
  cursor: pointer;
}
.btn-edit-profile:hover {
  background: rgba(230, 162, 60, 0.1);
}

.btn-logout {
  align-self: flex-start;
  padding: 6px 12px;
  border-radius: 16px;
  border: 1px solid $border;
  background: #fafafa;
  font-size: 13px;
  color: $text2;
  cursor: pointer;
}

.btn-logout:hover {
  border-color: $primary;
  color: $primary;
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

.pets-section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: $text;
}

.btn-add-pet {
  padding: 6px 14px;
  border-radius: 8px;
  border: 1px solid $primary;
  background: #fff;
  font-size: 14px;
  color: $primary;
  cursor: pointer;
}
.btn-add-pet:hover {
  background: rgba(230, 162, 60, 0.1);
}

.pets-empty {
  margin: 0 0 12px;
  font-size: 14px;
  color: $text3;
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

.btn-edit-pet {
  margin-top: 10px;
  padding: 4px 12px;
  border-radius: 6px;
  border: 1px solid $border;
  background: #fff;
  font-size: 12px;
  color: $text2;
  cursor: pointer;
}
.btn-edit-pet:hover {
  border-color: $primary;
  color: $primary;
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

/* 弹窗 */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-box {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  width: 90%;
  max-width: 420px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: $text;
}

.modal-error {
  margin: 0 0 12px;
  font-size: 13px;
  color: #c00;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
  label {
    font-size: 13px;
    color: $text2;
    margin-top: 4px;
    .required { color: #c00; }
  }
  input[type="text"],
  input[type="number"],
  input[type="date"],
  select,
  textarea {
    padding: 8px 10px;
    border: 1px solid $border;
    border-radius: 6px;
    font-size: 14px;
  }
  .checkbox-label {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 4px;
  }
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 12px;
  border-top: 1px solid $border;
}

.btn-cancel {
  padding: 8px 16px;
  border: 1px solid $border;
  border-radius: 8px;
  background: #fafafa;
  font-size: 14px;
  color: $text2;
  cursor: pointer;
}
.btn-cancel:hover {
  background: #f0f0f0;
}

.btn-submit {
  padding: 8px 20px;
  border: none;
  border-radius: 8px;
  background: $primary;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
}
.btn-submit:hover:not(:disabled) {
  opacity: 0.9;
}
.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
