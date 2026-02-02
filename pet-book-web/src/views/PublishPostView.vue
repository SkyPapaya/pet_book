<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

type PublishChannelId = 'adopt' | 'knowledge' | 'help'
const channelOptions: { id: PublishChannelId; name: string }[] = [
  { id: 'adopt', name: '宠物领养' },
  { id: 'knowledge', name: '养宠知识' },
  { id: 'help', name: '宠物求助' },
]
const selectedChannel = ref<PublishChannelId>('adopt')

// 媒体：图片或视频（本地预览 URL）
const mediaList = ref<{ file: File; url: string; type: 'image' | 'video' }[]>([])
const title = ref('')
const body = ref('')

// 公开可见
const isPublic = ref(true)
// 标记地点（占位）
const location = ref('')
// 高级选项（占位）

// 建议话题（演示）
const suggestHashtags = ['#领养代替购买', '#养宠日常', '#宠物求助', '#养宠知识', '#萌宠']
const selectedHashtags = ref<string[]>([])

const fileInputRef = ref<HTMLInputElement | null>(null)
const maxMedia = 9

function goBack() {
  router.push('/publish')
}

function triggerFileSelect() {
  fileInputRef.value?.click()
}

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const files = input.files
  if (!files?.length) return
  const remain = maxMedia - mediaList.value.length
  for (let i = 0; i < Math.min(files.length, remain); i++) {
    const file = files[i]
    if (!file) continue
    const url = URL.createObjectURL(file)
    const type = file.type.startsWith('video/') ? 'video' : 'image'
    mediaList.value.push({ file, url, type })
  }
  input.value = ''
}

function removeMedia(index: number) {
  const item = mediaList.value[index]
  if (item) URL.revokeObjectURL(item.url)
  mediaList.value.splice(index, 1)
}

function toggleHashtag(tag: string) {
  const i = selectedHashtags.value.indexOf(tag)
  if (i >= 0) selectedHashtags.value.splice(i, 1)
  else selectedHashtags.value.push(tag)
}

function saveDraft() {
  console.log('存草稿', { selectedChannel: selectedChannel.value, title: title.value, body: body.value, mediaCount: mediaList.value.length })
  alert('草稿已保存（演示）')
}

function publish() {
  if (!title.value.trim()) {
    alert('请填写标题')
    return
  }
  console.log('发布笔记', {
    channel: selectedChannel.value,
    title: title.value,
    body: body.value,
    mediaCount: mediaList.value.length,
    isPublic: isPublic.value,
    hashtags: selectedHashtags.value,
  })
  alert('发布成功（演示）')
  router.push('/publish')
}

const canAddMore = computed(() => mediaList.value.length < maxMedia)
</script>

<template>
  <div class="publish-post-view">
    <header class="post-header">
      <button type="button" class="btn-back" @click="goBack">‹ 返回</button>
      <span class="title">发布笔记</span>
    </header>

    <div class="post-form">
      <!-- 媒体上传 -->
      <section class="section media-section">
        <div class="media-grid">
          <template v-for="(item, index) in mediaList" :key="index">
            <div class="media-item">
              <img v-if="item.type === 'image'" :src="item.url" alt="" class="media-preview" />
              <video v-else :src="item.url" class="media-preview" muted />
              <button type="button" class="btn-remove" @click="removeMedia(index)">×</button>
            </div>
          </template>
          <div
            v-if="canAddMore"
            class="media-add"
            @click="triggerFileSelect"
          >
            <span class="plus">+</span>
            <span class="hint">添加图片或视频</span>
          </div>
        </div>
        <input
          ref="fileInputRef"
          type="file"
          accept="image/*,video/*"
          multiple
          class="hidden-input"
          @change="onFileChange"
        />
      </section>

      <!-- 发布类型（与三个频道一致） -->
      <section class="section">
        <label class="field-label">选择类型</label>
        <div class="channel-options">
          <button
            v-for="ch in channelOptions"
            :key="ch.id"
            type="button"
            class="channel-btn"
            :class="{ active: selectedChannel === ch.id }"
            @click="selectedChannel = ch.id as PublishChannelId"
          >
            {{ ch.name }}
          </button>
        </div>
      </section>

      <!-- 标题 -->
      <section class="section">
        <input
          v-model="title"
          type="text"
          class="input-title"
          placeholder="添加标题"
        />
      </section>

      <!-- 正文 -->
      <section class="section">
        <div class="input-body-wrap">
          <textarea
            v-model="body"
            class="input-body"
            placeholder="添加正文或发语音"
            rows="4"
          />
          <span class="mic-icon" title="发语音">🎤</span>
        </div>
      </section>

      <!-- 话题 / @用户 / 投票 -->
      <section class="section">
        <div class="tag-actions">
          <button type="button" class="tag-btn"># 话题</button>
          <button type="button" class="tag-btn">@ 用户</button>
          <button type="button" class="tag-btn">投票</button>
        </div>
        <div v-if="suggestHashtags.length" class="hashtag-row">
          <button
            v-for="tag in suggestHashtags"
            :key="tag"
            type="button"
            class="hashtag-btn"
            :class="{ active: selectedHashtags.includes(tag) }"
            @click="toggleHashtag(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </section>

      <!-- 设置项：标记地点、公开可见、高级选项（无添加组件） -->
      <section class="section options-section">
        <button type="button" class="option-row">
          <span class="option-icon">📍</span>
          <span class="option-text">标记地点</span>
          <span class="option-arrow">›</span>
        </button>
        <button type="button" class="option-row">
          <span class="option-icon">🔒</span>
          <span class="option-text">{{ isPublic ? '公开可见' : '仅自己可见' }}</span>
          <span class="option-arrow">›</span>
        </button>
        <button type="button" class="option-row">
          <span class="option-icon">⚙</span>
          <span class="option-text">高级选项</span>
          <span class="option-arrow">›</span>
        </button>
      </section>
    </div>

    <!-- 底部：存草稿、发布笔记 -->
    <footer class="post-footer">
      <div class="post-footer-inner">
        <button type="button" class="btn-draft" @click="saveDraft">存草稿</button>
        <button type="button" class="btn-publish" @click="publish">发布笔记</button>
      </div>
    </footer>
  </div>
