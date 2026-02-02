import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { PostDetail } from '../types'

import type { UserProfile } from '../types'

// 当前登录用户（模拟，含个人主页所需字段）
export const useAppStore = defineStore('app', () => {
  const user = ref<UserProfile | null>({
    id: 1,
    nickname: '宠物爱好者',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=pet',
    accountId: 'Pet_Lover_01',
    ip: '广东',
    signature: '不定义美丽，只记录美好。领养代替购买～',
    gender: 'male',
    age: 28,
    location: '广东广州',
    profession: '宠物博主',
    followingCount: 304,
    followersCount: 1280,
    likesAndCollectCount: 5620,
  })

  // 当前打开的帖子详情（用于弹窗）
  const openedPostId = ref<number | null>(null)
  const openedPostDetail = ref<PostDetail | null>(null)

  const isPostModalOpen = computed(() => openedPostId.value != null)

  function openPost(id: number, detail?: PostDetail | null) {
    openedPostId.value = id
    openedPostDetail.value = detail ?? null
  }

  function closePost() {
    openedPostId.value = null
    openedPostDetail.value = null
  }

  return {
    user,
    openedPostId,
    openedPostDetail,
    isPostModalOpen,
    openPost,
    closePost,
  }
})
