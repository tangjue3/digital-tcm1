<template>
  <div class="dashboard-page">
    <section class="dashboard-main">
      <div class="dashboard-top">
        <div class="welcome-banner">
          <div class="welcome-copy">
            <h1>欢迎回来，张医生</h1>
            <p class="welcome-date">{{ currentDate }}</p>
            <p class="welcome-desc">智能四诊、精准分析、辅助决策，提升诊疗效率。</p>
          </div>
          <div class="welcome-visual">
            <div class="visual-ring ring-lg"></div>
            <div class="visual-ring ring-md"></div>
            <div class="visual-shield">
              <el-icon><Plus /></el-icon>
            </div>
            <div class="visual-card visual-card--main"></div>
            <div class="visual-card visual-card--side"></div>
          </div>
        </div>

        <div class="focus-card">
          <div class="section-head section-head--interactive" @click="goToCriticalRiskList">
            <h3>紧急关注（{{ focusTotal }}）</h3>
            <a href="javascript:void(0)" @click.stop="goToCriticalRiskList">查看全部</a>
          </div>
          <div class="focus-list">
            <div
              v-for="patient in highRiskPatients"
              :key="patient.id"
              class="focus-item focus-item--interactive"
              @click="viewFocusPatient(patient.id)"
            >
              <div class="focus-item__avatar">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="focus-item__body">
                <div class="focus-item__name">{{ patient.name }}</div>
                <div class="focus-item__desc">{{ patient.issue }}</div>
              </div>
              <span class="focus-item__tag">{{ getRiskLabel(patient.riskLevel) }}</span>
            </div>
            <div v-if="showFocusOverflowHint" class="focus-overflow" @click="goToCriticalRiskList">
              ...
            </div>
          </div>
        </div>
      </div>

      <div class="stats-grid">
        <div
          v-for="card in stats"
          :key="card.label"
          class="stat-card"
          :class="{ 'stat-card--clickable': Boolean(card.path) }"
          @click="navigateStatCard(card)"
        >
          <div class="stat-card__icon" :class="card.theme">
            <el-icon><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ card.value }}</div>
            <div class="stat-card__label">{{ card.label }}</div>
            <div class="stat-card__delta">
              <span>{{ card.compareLabel }}</span>
              <span :class="card.deltaClass">{{ card.delta }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="chart-grid">
        <div class="panel panel-trend">
          <div class="section-head">
            <h3>近期问诊趋势分析</h3>
            <div class="switcher">
              <button
                v-for="period in periods"
                :key="period.value"
                class="switcher-btn"
                :class="{ active: chartPeriod === period.value }"
                @click="chartPeriod = period.value"
              >
                {{ period.label }}
              </button>
            </div>
          </div>
          <div ref="trendChartRef" class="trend-chart"></div>
        </div>

        <div class="panel panel-distribution">
          <div class="section-head">
            <h3>本周病种分布</h3>
          </div>
          <div class="distribution-wrap">
            <div ref="pieChartRef" class="distribution-chart"></div>
            <div class="distribution-legend">
              <div v-for="item in diseaseDistribution" :key="item.name" class="distribution-legend__item">
                <span class="distribution-legend__dot" :style="{ backgroundColor: item.color }"></span>
                <span class="distribution-legend__name">{{ item.name }}</span>
                <span class="distribution-legend__value">{{ item.percent }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  Document,
  DocumentChecked,
  Opportunity,
  Plus,
  UserFilled,
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getConsultReportDashboard, getUrgentConsultReports } from '@/api/system/consultReport'
import { getTcmKbStatus } from '@/api/tcmAi'

const router = useRouter()

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  })
})

const stats = ref([
  {
    label: '待处理报告',
    value: '12',
    compareLabel: '更新时间',
    delta: '--',
    deltaClass: 'neutral',
    icon: DocumentChecked,
    theme: 'theme-blue',
    path: '/home/cglist',
    scope: 'pending',
  },
  {
    label: '今日就诊',
    value: '45',
    compareLabel: '更新时间',
    delta: '--',
    deltaClass: 'neutral',
    icon: UserFilled,
    theme: 'theme-sky',
    path: '/home/cglist',
    scope: 'today',
  },
  {
    label: '高风险预警',
    value: '3',
    compareLabel: '更新时间',
    delta: '--',
    deltaClass: 'neutral',
    icon: Opportunity,
    theme: 'theme-warn',
    path: '/home/cglist',
    scope: 'highRisk',
  },
  {
    label: '知识库片段',
    value: '0',
    compareLabel: '知识库状态',
    delta: '--',
    deltaClass: 'neutral',
    icon: Document,
    theme: 'theme-indigo',
    path: '/home/aiTCMKnowledgeBase',
  },
])

