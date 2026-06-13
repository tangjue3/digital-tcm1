<script setup>
import { computed, ref, onMounted } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import FloatingBall from './components/FloatingBall.vue'
import { logout } from '@/api/auth'
import { getToken, removeToken } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const isAuthed = computed(() => !['/', '/login'].includes(route.path) && Boolean(getToken()))
const backgroundLoaded = ref(false)

const handleLogout = async () => {
  try {
    await logout()
  } catch (error) {
    console.warn('[auth] logout request failed', error)
  } finally {
    removeToken()
    ElMessage.success('已退出登录')
    router.replace('/')
  }
}

onMounted(() => {
  const img = new Image()
  img.onload = () => {
    backgroundLoaded.value = true
  }
  img.onerror = () => {
    backgroundLoaded.value = true
  }
  img.src = '/background-1.png'
})
</script>

<template>
  <div class="app-container">
    <div class="app-background" :class="{ 'loaded': backgroundLoaded }"></div>
    <RouterView />
    <FloatingBall />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --theme-blue-900: #0f3d8f;
  --theme-blue-700: #1f5fca;
  --theme-blue-500: #3f8cff;
  --theme-blue-100: #e9f2ff;
  --theme-text: #103268;
}

.app-container {
  position: relative;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
}

.app-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background: rgba(245, 248, 255, 0.95) no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
  will-change: transform;
  transform: translateZ(0);
  opacity: 0;
  transition: opacity 0.8s ease-in-out;
}

.app-background.loaded {
  background: url('/background-1.png') no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
  opacity: 1;
}

.page-title {
  margin-left: 200px;
  min-height: 64px;
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 10px 28px;
  color: #fff;
  background: linear-gradient(120deg, var(--theme-blue-900) 0%, var(--theme-blue-700) 65%, var(--theme-blue-500) 100%);
  font-size: 21px;
  letter-spacing: 0.3px;
  box-shadow: 0 8px 24px rgba(31, 95, 202, 0.28);
}

.back-btn {
  margin-left: auto;
  border: none;
  background: #ffffff !important;
  color: var(--theme-blue-700) !important;
  font-weight: 700;
}

.back-btn:hover {
  background: var(--theme-blue-100) !important;
}

@media (max-width: 900px) {
  .page-title {
    margin-left: 0;
    padding: 12px 16px;
    font-size: 17px;
    line-height: 1.4;
    flex-wrap: wrap;
  }

  .back-btn {
    margin-left: 0;
  }
}
</style>
