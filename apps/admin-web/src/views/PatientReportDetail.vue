<template>
  <div class="report-detail-page" v-loading="loading">
    <div class="page-header">
      <el-button @click="$router.go(-1)" icon="Back" circle />
      <h2>中医四诊综合分析报告详情</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="box-card patient-info-card">
          <template #header>
            <div class="card-header">
              <span>患者基本信息</span>
              <el-tag :type="riskTagType" effect="dark">{{ riskTagText }}</el-tag>
            </div>
          </template>
          <div class="patient-info">
            <el-descriptions border :column="2">
              <el-descriptions-item label="报告编号">{{ report.reportNo || '-' }}</el-descriptions-item>
              <el-descriptions-item label="报告状态">{{ statusText(report.status) }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ report.patientName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ report.gender || '-' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ report.age ?? '-' }}岁</el-descriptions-item>
              <el-descriptions-item label="就诊类型">{{ report.visitType || '-' }}</el-descriptions-item>
              <el-descriptions-item label="提交时间">{{ report.createTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="基础病">{{ report.basicDisease || '无/未填写' }}</el-descriptions-item>
              <el-descriptions-item label="药物过敏史">{{ formatConditional(report.allergyHistoryType, report.allergyHistoryDetail) }}</el-descriptions-item>
              <el-descriptions-item label="既往病史">{{ formatConditional(report.pastHistoryType, report.pastHistoryDetail) }}</el-descriptions-item>
              <el-descriptions-item label="当前用药">{{ formatConditional(report.currentMedicationType, report.currentMedicationDetail) }}</el-descriptions-item>
              <el-descriptions-item label="影响生活">{{ formatArray(report.lifeImpact) }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <el-card class="box-card diagnosis-card">
          <template #header>
            <div class="card-header">
              <span>问诊报告内容</span>
              <el-tag type="warning">仅供医生进一步评估</el-tag>
            </div>
          </template>
          <div class="diagnosis-content">
            <h3>主诉与补充说明</h3>
            <p>{{ report.chiefComplaint || '暂无补充说明' }}</p>

            <h3>病种/症状选择</h3>
            <div v-if="diseaseList.length" class="disease-list">
              <div v-for="item in diseaseList" :key="item.id || item.diseaseCode" class="disease-item">
                <div class="disease-item__title">
                  <span>{{ item.diseaseName || '-' }}</span>
                  <el-tag :type="riskLevelToTag(item.riskLevel)" size="small">{{ riskLevelText(item.riskLevel) }}</el-tag>
                </div>
                <div class="disease-item__meta">编码：{{ item.diseaseCode || '-' }}</div>
                <div class="disease-item__meta">路径：{{ item.diseasePath || '-' }}</div>
                <div class="disease-item__meta" v-if="item.riskReason">提示：{{ item.riskReason }}</div>
              </div>
            </div>
            <p v-else>暂无病种选择记录</p>

            <h3>辅助资料</h3>
            <div v-if="attachmentList.length" class="attachment-list">
              <div v-for="item in attachmentList" :key="item.id || item.fileUrl" class="attachment-item">
                <div class="attachment-item__type">{{ attachmentTypeText(item.fileType) }}</div>
                <div class="attachment-item__name">{{ item.fileName || '-' }}</div>
                <a class="attachment-item__link" :href="item.fileUrl" target="_blank" rel="noreferrer">查看附件</a>
              </div>
            </div>
            <p v-else>暂无附件资料</p>

            <div class="alert-box" v-if="report.highestRiskLevel === 'high' || report.highestRiskLevel === 'critical'">
              <el-alert
                title="该类情况可能需要尽快线下就医，请结合实际情况进一步判断。"
                type="warning"
                show-icon
                :closable="false"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="box-card push-card">
          <template #header>
            <div class="card-header">
              <span>处理建议</span>
            </div>
          </template>

          <div class="push-section">
            <h4>医生建议</h4>
            <el-input
              v-model="pushMessage.reminder"
              type="textarea"
              :rows="5"
              placeholder="请输入处理建议或复诊提醒"
            />
          </div>

          <div class="push-section">
            <h4>补充说明</h4>
            <el-input
              v-model="pushMessage.advice"
              type="textarea"
              :rows="5"
              placeholder="请输入补充说明"
            />
          </div>

          <div class="push-actions">
            <el-button type="primary" size="large" @click="handlePush" block style="width: 100%;">
              保存处理建议
            </el-button>
            <p class="hint">当前第一阶段仅保存状态与医生建议字段，不做复杂工作流。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getConsultReport, updateConsultReportStatus } from '@/api/system/consultReport'

const route = useRoute()
const patientId = route.query.id

const loading = ref(false)
const report = ref({})
const diseaseList = ref([])
const attachmentList = ref([])
const pushMessage = ref({
  reminder: '',
  advice: ''
})

const riskTagText = computed(() => riskLevelText(report.value.highestRiskLevel))
const riskTagType = computed(() => riskLevelToTag(report.value.highestRiskLevel))

function formatConditional(type, detail) {
  if (!type) return '未填写'
  return type === '有' && detail ? `有，${detail}` : type
}

function formatArray(value) {
  if (!value) return '未填写'
  if (Array.isArray(value)) return value.join('、')
  return String(value).split(',').filter(Boolean).join('、') || '未填写'
}

function statusText(status) {
  const map = {
    pending: '待处理',
    processing: '处理中',
    supplement_required: '需补充',
    completed: '已完成',
    rejected: '已驳回'
  }
  return map[status] || status || '-'
}

function riskLevelText(level) {
  const map = {
    normal: '普通',
    medium: '建议关注',
    high: '高风险',
    critical: '极高风险'
  }
  return map[level] || '普通'
}

function riskLevelToTag(level) {
  if (level === 'critical' || level === 'high') return 'danger'
  if (level === 'medium') return 'warning'
  return 'info'
}

function attachmentTypeText(fileType) {
  const map = {
    tongue_image: '舌象照片',
    face_image: '面部照片',
    affected_part_image: '患处照片',
    voice: '语音描述',
    inspection_report: '检查报告',
    other: '其他附件'
  }
  return map[fileType] || '附件'
}

async function loadDetail() {
  if (!patientId) {
    ElMessage.warning('缺少报告编号')
    return
  }
  loading.value = true
  try {
    const response = await getConsultReport(patientId)
    const data = response?.data || {}
    report.value = data
    diseaseList.value = data.diseaseList || []
    attachmentList.value = data.attachmentList || []
    pushMessage.value.reminder = data.doctorAdvice || ''
    pushMessage.value.advice = data.doctorSummary || ''
  } catch (error) {
    ElMessage.error(error?.message || '获取报告详情失败')
  } finally {
    loading.value = false
  }
}

async function handlePush() {
  try {
    await updateConsultReportStatus(patientId, {
      status: report.value.status === 'completed' ? 'completed' : 'processing',
      doctorAdvice: pushMessage.value.reminder,
      doctorSummary: pushMessage.value.advice
    })
    ElMessage.success('处理建议已保存')
    loadDetail()
  } catch (error) {
    ElMessage.error(error?.message || '保存处理建议失败')
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.report-detail-page {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin-left: 15px;
  color: #333;
}

.box-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.diagnosis-content h3 {
  color: #8b4513;
  margin-top: 15px;
  margin-bottom: 8px;
  font-size: 16px;
  border-left: 4px solid #d2691e;
  padding-left: 10px;
}

.diagnosis-content p {
  color: #666;
  line-height: 1.6;
  background: #fafafa;
  padding: 10px;
  border-radius: 4px;
}

.disease-list,
.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.disease-item,
.attachment-item {
  background: #fafafa;
  border-radius: 8px;
  padding: 12px;
}

.disease-item__title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  color: #333;
}

.disease-item__meta,
.attachment-item__name,
.attachment-item__link,
.attachment-item__type {
  margin-top: 6px;
  color: #666;
  font-size: 14px;
  word-break: break-all;
}

.attachment-item__link {
  color: #409eff;
  text-decoration: none;
}

.alert-box {
  margin-top: 20px;
}

.push-section {
  margin-bottom: 20px;
}

.push-section h4 {
  margin-bottom: 10px;
  color: #333;
}

.hint {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
  text-align: center;
}
</style>