function navigateStatCard(card) {
  if (!card?.path) {
    return
  }
  router.push({
    path: card.path,
    query: card.scope ? { scope: card.scope } : {},
  })
}

const highRiskPatients = ref([
  { id: 1, name: '患者甲', issue: '高风险报告待处理', riskLevel: 'high' },
  { id: 2, name: '患者乙', issue: '疑似紧急情况需优先关注', riskLevel: 'critical' },
  { id: 3, name: '患者丙', issue: '医生建议尽快跟进', riskLevel: 'high' },
])
const focusTotal = ref(0)
const showFocusOverflowHint = computed(() => focusTotal.value > highRiskPatients.value.length)

function goToCriticalRiskList() {
  router.push({
    path: '/home/cglist',
    query: { scope: 'criticalRisk' },
  })
}

function viewFocusPatient(reportId) {
  if (!reportId) {
    return
  }
  router.push({
    path: '/home/patientReportDetail',
    query: { id: reportId },
  })
}

const diseaseDistribution = ref([
  { name: '消化调理', value: 32, percent: '32%', color: '#3d7bff' },
  { name: '心脑血管', value: 24, percent: '24%', color: '#4cc3dd' },
  { name: '呼吸相关', value: 18, percent: '18%', color: '#7f72f2' },
  { name: '骨伤疼痛', value: 16, percent: '16%', color: '#79cf95' },
  { name: '其他', value: 10, percent: '10%', color: '#ffbe67' },
])

const periods = [
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
]

const chartPeriod = ref('week')
const trendChartRef = ref(null)
const pieChartRef = ref(null)

let trendChart
let pieChart
const distributionPalette = ['#3d7bff', '#4cc3dd', '#7f72f2', '#79cf95', '#ffbe67', '#ff8b7b']

const trendSeriesMap = {
  week: {
    visits: [16, 24, 20, 31, 28, 42, 39],
    reports: [6, 10, 7, 14, 11, 18, 15],
  },
  month: {
    visits: [22, 26, 29, 24, 32, 36, 41],
    reports: [9, 12, 11, 10, 15, 17, 19],
  },
}
const trendLabelsMap = ref({
  week: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
  month: ['第1周', '第2周', '第3周', '第4周', '第5周', '第6周', '第7周'],
})

function initTrendChart() {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  renderTrendChart()
}

function renderTrendChart() {
  if (!trendChart) return
  const data = trendSeriesMap[chartPeriod.value]
  const labels = trendLabelsMap.value[chartPeriod.value] || []
  const maxValue = Math.max(10, ...data.visits, ...data.reports)
  const interval = Math.max(2, Math.ceil(maxValue / 5))
  const axisLabelInterval = labels.length > 10 ? Math.ceil(labels.length / 6) - 1 : 0
  trendChart.setOption({
    animation: false,
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(16, 39, 87, 0.92)',
      borderWidth: 0,
      textStyle: { color: '#fff' },
    },
    legend: {
      top: 4,
      right: 10,
      icon: 'circle',
      itemWidth: 8,
      itemHeight: 8,
      textStyle: {
        color: '#7d8fb3',
        fontSize: 12,
      },
      data: ['问诊数量', '高风险数量'],
    },
    grid: {
      top: 42,
      left: 16,
      right: 16,
      bottom: 16,
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: { lineStyle: { color: '#edf2fb' } },
      axisTick: { show: false },
      axisLabel: { color: '#98a8c3', fontSize: 12 },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 50,
      interval: 10,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#98a8c3', fontSize: 12 },
      splitLine: {
        lineStyle: {
          color: '#ecf2fb',
          type: 'dashed',
        },
      },
    },
    series: [
      {
        name: '问诊数量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: data.visits,
        lineStyle: { width: 3, color: '#3d7bff' },
        itemStyle: { color: '#3d7bff', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(61, 123, 255, 0.18)' },
            { offset: 1, color: 'rgba(61, 123, 255, 0)' },
          ]),
        },
      },
      {
        name: '高风险数量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        data: data.reports,
        lineStyle: { width: 2, color: '#6cc67c' },
        itemStyle: { color: '#6cc67c', borderColor: '#fff', borderWidth: 2 },
      },
    ],
  })
  trendChart.setOption({
    legend: {
      data: ['报告数量', '高风险数量'],
    },
    xAxis: {
      data: labels,
      axisLabel: {
        color: '#98a8c3',
        fontSize: 12,
        interval: axisLabelInterval,
      },
    },
    yAxis: {
      min: 0,
      max: interval * 5,
      interval,
      axisLabel: { color: '#98a8c3', fontSize: 12 },
    },
    series: [
      {
        name: '报告数量',
        data: data.visits,
      },
      {
        name: '高风险数量',
        data: data.reports,
      },
    ],
  })
}

