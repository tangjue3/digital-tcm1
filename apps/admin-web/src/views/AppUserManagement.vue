<template>
  <div class="app-user-page">
    <section class="toolbar">
      <div>
        <h2>用户管理</h2>
        <p>查看 App 端采集的病人基础资料、声音、舌苔图、面部图和最近脉搏。</p>
      </div>
      <el-button type="primary" :loading="loading" @click="fetchList">刷新</el-button>
    </section>

    <section class="query-panel">
      <el-form :model="query" inline label-width="76px">
        <el-form-item label="用户">
          <el-input v-model="query.userName" clearable placeholder="用户名/昵称" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="query.phonenumber" clearable placeholder="手机号/账号" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            range-separator="至"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-panel">
      <el-table v-loading="loading" :data="rows" border empty-text="暂无用户数据">
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="nickName" label="用户名/昵称" min-width="130">
          <template #default="{ row }">{{ row.nickName || row.userName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phonenumber" label="手机号/账号" min-width="150">
          <template #default="{ row }">{{ row.phonenumber || row.userName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="sex" label="性别" width="90">
          <template #default="{ row }">{{ formatSex(row.sex) }}</template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="90">
          <template #default="{ row }">{{ row.age ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="collectTime" label="采集时间" min-width="170">
          <template #default="{ row }">{{ row.collectTime || row.createTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="声音文件" width="110" align="center">
          <template #default="{ row }">
            <el-button v-if="row.voiceFile" link type="primary" @click="previewAudio(row.voiceFile)">播放声音</el-button>
            <span v-else class="empty">-</span>
          </template>
        </el-table-column>
        <el-table-column label="舌苔图片" width="120" align="center">
          <template #default="{ row }">
            <el-button v-if="row.tongueImage" link type="primary" @click="previewImage(row.tongueImage, '舌苔图片预览')">
              查看舌苔图
            </el-button>
            <span v-else class="empty">-</span>
          </template>
        </el-table-column>
        <el-table-column label="面部图片" width="120" align="center">
          <template #default="{ row }">
            <el-button v-if="row.faceImage" link type="primary" @click="previewImage(row.faceImage, '面部图片预览')">
              查看面部图
            </el-button>
            <span v-else class="empty">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </section>

    <el-dialog v-model="detailVisible" title="用户详情" width="760px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ detail.userId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detail.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detail.nickName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phonenumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ formatSex(detail.sex) }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detail.age ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="采集时间">{{ detail.collectTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="media-section">
        <div class="pulse-card">
          <div class="pulse-main">
            <div>
              <div class="media-title">最近脉搏</div>
              <div v-if="latestPulse.pulseRate" class="pulse-value">
                {{ latestPulse.pulseRate }} <span>次/分钟</span>
              </div>
              <div v-else class="empty">暂无脉搏数据</div>
            </div>
            <el-tag v-if="latestPulse.source" type="success">{{ latestPulse.source }}</el-tag>
          </div>
          <div v-if="latestPulse.pulseRate" class="pulse-meta">
            <span>采集时间：{{ latestPulse.sampledAt || '-' }}</span>
            <span>设备：{{ latestPulse.deviceId || 'manual' }}</span>
            <span>信号质量：{{ latestPulse.signalQuality ?? '-' }}</span>
          </div>
        </div>

        <div class="media-block">
          <div class="media-title">声音文件</div>
          <audio v-if="detail.voiceFile" controls :src="mediaUrl(detail.voiceFile)" />
          <span v-else class="empty">未上传</span>
        </div>
        <div class="media-grid">
          <div class="media-block">
            <div class="media-title">舌苔图片</div>
            <el-image
              v-if="detail.tongueImage"
              :src="mediaUrl(detail.tongueImage)"
              fit="cover"
              :preview-src-list="[mediaUrl(detail.tongueImage)]"
            />
            <span v-else class="empty">未上传</span>
          </div>
          <div class="media-block">
            <div class="media-title">面部图片</div>
            <el-image
              v-if="detail.faceImage"
              :src="mediaUrl(detail.faceImage)"
              fit="cover"
              :preview-src-list="[mediaUrl(detail.faceImage)]"
            />
            <span v-else class="empty">未上传</span>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="mediaVisible" :title="mediaTitle" width="720px" destroy-on-close>
      <div v-if="mediaType === 'audio'" class="preview-audio">
        <audio controls autoplay :src="mediaSource" />
      </div>
      <div v-else-if="mediaType === 'image'" class="preview-image-wrap">
        <el-image :src="mediaSource" fit="contain" :preview-src-list="[mediaSource]" class="preview-image" />
      </div>
      <template #footer>
        <el-button @click="mediaVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAppUser, getAppUser, listAppUsers } from '@/api/appUser'
import { getLatestPulse } from '@/api/pulse'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const dateRange = ref([])
const detailVisible = ref(false)
const detail = ref({})
const latestPulse = ref({})
const mediaVisible = ref(false)
const mediaTitle = ref('')
const mediaType = ref('')
const mediaSource = ref('')

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: ''
})

