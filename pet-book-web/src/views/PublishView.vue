<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 左侧菜单：返回首页、笔记管理、数据看板
const menus = [
  { path: '/', label: '返回首页' },
  { path: '/publish', label: '笔记管理', active: true },
  { path: '/publish/stats', label: '数据看板' },
]

const activeMenu = ref('笔记管理')
const noteSearch = ref('')
const noteList = ref<{ id: number; title: string; status: string; createdAt: string }[]>([])
const noteLoading = ref(false)
const noteHasMore = ref(true)
const notePage = ref(1)
const NOTE_PAGE_SIZE = 10

function goTo(path: string) {
  if (path === '/') router.push('/')
  else if (path === '/publish/stats') {
    activeMenu.value = '数据看板'
    // 可后续做数据看板页
  } else {
    activeMenu.value = '笔记管理'
  }
}

function loadNotes(append = false) {
  if (noteLoading.value) return
  noteLoading.value = true
  setTimeout(() => {
    const list = Array.from({ length: NOTE_PAGE_SIZE }, (_, i) => {
      const idx = (append ? notePage.value - 1 : 0) * NOTE_PAGE_SIZE + i + 1
      return {
        id: idx,
        title: `我的笔记 ${idx} - 宠物领养经验分享`,
        status: (['已发布', '草稿', '审核中'] as const)[idx % 3] ?? '已发布',
        createdAt: new Date(Date.now() - idx * 86400000).toLocaleDateString(),
      }
    })
    if (append) {
      noteList.value = [...noteList.value, ...list]
    } else {
      noteList.value = list
    }
    noteHasMore.value = list.length >= NOTE_PAGE_SIZE
    notePage.value = append ? notePage.value + 1 : 2
    noteLoading.value = false
  }, 300)
}

function onNoteScroll(e: Event) {
  const el = e.target as HTMLElement
  if (el.scrollHeight - el.scrollTop - el.clientHeight < 80 && noteHasMore.value && !noteLoading.value) {
    loadNotes(true)
  }
}

function editNote(id: number) {
  console.log('编辑笔记', id)
}

function deleteNote(id: number) {
  if (confirm('确定删除这条笔记？')) {
    noteList.value = noteList.value.filter((n) => n.id !== id)
  }
}

onMounted(() => {
  loadNotes(false)
})
</script>

<template>
  <div class="publish-view">
    <aside class="publish-sidebar">
      <a
        v-for="m in menus"
        :key="m.label"
        href="javascript:;"
        class="menu-item"
        :class="{ active: activeMenu === m.label }"
        @click.prevent="goTo(m.path)"
      >
        {{ m.label }}
      </a>
    </aside>
    <main class="publish-main">
      <template v-if="activeMenu === '笔记管理'">
        <div class="toolbar">
          <input
            v-model="noteSearch"
            type="text"
            class="search-input"
            placeholder="搜索已发布的笔记"
          />
        </div>
        <div class="note-list" @scroll="onNoteScroll">
          <div
            v-for="note in noteList"
            :key="note.id"
            class="note-item"
          >
            <div class="note-info">
              <h3 class="note-title">{{ note.title }}</h3>
              <div class="note-meta">
                <span class="status">{{ note.status }}</span>
                <span class="date">{{ note.createdAt }}</span>
              </div>
            </div>
            <div class="note-actions">
              <button type="button" class="btn btn-edit" @click="editNote(note.id)">
                编辑
              </button>
              <button type="button" class="btn btn-delete" @click="deleteNote(note.id)">
                删除
              </button>
            </div>
          </div>
          <div v-if="noteLoading" class="loading">加载中...</div>
          <div v-else-if="!noteHasMore && noteList.length" class="no-more">
            没有更多了
          </div>
        </div>
      </template>
      <template v-else-if="activeMenu === '数据看板'">
        <div class="stats-placeholder">
          <p>数据看板（后续开发）</p>
        </div>
      </template>
    </main>
  </div>
</template>

<style lang="scss" scoped>
$primary: #ff2442;
$text: #333;
$text2: #666;
$border: #eee;

.publish-view {
  display: flex;
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
}

.publish-sidebar {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-right: 1px solid $border;
  padding: 20px 0;
}

.menu-item {
  display: block;
  padding: 12px 24px;
  color: $text2;
  font-size: 14px;
  text-decoration: none;
  &:hover {
    background: #fff5f5;
    color: $primary;
  }
  &.active {
    color: $primary;
    font-weight: 600;
    background: rgba($primary, 0.06);
  }
}

.publish-main {
  flex: 1;
  min-width: 0;
  background: #fff;
  margin: 16px;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.toolbar {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  max-width: 400px;
  padding: 10px 16px;
  border: 1px solid $border;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  &:focus {
    border-color: $primary;
  }
}

.note-list {
  flex: 1;
  overflow-y: auto;
  max-height: calc(100vh - 180px);
}

.note-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid $border;
  &:last-child {
    border-bottom: none;
  }
}

.note-info {
  flex: 1;
  min-width: 0;
}

.note-title {
  margin: 0 0 8px;
  font-size: 15px;
  font-weight: 500;
  color: $text;
}

.note-meta {
  font-size: 12px;
  color: #999;
  .status {
    margin-right: 12px;
  }
}

.note-actions {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  border: none;
  &.btn-edit {
    background: #f5f5f5;
    color: $text;
    &:hover {
      background: #eee;
    }
  }
  &.btn-delete {
    background: #fff5f5;
    color: $primary;
    &:hover {
      background: #ffe5e5;
    }
  }
}

.loading,
.no-more {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}

.stats-placeholder {
  padding: 60px;
  text-align: center;
  color: #999;
}
</style>