function initPieChart() {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  const chartData = diseaseDistribution.value.length
    ? diseaseDistribution.value
    : [{ name: '暂无数据', value: 1, percent: '--', color: '#d9e2f2' }]
  pieChart.setOption({
    animation: false,
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['54%', '78%'],
        center: ['42%', '50%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          position: 'center',
          formatter: '{title|病种分布}\n{value|占比}',
          rich: {
            title: {
              fontSize: 18,
              fontWeight: 700,
              color: '#29456f',
              lineHeight: 28,
            },
            value: {
              fontSize: 14,
              color: '#6e82a6',
            },
          },
        },
        labelLine: { show: false },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 6,
        },
        data: chartData.map((item) => ({
          value: item.value,
          name: item.name,
          itemStyle: { color: item.color },
        })),
      },
    ],
  })
}

function resizeCharts() {
  trendChart?.resize()
  pieChart?.resize()
}

function sumTrendValues(values) {
  if (!Array.isArray(values)) {
    return 0
  }
  return values.reduce((sum, item) => sum + Number(item || 0), 0)
}

function decorateDiseaseDistribution(items = []) {
  const validItems = Array.isArray(items) ? items : []
  if (!validItems.length) {
    return []
  }
  const total = validItems.reduce((sum, item) => sum + Number(item?.value || 0), 0)
  return validItems.map((item, index) => {
    const value = Number(item?.value || 0)
    const percent = total > 0 ? `${Math.round((value / total) * 100)}%` : '0%'
    return {
      name: item?.name || `第${index + 1}类`,
      value,
      percent,
      color: distributionPalette[index % distributionPalette.length],
    }
  })
}

function getRiskLabel(riskLevel) {
  if (riskLevel === 'critical') {
    return '极高风险'
  }
  if (riskLevel === 'high') {
    return '高风险'
  }
  if (riskLevel === 'medium') {
    return '建议关注'
  }
  return '普通'
}

async function syncDashboardSummary() {
  try {
    const [dashboardResponse, urgentResponse, kbStatusResponse] = await Promise.all([
      getConsultReportDashboard(),
      getUrgentConsultReports({ limit: 3 }),
      getTcmKbStatus(),
    ])
    const dashboard = dashboardResponse?.data || {}
    const kbStatus = kbStatusResponse?.data || {}
    const nextStats = [...stats.value]
    nextStats[0] = {
      ...nextStats[0],
      value: String(dashboard.pendingCount ?? 0),
      compareLabel: '更新时间',
      delta: dashboard.updatedAt || '--',
      deltaClass: 'neutral',
    }
    nextStats[1] = {
      ...nextStats[1],
      value: String(dashboard.todayVisitCount ?? 0),
      compareLabel: '近7天总数',
      delta: String(sumTrendValues(dashboard.weekTrend?.reports)),
      deltaClass: 'neutral',
    }
    nextStats[2] = {
      ...nextStats[2],
      value: String(dashboard.highRiskCount ?? 0),
      compareLabel: '近7天高风险',
      delta: String(sumTrendValues(dashboard.weekTrend?.highRisk)),
      deltaClass: 'neutral',
    }
    nextStats[3] = {
      ...nextStats[3],
      value: String(kbStatus.chunkCount ?? 0),
      compareLabel: '知识库文件数',
      delta: String(kbStatus.fileCount ?? 0),
      deltaClass: 'neutral',
    }
    stats.value = nextStats
    focusTotal.value = Number(dashboard.highRiskCount ?? 0)

    if (dashboard.weekTrend) {
      trendSeriesMap.week.visits = Array.isArray(dashboard.weekTrend.reports)
        ? dashboard.weekTrend.reports.map((item) => Number(item || 0))
        : [0]
      trendSeriesMap.week.reports = Array.isArray(dashboard.weekTrend.highRisk)
        ? dashboard.weekTrend.highRisk.map((item) => Number(item || 0))
        : [0]
      trendLabelsMap.value.week = Array.isArray(dashboard.weekTrend.labels) && dashboard.weekTrend.labels.length
        ? dashboard.weekTrend.labels
        : trendLabelsMap.value.week
    }

    if (dashboard.monthTrend) {
      trendSeriesMap.month.visits = Array.isArray(dashboard.monthTrend.reports)
        ? dashboard.monthTrend.reports.map((item) => Number(item || 0))
        : [0]
      trendSeriesMap.month.reports = Array.isArray(dashboard.monthTrend.highRisk)
        ? dashboard.monthTrend.highRisk.map((item) => Number(item || 0))
        : [0]
      trendLabelsMap.value.month = Array.isArray(dashboard.monthTrend.labels) && dashboard.monthTrend.labels.length
        ? dashboard.monthTrend.labels
        : trendLabelsMap.value.month
    }

    diseaseDistribution.value = decorateDiseaseDistribution(dashboard.diseaseDistribution || [])
    highRiskPatients.value = (urgentResponse?.data || []).slice(0, 3).map((item) => ({
      id: item.reportId,
      name: item.patientName || '未命名患者',
      issue: item.diseaseSummary || item.summary || '暂无补充说明',
      riskLevel: item.highestRiskLevel || 'high',
    }))
  } catch (error) {
    console.warn('[admin-web] sync dashboard summary failed', error)
  }
}

