<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'

const router = useRouter()
const appStore = useAppStore()

const mode = ref<'login' | 'register'>('login')
const accountId = ref('')
const password = ref('')
const nickname = ref('')
const loading = ref(false)

function applyUserFromResponse(data: any) {
  localStorage.setItem('token', data.token)
  if (appStore.user) {
    appStore.user.id = data.userId
    appStore.user.nickname = data.nickname || appStore.user.nickname
    appStore.user.avatar = data.avatar || appStore.user.avatar
  }
}

async function onSubmit() {
  if (!accountId.value || !password.value) {
    alert('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    if (mode.value === 'login') {
      const res = await axios.post('/api/auth/login', {
        accountId: Number(accountId.value),
        password: password.value,
      })
      if (res.data.code !== 200) {
        alert(res.data.msg || '登录失败')
        return
      }
      applyUserFromResponse(res.data.data)
      alert('登录成功')
    } else {
      const res = await axios.post('/api/auth/register', {
        accountId: Number(accountId.value),
        password: password.value,
        nickname: nickname.value || undefined,
      })
      if (res.data.code !== 200) {
        alert(res.data.msg || '注册失败')
        return
      }
      applyUserFromResponse(res.data.data)
      alert('注册并登录成功')
    }
    router.push('/')
  } catch (e) {
    alert(mode.value === 'login' ? '登录失败' : '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="card">
      <h1 class="title">
        {{ mode === 'login' ? '登录宠物书' : '注册宠物书' }}
      </h1>
      <div class="tabs">
        <button
          type="button"
          class="tab-btn"
          :class="{ active: mode === 'login' }"
          @click="mode = 'login'"
        >
          登录
        </button>
        <button
          type="button"
          class="tab-btn"
          :class="{ active: mode === 'register' }"
          @click="mode = 'register'"
        >
          注册
        </button>
      </div>
      <label class="field">
        <span class="label">账号 ID（数字）</span>
        <input v-model="accountId" type="text" placeholder="例如：1001" />
      </label>
      <label v-if="mode === 'register'" class="field">
        <span class="label">昵称（可选）</span>
        <input v-model="nickname" type="text" placeholder="请输入昵称" />
      </label>
      <label class="field">
        <span class="label">密码</span>
        <input v-model="password" type="password" placeholder="请输入密码" />
      </label>
      <button class="btn" type="button" :disabled="loading" @click="onSubmit">
        {{ loading ? (mode === 'login' ? '登录中...' : '注册中...') : mode === 'login' ? '登录' : '注册' }}
      </button>
      <p class="tip" v-if="mode === 'login'">可以先在数据库 user 表插入一条测试账号（account_id + password）。</p>
      <p class="tip" v-else>注册会直接创建账号并自动登录。</p>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f6f1;
}

.card {
  width: 360px;
  padding: 24px 28px 32px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}

.title {
  margin: 0 0 16px;
  font-size: 20px;
  font-weight: 600;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.tab-btn {
  flex: 1;
  padding: 6px 0;
  border-radius: 999px;
  border: 1px solid #ddd;
  background: #f7f7f7;
  font-size: 14px;
  cursor: pointer;
}

.tab-btn.active {
  border-color: #e6a23c;
  background: #fff7e6;
  color: #e6a23c;
  font-weight: 600;
}

.field {
  display: flex;
  flex-direction: column;
  margin-bottom: 14px;
}

.label {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
}

input {
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
}

.btn {
  width: 100%;
  margin-top: 10px;
  padding: 10px 14px;
  border: none;
  border-radius: 999px;
  background: #e6a23c;
  color: #fff;
  font-size: 15px;
  cursor: pointer;
}

.tip {
  margin-top: 12px;
  font-size: 12px;
  color: #999;
}
</style>

