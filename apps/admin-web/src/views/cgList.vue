<template>
  <div class="analysis-report-page">
    <div class="page-header">
      <div>
        <h2>中医四诊综合分析报告单</h2>
        <p class="subtitle">{{ pageSubtitle }}</p>
      </div>
      <el-tag v-if="presetScopeLabel" type="info" effect="plain" round class="scope-tag">
        {{ presetScopeLabel }}
      </el-tag>
    </div>

    <div class="filter-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索患者姓名、报告编号、主诉或病种摘要"
        :prefix-icon="Search"
        style="width: 320px; margin-right: 15px;"
      />
      <el-select v-model="filterRiskLevel" placeholder="风险等级" clearable style="width: 180px;">
        <el-option label="全部风险" value="" />
        <el-option label="高风险" value="high" />
        <el-option label="极高风险" value="critical" />
        <el-option label="建议关注" value="medium" />
        <el-option label="普通" value="normal" />
      </el-select>
    </div>

    <div v-loading="loading" class="patient-grid">
      <el-card
        v-for="patient in filteredPatients"
        :key="patient.id"
        class="patient-card"
        shadow="hover"
        @click="viewDetail(patient.id)"
      >
        <div class="card-header">
          <div class="patient-basic">
            <span class="name">{{ patient.name }}</span>
            <span class="gender-age">{{ patient.gender }} / {{ patient.age }}岁</span>
          </div>
          <div class="card-tags">
            <el-tag v-if="patient.isHandled" type="success" effect="light">已处理</el-tag>
            <el-tag :type="statusTagType(patient.status)" effect="light">{{ statusText(patient.status) }}</el-tag>
            <el-tag :type="riskTagType(patient.riskLevel)" effect="dark">
              {{ riskText(patient.riskLevel) }}
            </el-tag>
          </div>
        </div>

        <div class="card-body">
          <div class="info-row">
            <label>报告编号：</label>
            <span>{{ patient.reportNo }}</span>
          </div>
          <div class="info-row">
            <label>提交时间：</label>
            <span>{{ patient.date }}</span>
          </div>
          <div class="info-row">
            <label>主要问题：</label>
            <span class="issue">{{ patient.issue }}</span>
          </div>
          <div class="info-row">
            <label>病种摘要：</label>
            <span>{{ patient.diagnosis }}</span>
          </div>
        </div>

        <div class="card-footer">
          <el-button type="primary" link>查看详情 & 处理建议 >></el-button>
        </div>
      </el-card>

      <el-empty v-if="!loading && !filteredPatients.length" description="暂无报告数据" />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { listConsultReports } from '@/api/system/consultReport'

const router = useRouter()
const route = useRoute()

const searchQuery = ref('')
const filterRiskLevel = ref('')
const loading = ref(false)
const patients = ref([])

const scopeLabelMap = {
  pending: '待处理报告',
  today: '今日就诊',
  highRisk: '高风险预警',
  criticalRisk: '极高风险',
}

const presetScope = computed(() => {
  const scope = route.query.scope
  return typeof scope === 'string' ? scope : ''
})

const presetScopeLabel = computed(() => scopeLabelMap[presetScope.value] || '')

const pageSubtitle = computed(() => {
  if (presetScope.value === 'pending') {
    return '当前展示尚未处理的四诊分析报告单，便于医生优先跟进。'
  }
  if (presetScope.value === 'today') {
    return '当前展示今日提交的四诊分析报告单，帮助快速查看当日就诊情况。'
  }
  if (presetScope.value === 'highRisk') {
    return '当前展示高风险与极高风险报告单，便于优先处理预警病例。'
  }
  if (presetScope.value === 'criticalRisk') {
    return '当前展示极高风险报告单，便于快速跟进紧急病例。'
  }
  return '查看 App 用户提交的问诊报告，并进入详情页进一步处理。'
})

const filteredPatients = computed(() => {
  const keyword = searchQuery.value.trim()
  return patients.value.filter((patient) => {
    if (!matchPresetScope(patient)) {
      return false
    }

    const queryMatched = !keyword
      || patient.name.includes(keyword)
      || patient.issue.includes(keyword)
      || patient.diagnosis.includes(keyword)
      || patient.reportNo.includes(keyword)

    const riskMatched = filterRiskLevel.value ? patient.riskLevel === filterRiskLevel.value : true
    return queryMatched && riskMatched
  })
})