watch(chartPeriod, () => {
  renderTrendChart()
})

onMounted(async () => {
  await syncDashboardSummary()
  nextTick(() => {
    initTrendChart()
    initPieChart()
  })
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.dashboard-page {
  min-height: calc(100vh - 96px);
}

.panel,
.focus-card,
.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(225, 235, 250, 0.96);
  box-shadow: 0 10px 30px rgba(61, 109, 190, 0.08);
}

.dashboard-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dashboard-top {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 12px;
}

.welcome-banner {
  opacity: 0.8;
  min-height: 168px;
  padding: 20px 18px;
  border-radius: 22px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  overflow: hidden;
  position: relative;
  background:
    linear-gradient(115deg, rgba(255, 255, 255, 0.08), transparent 42%),
    linear-gradient(90deg, #1b49c9 0%, #205ed8 40%, #2d7df1 100%);
  box-shadow: 0 18px 40px rgba(34, 94, 215, 0.24);
}

.welcome-banner::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.08) 1px, transparent 1px),
    linear-gradient(rgba(255, 255, 255, 0.08) 1px, transparent 1px);
  background-size: 96px 96px, 96px 96px;
  opacity: 0.2;
  pointer-events: none;
}

.welcome-copy {
  position: relative;
  z-index: 1;
}

.welcome-copy h1 {
  margin: 0;
  font-size: 18px;
  line-height: 1.4;
  font-weight: 800;
}

.welcome-date,
.welcome-desc {
  margin: 12px 0 0;
  color: rgba(234, 242, 255, 0.96);
  font-size: 14px;
}

.welcome-desc {
  margin-top: 20px;
  color: rgba(220, 232, 255, 0.92);
}

.welcome-visual {
  width: 300px;
  position: relative;
  flex-shrink: 0;
}

.visual-ring {
  position: absolute;
  border: 1px solid rgba(167, 212, 255, 0.4);
  border-radius: 50%;
}

.ring-lg {
  width: 230px;
  height: 84px;
  bottom: 0;
  left: 20px;
}

.ring-md {
  width: 164px;
  height: 58px;
  bottom: 12px;
  left: 54px;
}

.visual-shield {
  position: absolute;
  right: 94px;
  top: 36px;
  width: 84px;
  height: 94px;
  border-radius: 26px;
  background: linear-gradient(180deg, rgba(143, 214, 255, 0.34), rgba(36, 121, 255, 0.72));
  border: 1px solid rgba(255, 255, 255, 0.32);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 42px;
  color: #fff;
  box-shadow: 0 18px 32px rgba(18, 77, 204, 0.28);
}

.visual-card {
  position: absolute;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.22), rgba(255, 255, 255, 0.08));
  box-shadow: 0 12px 20px rgba(18, 77, 204, 0.2);
}

