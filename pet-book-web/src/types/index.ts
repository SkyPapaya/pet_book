// 帖子卡片（列表用）
export interface PostCard {
  id: number
  title: string
  coverUrl: string
  /** 封面展示比例（用于瀑布流大小不一） */
  coverAspectRatio?: number
  authorName: string
  authorAvatar: string
  likeCount: number
  collectCount?: number
  commentCount?: number
  channel?: string // 领养 | 养宠知识 | 求助
  /** 详情正文（演示数据用） */
  desc?: string
}

// 帖子详情（弹窗用）
export interface PostDetail {
  id: number
  title: string
  desc: string
  imageUrls: string[]
  authorId: number
  authorName: string
  authorAvatar: string
  likeCount: number
  collectCount: number
  commentCount: number
  isLiked?: boolean
  isCollected?: boolean
  isFollowed?: boolean
  createdAt?: string
  publishIp?: string
}

// 评论
export interface Comment {
  id: number
  postId: number
  userId: number
  userName: string
  userAvatar: string
  content: string
  likeCount: number
  isLiked?: boolean
  createdAt: string
  replies?: Comment[]
  replyCount?: number
  parentId?: number
}

// 用户简要
export interface UserBrief {
  id: number
  nickname: string
  avatar: string
  bio?: string
  ip?: string
}

// 个人主页用户信息（宠物书账号、IP、签名、性别年龄职业、关注/粉丝/获赞与收藏）
export interface UserProfile {
  id: number
  nickname: string
  avatar: string
  /** 宠物书账号（唯一标识） */
  accountId: string
  /** IP 属地 */
  ip: string
  /** 签名/简介 */
  signature: string
  /** 性别 */
  gender: 'male' | 'female' | ''
  /** 年龄 */
  age?: number
  /** 所在地，如 江苏南京 */
  location?: string
  /** 职业 */
  profession?: string
  /** 关注数量 */
  followingCount: number
  /** 粉丝数量 */
  followersCount: number
  /** 获赞与收藏总数 */
  likesAndCollectCount: number
}