function matchPresetScope(patient) {
  if (presetScope.value === 'pending') {
    return patient.status === 'pending'
  }
  if (presetScope.value === 'today') {
    return patient.isToday
  }
  if (presetScope.value === 'highRisk') {
    return patient.riskLevel === 'high' || patient.riskLevel === 'critical'
  }
  if (presetScope.value === 'criticalRisk') {
    return patient.riskLevel === 'critical'
  }
  return true
}

function riskText(level) {
  const map = {
    normal: '普通',
    medium: '建议关注',
    high: '高风险',
    critical: '极高风险',
  }
  return map[level] || '普通'
}

function riskTagType(level) {
  if (level === 'critical' || level === 'high') {
    return 'danger'
  }
  if (level === 'medium') {
    return 'warning'
  }
  return 'info'
}

function statusText(status) {
  const map = {
    pending: '待处理',
    processing: '处理中',
    supplement_required: '需补充',
    completed: '已完成',
    rejected: '已驳回',
  }
  return map[status] || status || '未知状态'
}

function statusTagType(status) {
  if (status === 'completed') {
    return 'success'
  }
  if (status === 'processing') {
    return 'warning'
  }
  if (status === 'supplement_required' || status === 'rejected') {
    return 'danger'
  }
  return 'info'
}

function isToday(dateText) {
  if (!dateText) {
    return false
  }
  const date = new Date(dateText)
  if (Number.isNaN(date.getTime())) {
    return false
  }
  const now = new Date()
  return date.getFullYear() === now.getFullYear()
    && date.getMonth() === now.getMonth()
    && date.getDate() === now.getDate()
}

function isHandledReport(item) {
  return Boolean(item?.doctorAdvice || item?.doctorSummary)
    || item?.status === 'processing'
    || item?.status === 'completed'
}

function viewDetail(id) {
  router.push({
    path: '/home/patientReportDetail',
    query: {
      id,
      scope: presetScope.value || undefined,
    },
  })
}

function buildListParams() {
  const params = {
    pageNum: 1,
    pageSize: 500,
  }

  if (presetScope.value === 'pending') {
    params.status = 'pending'
  } else if (presetScope.value === 'today') {
    const now = new Date()
    const year = now.getFullYear()
    const month = `${now.getMonth() + 1}`.padStart(2, '0')
    const day = `${now.getDate()}`.padStart(2, '0')
    params.startTime = `${year}-${month}-${day} 00:00:00`
    params.endTime = `${year}-${month}-${day} 23:59:59`
  }

  return params
}

async function loadReports() {
  loading.value = true
  try {
    const response = await listConsultReports(buildListParams())
    const rows = response?.rows || []
    patients.value = rows.map((item) => ({
      id: item.reportId,
      reportNo: item.reportNo || '-',
      name: item.patientName || '未命名患者',
      gender: item.gender || '未知',
      age: item.age ?? '-',
      date: item.createTime || '-',
      issue: item.chiefComplaint || item.diseaseSummary || '暂无补充说明',
      diagnosis: item.diseaseSummary || '暂无病种摘要',
      riskLevel: item.highestRiskLevel || 'normal',
      status: item.status || 'pending',
      isToday: isToday(item.createTime),
      isHandled: isHandledReport(item),
    }))
  } catch (error) {
    ElMessage.error(error?.message || '获取报告列表失败')
  } finally {
    loading.value = false
  }
}

watch(() => route.query.scope, () => {
  loadReports()
})

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.analysis-report-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-header h2 {
  color: #333;
  margin: 0 0 5px;
}

.subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.scope-tag {
  margin-top: 4px;
}

.filter-bar {
  margin-bottom: 25px;
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.patient-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.patient-card {
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  border-radius: 12px;
}

.patient-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(139, 69, 19, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  gap: 12px;
}

.patient-basic .name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-right: 10px;
}

.patient-basic .gender-age {
  color: #999;
  font-size: 14px;
}

.card-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.card-body {
  color: #666;
  font-size: 14px;
}

.info-row {
  margin-bottom: 8px;
  display: flex;
}

.info-row label {
  width: 78px;
  color: #999;
  flex-shrink: 0;
}

.issue {
  color: #d2691e;
  font-weight: 500;
}

.card-footer {
  margin-top: 15px;
  text-align: right;
  border-top: 1px dashed #eee;
  padding-top: 10px;
}

:deep(.el-button--primary.is-link) {
  color: #d2691e;
}
</style>
