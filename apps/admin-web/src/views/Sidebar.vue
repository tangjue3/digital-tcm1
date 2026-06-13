<template>
  <header class="topbar">
    <div class="brand">
      <div class="brand__badge">
        <el-icon><Opportunity /></el-icon>
      </div>
      <div class="brand__copy">
        <div class="brand__title">云枢四诊</div>
        <div class="brand__subtitle">智能中医诊疗平台</div>
      </div>
    </div>

    <el-menu :default-active="activeIndex" class="topbar-menu" router mode="horizontal">
      <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ item.label }}</span>
      </el-menu-item>
    </el-menu>

    <div class="topbar-actions">
      <div class="notice">
        <el-icon><Bell /></el-icon>
        <span v-if="noticeCount > 0" class="notice__badge">{{ noticeCount }}</span>
      </div>
      <div class="doctor-card">
        <div class="doctor-card__avatar">张</div>
        <div class="doctor-card__copy">
          <div class="doctor-card__name">张医生</div>
          <div class="doctor-card__role">主任医师</div>
        </div>
        <el-icon class="doctor-card__arrow"><ArrowDown /></el-icon>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getConsultReportDashboard } from '@/api/system/consultReport'
import {
  ArrowDown,
  Bell,
  Service,
  ChatDotRound,
  Collection,
  Document,
  House,
  Opportunity,
  User,
} from '@element-plus/icons-vue'

const route = useRoute()
const activeIndex = computed(() => route.path)
const noticeCount = ref(0)

const menus = [
  { path: '/home/dashboard', label: '首页', icon: House },
  { path: '/home/cglist', label: '四诊综合分析报告单', icon: Document },
  { path: '/home/medicalCaseAnalysis', label: '医生诊疗案例分析', icon: Document },
  { path: '/home/aiTCMIntelligentAgent', label: '中医诊疗智能体', icon: Service },
  { path: '/home/aiTCMKnowledgeBase', label: '中医药知识库', icon: Collection },
  { path: '/home/medicalChat', label: '医患沟通', icon: ChatDotRound },
  { path: '/home/appUser', label: '用户管理', icon: User },
]

onMounted(async () => {
  try {
    const response = await getConsultReportDashboard()
    noticeCount.value = Number(response?.data?.highRiskCount ?? 0)
  } catch (error) {
    console.warn('[admin-web] sidebar notice fallback to 0', error)
  }
})
</script>

<style scoped>
.topbar {
  height: 68px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(227, 236, 250, 0.95);
  box-shadow: 0 12px 30px rgba(61, 109, 190, 0.08);
  border-radius: 20px;
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 16px;
  padding: 0 20px;
  position: sticky;
  top: 12px;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand__badge {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  background: linear-gradient(180deg, #5f8dff, #326be8);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 18px rgba(50, 107, 232, 0.24);
}

.brand__title {
  color: #1f355b;
  font-size: 18px;
  font-weight: 800;
  line-height: 1;
}

.brand__subtitle {
  margin-top: 4px;
  color: #8f9fba;
  font-size: 11px;
}

.topbar-menu {
  min-width: 0;
  border-bottom: 0 !important;
  background: transparent !important;
}

:deep(.el-menu--horizontal) {
  border-bottom: 0 !important;
}

:deep(.el-menu--horizontal > .el-menu-item) {
  height: 42px;
  line-height: 42px;
  margin: 0 6px;
  border-radius: 12px;
  border-bottom: 0 !important;
  color: #586f95;
  font-size: 14px;
  font-weight: 600;
  padding: 0 14px;
}

:deep(.el-menu--horizontal > .el-menu-item:hover) {
  color: #2f6feb !important;
  background: rgba(59, 121, 255, 0.08) !important;
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  color: #fff !important;
  background: linear-gradient(180deg, #4a86ff, #2d6fea) !important;
  box-shadow: 0 10px 18px rgba(45, 111, 234, 0.24);
}

:deep(.el-menu-item .el-icon) {
  margin-right: 8px;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.notice {
  width: 38px;
  height: 38px;
  border-radius: 14px;
  border: 1px solid #e8eef9;
  background: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #6980a8;
  position: relative;
}

.notice__badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #ff5d67;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  line-height: 18px;
  text-align: center;
}

.doctor-card {
  display: flex;
  align-items: center;
  gap: 10px;
}

.doctor-card__avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(180deg, #8fc6ff, #5f8dff);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
}

.doctor-card__name {
  color: #2c3f63;
  font-size: 14px;
  font-weight: 700;
}

.doctor-card__role {
  margin-top: 4px;
  color: #8a9dba;
  font-size: 12px;
}

.doctor-card__arrow {
  color: #8da0bd;
  font-size: 16px;
}

@media (max-width: 1280px) {
  .topbar {
    grid-template-columns: 1fr;
    height: auto;
    padding: 16px;
  }

  .topbar-menu {
    overflow-x: auto;
  }

  .topbar-actions {
    justify-content: flex-end;
  }
}
</style>
