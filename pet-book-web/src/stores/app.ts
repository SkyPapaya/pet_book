import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { PostDetail } from '../types'

import type { UserProfile, Pet } from '../types'

const mockPets: Pet[] = [
  {
    id: 1,
    name: '橘宝',
    avatar: 'https://picsum.photos/seed/cat1/400/400',
    species: '猫',
    breed: '橘猫',
    gender: 'male',
    birthday: '2022-05-10',
    ageText: '2岁',
    health: '健康',
    neutered: true,
  },
  {
    id: 2,
    name: '团子',
    avatar: 'https://picsum.photos/seed/dog1/400/400',
    species: '狗',
    breed: '柯基',
    gender: 'female',
    birthday: '2023-01-20',
    ageText: '1岁',
    health: '健康',
    neutered: false,
  },
  {
    id: 3,
    name: '咪咪',
    avatar: 'https://picsum.photos/seed/cat2/400/400',
    species: '猫',
    breed: '狸花猫',
    gender: 'female',
    birthday: '2021-08-15',
    ageText: '3岁',
    health: '有慢性肾病，定期复查',
    neutered: true,
  },
]

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
    pets: mockPets,
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
