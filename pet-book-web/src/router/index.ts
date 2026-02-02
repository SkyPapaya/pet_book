import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { title: '发现' },
    },
    {
      path: '/publish',
      name: 'publish',
      component: () => import('../views/PublishView.vue'),
      meta: { title: '发布' },
    },
    {
      path: '/publish/create',
      name: 'publishCreate',
      component: () => import('../views/PublishPostView.vue'),
      meta: { title: '发布笔记' },
    },
    {
      path: '/notification',
      name: 'notification',
      component: () => import('../views/NotificationView.vue'),
      meta: { title: '通知' },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { title: '个人中心' },
    },
  ],
})

router.afterEach((to) => {
  const title = (to.meta?.title as string) || '宠物书'
  document.title = title ? `${title} - 宠物书` : '宠物书'
})

export default router