</template>

<style lang="scss" scoped>
$primary: #e6a23c;
$text: #333;
$text2: #666;
$text3: #999;
$border: #eee;

.publish-post-view {
  width: 100%;
  max-width: min(960px, 100vw - 240px);
  margin: 0 auto;
  padding: 0 clamp(16px, 4vw, 32px) 120px;
  background: #fff;
  min-height: calc(100vh - 60px);
  border-radius: 0;
}

.post-header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #fff;
  border-bottom: 1px solid $border;
  padding: 14px 0;
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-back {
  padding: 8px 0;
  border: none;
  background: none;
  font-size: 16px;
  color: $text2;
  cursor: pointer;
  &:hover {
    color: $primary;
  }
}

.post-header .title {
  font-size: 17px;
  font-weight: 600;
  color: $text;
}

.post-form {
  padding-top: 20px;
}

.section {
  margin-bottom: 24px;
}

.field-label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  color: $text2;
}

.media-section {
  margin-bottom: 28px;
}

.media-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.media-item {
  position: relative;
  width: clamp(100px, 12vw, 140px);
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.media-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.btn-remove {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
}

.media-add {
  width: clamp(100px, 12vw, 140px);
  aspect-ratio: 1;
  border-radius: 12px;
  border: 1px dashed $border;
  background: #fafaf6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
  .plus {
    font-size: clamp(24px, 4vw, 32px);
    color: $text3;
  }
  .hint {
    font-size: 12px;
    color: $text3;
  }
  &:hover {
    border-color: $primary;
    background: #fef9f0;
  }
}

.hidden-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
  pointer-events: none;
}

.channel-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.channel-btn {
  padding: 10px 20px;
  border: 1px solid $border;
  background: #fff;
  border-radius: 24px;
  font-size: 14px;
  color: $text2;
  cursor: pointer;
  transition: all 0.2s;
  &:hover {
    border-color: $primary;
    color: $primary;
  }
  &.active {
    border-color: $primary;
    background: rgba(230, 162, 60, 0.12);
    color: $primary;
    font-weight: 500;
  }
}

.input-title {
  width: 100%;
  padding: 14px 0;
  border: none;
  border-bottom: 1px solid $border;
  font-size: 16px;
  color: $text;
  outline: none;
  &::placeholder {
    color: $text3;
  }
}

.input-body-wrap {
  position: relative;
  padding: 14px 40px 14px 0;
  border-bottom: 1px solid $border;
}

.input-body {
  width: 100%;
  padding: 0;
  border: none;
  font-size: 15px;
  color: $text;
  line-height: 1.6;
  outline: none;
  resize: none;
  &::placeholder {
    color: $text3;
  }
}

.mic-icon {
  position: absolute;
  right: 0;
  top: 14px;
  font-size: 20px;
  cursor: pointer;
  opacity: 0.7;
}

.tag-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.tag-btn {
  padding: 6px 12px;
  border: none;
  background: none;
  font-size: 14px;
  color: $text2;
  cursor: pointer;
  &:hover {
    color: $primary;
  }
}

.hashtag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hashtag-btn {
  padding: 6px 14px;
  border: 1px solid $border;
  background: #fafafa;
  border-radius: 20px;
  font-size: 13px;
  color: $text2;
  cursor: pointer;
  transition: all 0.2s;
  &:hover {
    border-color: $primary;
    color: $primary;
  }
  &.active {
    border-color: $primary;
    background: rgba(230, 162, 60, 0.12);
    color: $primary;
  }
}

.options-section {
  margin-top: 28px;
}

.option-row {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border: none;
  border-bottom: 1px solid #f5f5f5;
  background: none;
  font-size: 15px;
  color: $text;
  cursor: pointer;
  text-align: left;
  &:last-child {
    border-bottom: none;
  }
  &:hover {
    background: #fafafa;
  }
}

.option-icon {
  font-size: 18px;
}

.option-text {
  flex: 1;
}

.option-arrow {
  font-size: 18px;
  color: $text3;
}

.post-footer {
  position: fixed;
  left: 200px;
  right: 0;
  bottom: 0;
  z-index: 20;
  padding: 12px 0;
  padding-bottom: calc(12px + env(safe-area-inset-bottom, 0));
  background: #fff;
  border-top: 1px solid $border;
  display: flex;
  justify-content: center;
  box-sizing: border-box;
}

.post-footer-inner {
  display: flex;
  gap: 16px;
  width: 100%;
  max-width: min(960px, 100%);
  padding: 0 clamp(16px, 4vw, 32px);
  box-sizing: border-box;
}

@media (max-width: 900px) {
  .post-footer {
    left: 0;
  }
}

.btn-draft {
  flex: 1;
  padding: 14px 24px;
  border: 1px solid $border;
  background: #fff;
  border-radius: 24px;
  font-size: 16px;
  color: $text2;
  cursor: pointer;
  &:hover {
    background: #f5f5f5;
  }
}

.btn-publish {
  flex: 2;
  padding: 14px 24px;
  border: none;
  background: $primary;
  color: #fff;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  &:hover {
    opacity: 0.9;
  }
}
</style>