.visual-card--main {
  width: 92px;
  height: 118px;
  right: 24px;
  top: 24px;
  transform: rotate(7deg);
}

.visual-card--side {
  width: 74px;
  height: 96px;
  right: 130px;
  top: 12px;
  transform: rotate(-6deg);
}

.focus-card {
  opacity: 0.8;
  border-radius: 22px;
  padding: 14px 14px 8px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.section-head--interactive {
  cursor: pointer;
}

.section-head h3 {
  margin: 0;
  color: #213b67;
  font-size: 18px;
  font-weight: 800;
}

.section-head a {
  color: #3b79ff;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
}

.focus-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.focus-overflow {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 28px;
  color: #7f90ad;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 4px;
  cursor: pointer;
  user-select: none;
}

.focus-item {
  opacity: 1;
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border-radius: 16px;
  background: linear-gradient(180deg, #fff, #f8fbff);
  border: 1px solid #eef3fb;
}

.focus-item--interactive {
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.focus-item--interactive:hover {
  transform: translateY(-1px);
  border-color: #dbe6fb;
  box-shadow: 0 10px 24px rgba(52, 94, 166, 0.08);
}

.focus-item__avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(180deg, #5e97ff, #2d70f0);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.focus-item__name {
  color: #253b64;
  font-size: 14px;
  font-weight: 700;
}

.focus-item__desc {
  margin-top: 4px;
  color: #93a0b5;
  font-size: 12px;
}

.focus-item__tag {
  padding: 4px 10px;
  border-radius: 999px;
  background: #fff2f0;
  color: #ff6a61;
  font-size: 12px;
  font-weight: 700;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.stat-card {
  opacity: 0.8;
  border-radius: 20px;
  padding: 14px 14px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-card--clickable {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card--clickable:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 34px rgba(61, 109, 190, 0.12);
}

.stat-card__icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.theme-blue { background: linear-gradient(180deg, #6f8fff, #5662f3); }
.theme-sky { background: linear-gradient(180deg, #5fa4ff, #3677f5); }
.theme-warn { background: linear-gradient(180deg, #ff8a7b, #ff6a61); }
.theme-indigo { background: linear-gradient(180deg, #5f8dff, #3e73f7); }

.stat-card__value {
  color: #223962;
  font-size: 18px;
  font-weight: 800;
  line-height: 1;
}

.stat-card__label {
  margin-top: 6px;
  color: #60779d;
  font-size: 14px;
  font-weight: 600;
}

.stat-card__delta {
  margin-top: 8px;
  font-size: 12px;
  color: #9caac1;
}

.stat-card__delta .up {
  color: #5ab96f;
  margin-left: 4px;
}

.stat-card__delta .down {
  color: #ff845f;
  margin-left: 4px;
}

.stat-card__delta .neutral {
  color: #5a6f94;
  margin-left: 4px;
}

.chart-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 1fr);
  gap: 12px;
}

.panel {
  border-radius: 22px;
  padding: 14px 14px;
}

.trend-chart {
  height: 330px;
}

.switcher {
  display: inline-flex;
  gap: 6px;
  padding: 4px;
  border-radius: 12px;
  background: #f5f8ff;
}

.switcher-btn {
  border: 0;
  background: transparent;
  color: #90a1bc;
  border-radius: 10px;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.switcher-btn.active {
  background: linear-gradient(180deg, #4b86ff, #2d6feb);
  color: #fff;
  box-shadow: 0 8px 16px rgba(44, 110, 237, 0.24);
}

.distribution-wrap {
  display: grid;
  grid-template-columns: 230px minmax(0, 1fr);
  align-items: center;
  gap: 12px;
  min-height: 330px;
}

.distribution-chart {
  height: 260px;
}

.distribution-legend {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.distribution-legend__item {
  display: flex;
  align-items: center;
  color: #5f7499;
  font-size: 13px;
}

.distribution-legend__name {
  margin-left: 10px;
  margin-right: 8px;
}

.distribution-legend__value {
  color: #7f8fa9;
}

.distribution-legend__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.distribution-legend__value {
  color: #7f8fa9;
}

@media (max-width: 1280px) {
  .dashboard-top,
  .chart-grid,
  .stats-grid,
  .distribution-wrap {
    grid-template-columns: 1fr;
  }

  .welcome-visual {
    display: none;
  }
}
</style>