function buildParams() {
  const params = { ...query }
  const beginTime = dateRange.value?.[0]
  const endTime = dateRange.value?.[1]
  if (beginTime) {
    params.beginTime = beginTime
  }
  if (endTime) {
    params.endTime = endTime
  }
  return params
}

function mediaUrl(path) {
  if (!path) {
    return ''
  }
  const value = String(path).trim()
  if (/^(https?:|blob:|data:)/.test(value)) {
    return value
  }
  if (value.startsWith('/api/')) {
    return value
  }
  const normalized = value.startsWith('/') ? value : `/${value}`
  if (normalized.startsWith('/profile/')) {
    return `/api${normalized}`
  }
  return `/api${normalized}`
}

function previewAudio(path) {
  const url = mediaUrl(path)
  if (!url) {
    return
  }
  mediaTitle.value = '声音预览'
  mediaType.value = 'audio'
  mediaSource.value = url
  mediaVisible.value = true
}

function previewImage(path, title = '图片预览') {
  const url = mediaUrl(path)
  if (!url) {
    return
  }
  mediaTitle.value = title
  mediaType.value = 'image'
  mediaSource.value = url
  mediaVisible.value = true
}

function formatSex(value) {
  const sexMap = {
    0: '男',
    1: '女',
    2: '未知',
    男: '男',
    女: '女',
    未知: '未知'
  }
  return sexMap[value] || value || '-'
}

function requestErrorMessage(error, fallback) {
  const status = error?.response?.status
  const message = error?.response?.data?.msg || error?.message
  if (!status || message === 'Network Error') {
    return '后端服务未连接，请确认 8080 已启动'
  }
  return message ? `${fallback}：${message}` : fallback
}

async function fetchList() {
  loading.value = true
  try {
    const res = await listAppUsers(buildParams())
    rows.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error(error)
    rows.value = []
    total.value = 0
    ElMessage.error(requestErrorMessage(error, '用户信息加载失败'))
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  query.pageNum = 1
  fetchList()
}

function resetQuery() {
  query.pageNum = 1
  query.userName = ''
  query.phonenumber = ''
  dateRange.value = []
  fetchList()
}

async function showDetail(row) {
  try {
    const [profileRes, pulseRes] = await Promise.all([
      getAppUser(row.userId),
      getLatestPulse(row.userId)
    ])
    detail.value = profileRes.data || row
    latestPulse.value = pulseRes.data || {}
    detailVisible.value = true
  } catch (error) {
    console.error(error)
    ElMessage.error(requestErrorMessage(error, '详情加载失败'))
  }
}

async function handleDelete(row) {
  const displayName = row.nickName || row.userName || row.phonenumber || row.userId
  try {
    await ElMessageBox.confirm(
      `确定删除“${displayName}”的病人采集信息吗？登录账号会保留。`,
      '删除确认',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await deleteAppUser(row.userId)
    if (res?.code !== undefined && res.code !== 200) {
      throw new Error(res.msg || '删除失败')
    }
    ElMessage.success('已删除病人信息')
    if (rows.value.length === 1 && query.pageNum > 1) {
      query.pageNum -= 1
    }
    fetchList()
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }
    console.error(error)
    ElMessage.error(requestErrorMessage(error, '删除失败'))
  }
}

onMounted(fetchList)
</script>

<style scoped>
.app-user-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
  color: #17324d;
}

.toolbar,
.query-panel,
.table-panel {
  background: #ffffff;
  border: 1px solid #d9e6f7;
  border-radius: 8px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 22px;
}

.toolbar h2 {
  margin: 0;
}

.toolbar p {
  margin: 8px 0 0;
  color: #5f7085;
}

.query-panel {
  padding: 18px 18px 0;
}

.table-panel {
  padding: 18px;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.empty {
  color: #98a6b8;
}

.action-buttons {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.media-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 18px;
}

.pulse-card {
  padding: 16px;
  border: 1px solid #cfe6d4;
  border-radius: 10px;
  background: linear-gradient(135deg, #f3fff5 0%, #f8fbff 100%);
}

.pulse-main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.pulse-value {
  margin-top: 4px;
  color: #1f8f4d;
  font-size: 34px;
  font-weight: 800;
  line-height: 1.1;
}

.pulse-value span {
  color: #5f7085;
  font-size: 14px;
  font-weight: 500;
}

.pulse-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
  color: #5f7085;
  font-size: 13px;
}

.media-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.media-block {
  min-height: 96px;
  padding: 14px;
  border: 1px solid #d9e6f7;
  border-radius: 8px;
  background: #f8fbff;
}

.media-title {
  margin-bottom: 10px;
  font-weight: 700;
}

.media-block audio,
.preview-audio audio {
  width: 100%;
}

.media-block .el-image {
  width: 100%;
  height: 220px;
  border-radius: 6px;
  background: #eef4fb;
}

.preview-image-wrap {
  display: flex;
  justify-content: center;
}

.preview-image {
  width: 100%;
  max-height: 70vh;
  background: #f8fbff;
}

@media (max-width: 900px) {
  .toolbar {
    align-items: flex-start;
    flex-direction: column;
  }

  .media-grid {
    grid-template-columns: 1fr;
  }
}
</style>
