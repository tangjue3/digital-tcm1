<template>
  <div class="communication-page">
    <div class="top-bar">
      <div>
        <h2>医患沟通</h2>
        <p>真实对话用于手机端联调，演示案例用于比赛现场完整展示。</p>
      </div>
      <div class="top-actions">
        <el-segmented v-model="mode" :options="modeOptions" />
        <el-button v-if="isRealMode" type="primary" plain :loading="patientsLoading" @click="refreshAll">
          刷新
        </el-button>
      </div>
    </div>

    <div class="communication-layout">
      <aside class="patient-list">
        <div class="list-header">
          <div>
            <h3>{{ isRealMode ? '真实患者' : '演示病例' }}</h3>
            <span>{{ isRealMode ? '来自后端会话与用户资料' : '离线可展示的模拟交流' }}</span>
          </div>
          <el-input
            v-model="keyword"
            clearable
            size="small"
            :placeholder="isRealMode ? '搜索姓名或手机号' : '搜索病例或证型'"
            class="search-input"
          />
        </div>

        <div class="patient-items">
          <button
            v-for="patient in filteredPatients"
            :key="patient.id"
            type="button"
            class="patient-item"
            :class="{ active: selectedPatient?.id === patient.id }"
            @click="selectPatient(patient)"
          >
            <div class="patient-avatar">
              <span>{{ avatarText(patient.name) }}</span>
            </div>
            <div class="patient-info">
              <div class="patient-name-row">
                <span class="patient-name">{{ patient.name }}</span>
                <el-badge v-if="patient.unread > 0" :value="patient.unread" type="danger" />
              </div>
              <div class="patient-desc">{{ patient.desc }}</div>
              <div class="patient-meta">
                <span>{{ patient.phone || patient.status || '未绑定手机号' }}</span>
                <span>{{ patient.lastMessageTime || '暂无消息' }}</span>
              </div>
              <div class="conversation-preview">{{ patient.lastMessage || '点击开始沟通' }}</div>
            </div>
          </button>

          <el-empty v-if="!filteredPatients.length" :description="isRealMode ? '暂无真实会话' : '暂无演示病例'" />
        </div>
      </aside>

      <section v-if="selectedPatient" class="chat-area">
        <header class="chat-header">
          <div class="chat-title">
            <div class="patient-avatar small">
              <span>{{ avatarText(selectedPatient.name) }}</span>
            </div>
            <div>
              <div class="chat-name">{{ selectedPatient.name }}</div>
              <div class="chat-status">
                {{ selectedPatient.status || selectedPatient.phone || '等待沟通' }}
              </div>
            </div>
          </div>
          <div class="chat-actions">
            <el-tag :type="isRealMode ? 'success' : 'warning'" effect="plain">
              {{ isRealMode ? '真实对话' : '演示案例' }}
            </el-tag>
            <el-button size="small" :icon="VideoCamera">视频</el-button>
            <el-button size="small" :icon="Phone">语音</el-button>
            <el-button size="small" :icon="More">更多</el-button>
          </div>
        </header>

        <div ref="messageScroller" class="chat-messages">
          <div
            v-for="message in messages"
            :key="message.id"
            class="message-item"
            :class="{ doctor: message.sender === 'doctor', patient: message.sender === 'patient' }"
          >
            <div class="message-avatar">
              <span>{{ message.sender === 'doctor' ? '医' : avatarText(selectedPatient.name) }}</span>
            </div>
            <div class="message-content">
              <div class="message-sender">{{ message.senderName || senderName(message.sender) }}</div>
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>

          <el-empty v-if="!messages.length" description="暂无聊天记录" />
        </div>

        <footer class="chat-input-area">
          <div class="quick-messages">
            <el-tag
              v-for="quickMsg in quickMessages"
              :key="quickMsg"
              class="quick-msg-tag"
              @click="selectQuickMessage(quickMsg)"
            >
              {{ quickMsg }}
            </el-tag>
          </div>
          <el-input
            v-model="draft"
            type="textarea"
            :rows="3"
            resize="none"
            maxlength="300"
            show-word-limit
            placeholder="输入医生建议、复诊提醒或问诊问题"
            class="message-input"
            @keyup.ctrl.enter="sendMessage"
          />
          <div class="input-actions">
            <div class="action-buttons">
              <el-button size="small" :icon="Plus">附件</el-button>
              <el-button size="small" :icon="Picture">图片</el-button>
              <el-button size="small" :icon="Position">位置</el-button>
            </div>
            <el-button type="primary" :loading="sending" @click="sendMessage">
              发送消息
            </el-button>
          </div>
        </footer>
      </section>

      <aside v-if="selectedPatient" class="patient-detail-container">
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span>患者详情</span>
              <el-tag size="small" :type="isRealMode ? 'success' : 'warning'">
                {{ isRealMode ? '后端资料' : '演示资料' }}
              </el-tag>
            </div>
          </template>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="姓名">{{ selectedPatient.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ selectedPatient.gender || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ selectedPatient.age ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ selectedPatient.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="病情">{{ selectedPatient.desc || '-' }}</el-descriptions-item>
          </el-descriptions>

          <div class="diagnosis-info">
            <h4>诊断摘要</h4>
            <div class="diagnosis-card">
              <div class="diagnosis-item">
                <span>证型</span>
                <strong>{{ currentDiagnosis.type }}</strong>
              </div>
              <div class="diagnosis-item">
                <span>症状</span>
                <p>{{ currentDiagnosis.symptoms }}</p>
              </div>
              <div class="diagnosis-item">
                <span>建议</span>
                <p>{{ currentDiagnosis.advice }}</p>
              </div>
            </div>
          </div>

          <div v-if="isRealMode" class="real-profile">
            <h4>采集资料</h4>
            <div class="pulse-card">
              <span>最近脉搏</span>
              <strong v-if="latestPulse.pulseRate">{{ latestPulse.pulseRate }} 次/分钟</strong>
              <strong v-else>暂无</strong>
              <small v-if="latestPulse.sampledAt">{{ latestPulse.sampledAt }}</small>
            </div>
            <div class="media-grid">
              <a v-if="profile.voiceFile" :href="mediaUrl(profile.voiceFile)" target="_blank">声音文件</a>
              <a v-if="profile.tongueImage" :href="mediaUrl(profile.tongueImage)" target="_blank">舌苔图片</a>
              <a v-if="profile.faceImage" :href="mediaUrl(profile.faceImage)" target="_blank">面部图片</a>
              <span v-if="!profile.voiceFile && !profile.tongueImage && !profile.faceImage" class="empty-text">暂无上传资料</span>
            </div>
          </div>

          <div class="visit-history">
            <h4>就诊记录</h4>
            <div v-for="item in currentHistory" :key="item.date" class="history-item">
              <div>{{ item.date }}</div>
              <p>{{ item.content }}</p>
            </div>
          </div>
        </el-card>
      </aside>

      <div v-else class="empty-state">
        <el-empty :description="isRealMode ? '请选择一个真实患者' : '请选择一个演示病例'" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { More, Phone, Picture, Plus, Position, VideoCamera } from '@element-plus/icons-vue'
import { getInfo } from '@/api/auth'
import { getAppUser, listAppUsers } from '@/api/appUser'
import { getLatestPulse } from '@/api/pulse'
import {
  getMedicalChatConversation,
  listMedicalChatConversations,
  sendMedicalChatMessage
} from '@/api/medicalChat'

const route = useRoute()
const router = useRouter()

const PATIENT_CACHE_KEY = 'medical-chat-patient-index'
const SELECTED_PATIENT_KEY = 'medical-chat-selected-patient'
const CONVERSATION_CACHE_PREFIX = 'medical-chat-conversation:'
const READ_STATE_KEY = 'medical-chat-read-state'

const modeOptions = [
  { label: '真实对话', value: 'real' },
  { label: '演示案例', value: 'demo' }
]

const demoCases = [
  {
    id: 'demo-1',
    name: '张先生',
    gender: '男',
    age: 45,
    phone: '演示病例',
    desc: '慢性胃炎 - 脾胃虚寒证',
    status: '在线',
    lastMessage: '我会按您的建议调理饮食。',
    lastMessageTime: '09:50',
    unread: 2,
    diagnosis: {
      type: '脾胃虚寒证',
      symptoms: '胃部隐痛，饭后加重，反酸，食欲不振，乏力。',
      advice: '饮食宜温热易消化，避免生冷食物，可配合腹部热敷。'
    },
    history: [
      { date: '2025-02-12', content: '初诊，胃脘隐痛，辨为脾胃虚寒。' },
      { date: '2025-02-19', content: '复诊，胃痛减轻，继续调理饮食。' }
    ],
    messages: [
      { id: 'd1-1', sender: 'patient', content: '医生您好，我最近胃部不适，总是隐隐作痛，尤其是饭后更明显。', time: '09:30' },
      { id: 'd1-2', sender: 'doctor', content: '您好，这种情况持续多久了？有没有反酸、嗳气或者食欲不振？', time: '09:35' },
      { id: 'd1-3', sender: 'patient', content: '大概一个月了，确实有反酸和食欲不振，而且经常乏力。', time: '09:40' },
      { id: 'd1-4', sender: 'doctor', content: '可能偏脾胃虚寒。建议饮食温热清淡，先避免生冷，按时复诊观察。', time: '09:45' },
      { id: 'd1-5', sender: 'patient', content: '好的，谢谢医生。我会按您的建议调理饮食。', time: '09:50' }
    ]
  },
  {
    id: 'demo-2',
    name: '李女士',
    gender: '女',
    age: 32,
    phone: '演示病例',
    desc: '头痛眩晕 - 肝阳上亢证',
    status: '离线',
    lastMessage: '我会注意调整情绪和睡眠。',
    lastMessageTime: '10:35',
    unread: 0,
    diagnosis: {
      type: '肝阳上亢证',
      symptoms: '太阳穴针刺样头痛，口苦，失眠，情绪急躁。',
      advice: '保持心情舒畅，避免情绪刺激，保证睡眠。'
    },
    history: [
      { date: '2025-02-11', content: '初诊，头痛眩晕，情绪波动后加重。' },
      { date: '2025-02-18', content: '复诊，睡眠改善，头痛频率下降。' }
    ],
    messages: [
      { id: 'd2-1', sender: 'patient', content: '医生您好，我最近经常头痛，太阳穴附近像针扎一样。', time: '10:15' },
      { id: 'd2-2', sender: 'doctor', content: '头痛是持续性的还是阵发性的？是否伴有口苦、失眠？', time: '10:20' },
      { id: 'd2-3', sender: 'patient', content: '阵发性的，压力大时会加重，也有口苦和失眠。', time: '10:25' },
      { id: 'd2-4', sender: 'doctor', content: '考虑肝阳上亢倾向。近期先调节情绪，避免熬夜，饮食清淡。', time: '10:30' },
      { id: 'd2-5', sender: 'patient', content: '好的，我会注意调整情绪和睡眠。', time: '10:35' }
    ]
  },
  {
    id: 'demo-3',
    name: '周女士',
    gender: '女',
    age: 40,
    phone: '演示病例',
    desc: '失眠心悸 - 心肾不交证',
    status: '在线',
    lastMessage: '我会注意作息，避免熬夜。',
    lastMessageTime: '11:40',
    unread: 1,
    diagnosis: {
      type: '心肾不交证',
      symptoms: '失眠多梦，易醒，心悸健忘，手足心热。',
      advice: '作息规律，避免熬夜，睡前减少刺激性信息输入。'
    },
    history: [
      { date: '2025-02-10', content: '初诊，失眠一月，心悸健忘。' },
      { date: '2025-02-17', content: '复诊，入睡时间缩短，仍有多梦。' }
    ],
    messages: [
      { id: 'd3-1', sender: 'patient', content: '医生您好，我最近失眠很严重，睡着了也很容易醒。', time: '11:20' },
      { id: 'd3-2', sender: 'doctor', content: '这种情况持续多久了？有没有心悸、健忘或者手足心热？', time: '11:25' },
      { id: 'd3-3', sender: 'patient', content: '大概一个月了，确实有心悸和健忘，手足心也发热。', time: '11:30' },
      { id: 'd3-4', sender: 'doctor', content: '考虑心肾不交倾向。建议先调整作息，避免熬夜，必要时复诊评估。', time: '11:35' },
      { id: 'd3-5', sender: 'patient', content: '好的，我会注意作息，避免熬夜。', time: '11:40' }
    ]
  }
]

const quickMessages = [
  '请按时服药，如有不适及时联系我。',
  '建议您注意休息，保持良好的作息习惯。',
  '饮食宜清淡，避免辛辣刺激性食物。',
  '请定期复诊，以便调整治疗方案。',
  '您的情况需要进一步检查，建议到医院就诊。',
  '保持心情舒畅，避免过度劳累。'
]

const keyword = ref('')
const draft = ref('')
const mode = ref('real')
const doctorName = ref('当前医生')
const patientsLoading = ref(false)
const sending = ref(false)
const selectedPatientId = ref(null)
const realPatients = ref([])
const demoPatients = ref(demoCases.map(item => ({ ...item })))
const messages = ref([])
const profile = ref({})
const latestPulse = ref({})
const messageScroller = ref(null)

let pollTimer = null

const isRealMode = computed(() => mode.value === 'real')
const currentPatients = computed(() => isRealMode.value ? realPatients.value : demoPatients.value)

const filteredPatients = computed(() => {
  const normalized = keyword.value.trim().toLowerCase()
  if (!normalized) {
    return currentPatients.value
  }
  return currentPatients.value.filter(patient => {
    const haystack = `${patient.name} ${patient.phone || ''} ${patient.desc || ''}`.toLowerCase()
    return haystack.includes(normalized)
  })
})

const selectedPatient = computed(() => {
  return currentPatients.value.find(patient => patient.id === selectedPatientId.value) || null
})

const currentDiagnosis = computed(() => {
  return selectedPatient.value?.diagnosis || {
    type: isRealMode.value ? '待结合采集数据分析' : '未诊断',
    symptoms: isRealMode.value ? '可结合用户管理中的舌象、面部图、声音与脉象记录进一步判断。' : '暂无',
    advice: isRealMode.value ? '建议在沟通中补充主诉、病程、用药史与复诊反馈。' : '建议到医院就诊。'
  }
})

const currentHistory = computed(() => {
  if (selectedPatient.value?.history?.length) {
    return selectedPatient.value.history
  }
  return [
    { date: '最近一次', content: selectedPatient.value?.lastMessage || '暂无就诊记录，可在后续接入诊疗记录接口。' }
  ]
})

function avatarText(name = '') {
  return String(name || '患').slice(0, 1)
}

function senderName(sender) {
  return sender === 'doctor' ? doctorName.value : selectedPatient.value?.name || '患者'
}

function conversationCacheKey(patientUserId) {
  return `${CONVERSATION_CACHE_PREFIX}${patientUserId}`
}

function readCache(key, fallbackValue) {
  try {
    const raw = localStorage.getItem(key)
    return raw ? JSON.parse(raw) : fallbackValue
  } catch (error) {
    console.warn('[medical-chat] failed to read cache', key, error)
    return fallbackValue
  }
}

function writeCache(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value))
  } catch (error) {
    console.warn('[medical-chat] failed to write cache', key, error)
  }
}

function getReadState() {
  return readCache(READ_STATE_KEY, {})
}

function setReadCount(patientId, count) {
  const state = getReadState()
  state[String(patientId)] = Number(count) || 0
  writeCache(READ_STATE_KEY, state)
}

function getReadCount(patientId) {
  return Number(getReadState()[String(patientId)] || 0)
}

function markPatientRead(patientId, count) {
  setReadCount(patientId, count)
  const target = realPatients.value.find(item => item.id === patientId)
  if (target) {
    target.unread = 0
    target.readCount = count
  }
}

function mediaUrl(path) {
  if (!path) {
    return ''
  }
  const value = String(path).trim()
  if (/^https?:\/\//.test(value) || value.startsWith('/api/')) {
    return value
  }
  const normalized = value.startsWith('/') ? value : `/${value}`
  return `/api${normalized}`
}

function normalizeRealMessage(message = {}) {
  return {
    id: message.messageId || `${message.senderRole}-${message.sendTime}-${message.content}`,
    sender: message.senderRole === 'doctor' ? 'doctor' : 'patient',
    senderName: message.senderName || '',
    content: message.content || '',
    time: message.sendTime || ''
  }
}

function normalizePatient(patient = {}, summaryMap = new Map()) {
  const summary = summaryMap.get(patient.userId) || {}
  const messageCount = Number(summary.messageCount || 0)
  const readCount = getReadCount(patient.userId)
  return {
    id: patient.userId,
    userId: patient.userId,
    name: patient.nickName || patient.userName || patient.phonenumber || `患者${patient.userId}`,
    gender: formatSex(patient.sex),
    age: patient.age,
    phone: patient.phonenumber || '',
    desc: patient.mainComplaint || patient.remark || '真实患者资料',
    status: summary.lastMessageTime ? '有会话记录' : '待沟通',
    lastMessage: summary.lastMessage || '',
    lastMessageTime: summary.lastMessageTime || '',
    messageCount,
    readCount,
    unread: Math.max(messageCount - readCount, 0),
    doctorName: summary.doctorName || ''
  }
}

function normalizeSummaryOnlyPatient(summary = {}) {
  const messageCount = Number(summary.messageCount || 0)
  const readCount = getReadCount(summary.patientUserId)
  return {
    id: summary.patientUserId,
    userId: summary.patientUserId,
    name: summary.patientName || `患者${summary.patientUserId}`,
    gender: '',
    age: null,
    phone: summary.patientPhone || '',
    desc: '真实患者会话',
    status: '有会话记录',
    lastMessage: summary.lastMessage || '',
    lastMessageTime: summary.lastMessageTime || '',
    messageCount,
    readCount,
    unread: Math.max(messageCount - readCount, 0),
    doctorName: summary.doctorName || ''
  }
}

function formatSex(value) {
  if (value === '0' || value === 0 || value === '男') {
    return '男'
  }
  if (value === '1' || value === 1 || value === '女') {
    return '女'
  }
  return value || ''
}

function mergePatients(userRows = [], conversationSummaries = []) {
  const summaryMap = new Map()
  conversationSummaries.forEach(summary => {
    summaryMap.set(summary.patientUserId, summary)
  })

  const merged = userRows.map(user => normalizePatient(user, summaryMap))
  const existingIds = new Set(merged.map(item => item.id))

  conversationSummaries.forEach(summary => {
    if (!existingIds.has(summary.patientUserId)) {
      merged.push(normalizeSummaryOnlyPatient(summary))
    }
  })

  merged.sort((left, right) => {
    const leftTime = left.lastMessageTime || ''
    const rightTime = right.lastMessageTime || ''
    if (leftTime === rightTime) {
      return String(left.name).localeCompare(String(right.name), 'zh-CN')
    }
    return rightTime.localeCompare(leftTime)
  })

  return merged
}

function scrollToBottom() {
  nextTick(() => {
    const node = messageScroller.value
    if (node) {
      node.scrollTop = node.scrollHeight
    }
  })
}

function restoreConversationFromCache(patientUserId) {
  const cached = readCache(conversationCacheKey(patientUserId), null)
  if (cached?.messages) {
    messages.value = cached.messages.map(normalizeRealMessage)
    scrollToBottom()
  }
}

function setConversation(conversation, patientId) {
  const rawMessages = conversation?.messages || []
  messages.value = rawMessages.map(normalizeRealMessage)
  writeCache(conversationCacheKey(patientId), conversation || { messages: [] })
  markPatientRead(patientId, rawMessages.length)
  scrollToBottom()
}

async function loadDoctorInfo() {
  try {
    const response = await getInfo()
    const user = response?.user || {}
    doctorName.value = user.nickName || user.userName || '当前医生'
  } catch (error) {
    console.error('[medical-chat] failed to load doctor info', error)
  }
}

async function loadPatients(silent = false) {
  if (!silent) {
    patientsLoading.value = true
  }
  try {
    const [usersRes, summariesRes] = await Promise.all([
      listAppUsers({ pageNum: 1, pageSize: 200 }),
      listMedicalChatConversations()
    ])
    const merged = mergePatients(usersRes?.rows || [], summariesRes?.data || [])
    realPatients.value = merged
    writeCache(PATIENT_CACHE_KEY, merged)

    const routePatientId = Number(route.query.patientUserId || 0) || null
    const cachedPatientId = Number(localStorage.getItem(SELECTED_PATIENT_KEY) || 0) || null
    const nextPatientId = routePatientId || selectedPatientId.value || cachedPatientId || merged[0]?.id || null
    if (isRealMode.value && nextPatientId) {
      const targetPatient = merged.find(item => item.id === nextPatientId) || merged[0]
      if (targetPatient && selectedPatientId.value !== targetPatient.id) {
        await selectPatient(targetPatient, { replaceRoute: !routePatientId })
      }
    }
  } catch (error) {
    console.error('[medical-chat] failed to load patients', error)
    if (!realPatients.value.length) {
      realPatients.value = readCache(PATIENT_CACHE_KEY, [])
    }
    if (!silent) {
      ElMessage.error('患者列表加载失败，请确认后端服务已启动')
    }
  } finally {
    if (!silent) {
      patientsLoading.value = false
    }
  }
}

async function fetchConversation(patientUserId, silent = false) {
  if (!patientUserId) {
    messages.value = []
    return
  }
  try {
    const response = await getMedicalChatConversation(patientUserId)
    setConversation(response?.data || { messages: [] }, patientUserId)
  } catch (error) {
    console.error('[medical-chat] failed to load conversation', error)
    restoreConversationFromCache(patientUserId)
    if (!silent) {
      ElMessage.error('聊天记录加载失败')
    }
  }
}

async function loadRealProfile(patient) {
  profile.value = {}
  latestPulse.value = {}
  if (!patient?.userId) {
    return
  }
  try {
    const [profileRes, pulseRes] = await Promise.all([
      getAppUser(patient.userId),
      getLatestPulse(patient.userId)
    ])
    profile.value = profileRes?.data || {}
    latestPulse.value = pulseRes?.data || {}
    if (profile.value.userId) {
      patient.gender = formatSex(profile.value.sex) || patient.gender
      patient.age = profile.value.age ?? patient.age
      patient.phone = profile.value.phonenumber || patient.phone
      patient.desc = profile.value.mainComplaint || profile.value.remark || patient.desc
    }
  } catch (error) {
    console.warn('[medical-chat] failed to load patient profile', error)
  }
}

function loadDemoConversation(patient) {
  messages.value = [...(patient.messages || [])]
  patient.unread = 0
  scrollToBottom()
}

async function selectPatient(patient, options = {}) {
  if (!patient?.id) {
    return
  }
  selectedPatientId.value = patient.id
  if (isRealMode.value) {
    localStorage.setItem(SELECTED_PATIENT_KEY, String(patient.id))
    restoreConversationFromCache(patient.id)
    if (options.replaceRoute !== false) {
      router.replace({
        path: '/home/medicalChat',
        query: { patientUserId: String(patient.id) }
      })
    }
    await Promise.all([
      fetchConversation(patient.id, options.silent),
      loadRealProfile(patient)
    ])
  } else {
    profile.value = {}
    latestPulse.value = {}
    loadDemoConversation(patient)
  }
}

function selectQuickMessage(message) {
  draft.value = message
}

async function sendMessage() {
  const content = draft.value.trim()
  if (!content || !selectedPatient.value) {
    return
  }
  sending.value = true
  try {
    if (isRealMode.value) {
      await sendMedicalChatMessage(selectedPatient.value.id, content)
      draft.value = ''
      await fetchConversation(selectedPatient.value.id, true)
      await loadPatients(true)
      markPatientRead(selectedPatient.value.id, messages.value.length)
    } else {
      const now = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      messages.value.push({
        id: `demo-doctor-${Date.now()}`,
        sender: 'doctor',
        content,
        time: now
      })
      draft.value = ''
      setTimeout(() => {
        messages.value.push({
          id: `demo-patient-${Date.now()}`,
          sender: 'patient',
          content: '好的，我明白了，谢谢医生的建议。',
          time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
        })
        scrollToBottom()
      }, 600)
      scrollToBottom()
    }
  } catch (error) {
    console.error('[medical-chat] failed to send message', error)
    ElMessage.error(error?.response?.data?.msg || error?.message || '消息发送失败')
  } finally {
    sending.value = false
  }
}

async function refreshAll() {
  await loadPatients()
  if (isRealMode.value && selectedPatient.value) {
    await fetchConversation(selectedPatient.value.id, true)
  }
}

function startPolling() {
  stopPolling()
  pollTimer = window.setInterval(() => {
    if (!isRealMode.value) {
      return
    }
    loadPatients(true).then(() => {
      if (selectedPatient.value?.id) {
        fetchConversation(selectedPatient.value.id, true)
      }
    })
  }, 4000)
}

function stopPolling() {
  if (pollTimer) {
    window.clearInterval(pollTimer)
    pollTimer = null
  }
}

watch(mode, async () => {
  keyword.value = ''
  draft.value = ''
  messages.value = []
  selectedPatientId.value = null
  if (isRealMode.value) {
    await loadPatients()
  } else if (demoPatients.value.length) {
    await selectPatient(demoPatients.value[0])
  }
})

watch(() => route.query.patientUserId, (value) => {
  const routePatientId = Number(value || 0) || null
  if (!isRealMode.value || !routePatientId) {
    return
  }
  const matched = realPatients.value.find(item => item.id === routePatientId)
  if (matched) {
    selectPatient(matched, { replaceRoute: false, silent: true })
  }
})

onMounted(async () => {
  realPatients.value = readCache(PATIENT_CACHE_KEY, [])
  await loadDoctorInfo()
  await loadPatients()
  startPolling()
})

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<style scoped>
.communication-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: calc(100vh - 104px);
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  border: 1px solid #d9e6f7;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 14px 36px rgba(25, 84, 182, 0.08);
}

.top-bar h2,
.list-header h3,
.chat-name {
  margin: 0;
  color: #17324d;
}

.top-bar p,
.list-header span,
.patient-desc,
.patient-meta,
.conversation-preview,
.chat-status,
.message-sender,
.message-time,
.history-item p,
.empty-text,
.pulse-card small {
  color: #6b7d90;
  font-size: 12px;
}

.top-bar p {
  margin: 6px 0 0;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.communication-layout {
  display: grid;
  grid-template-columns: 300px minmax(0, 1fr) 340px;
  flex: 1;
  min-height: 680px;
  overflow: hidden;
  border: 1px solid #d9e6f7;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 18px 42px rgba(25, 84, 182, 0.08);
}

.patient-list,
.chat-area,
.patient-detail-container {
  min-height: 0;
}

.patient-list {
  display: flex;
  flex-direction: column;
  border-right: 1px solid #edf2f9;
}

.list-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 18px;
  border-bottom: 1px solid #edf2f9;
}

.patient-items {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.patient-item {
  display: flex;
  width: 100%;
  gap: 12px;
  padding: 14px;
  margin-bottom: 10px;
  border: 1px solid #d9e6f7;
  border-radius: 10px;
  background: #f8fbff;
  text-align: left;
  cursor: pointer;
  transition: 0.2s ease;
}

.patient-item:hover,
.patient-item.active {
  border-color: #3f8cff;
  background: linear-gradient(135deg, #f4f8ff 0%, #eaf2ff 100%);
  box-shadow: 0 10px 20px rgba(63, 140, 255, 0.12);
}

.patient-avatar,
.message-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  color: #fff;
  font-weight: 700;
}

.patient-avatar {
  width: 40px;
  height: 40px;
}

.patient-avatar.small,
.message-avatar {
  width: 34px;
  height: 34px;
}

.patient-info {
  min-width: 0;
  flex: 1;
}

.patient-name-row,
.patient-meta,
.chat-header,
.chat-title,
.chat-actions,
.input-actions,
.card-header {
  display: flex;
  align-items: center;
}

.patient-name-row,
.patient-meta,
.chat-header,
.input-actions,
.card-header {
  justify-content: space-between;
  gap: 10px;
}

.patient-name {
  color: #17324d;
  font-weight: 700;
}

.patient-desc,
.conversation-preview {
  margin-top: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.patient-meta {
  margin-top: 7px;
}

.chat-area {
  display: flex;
  flex-direction: column;
  border-right: 1px solid #edf2f9;
}

.chat-header {
  padding: 16px 18px;
  border-bottom: 1px solid #edf2f9;
  background: #fafcff;
}

.chat-title {
  gap: 10px;
}

.chat-actions {
  flex-wrap: wrap;
}

.chat-messages {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 20px;
  background:
    radial-gradient(circle at top left, rgba(63, 140, 255, 0.08), transparent 32%),
    linear-gradient(180deg, #f9fbff 0%, #f4f8ff 100%);
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
}

.message-item.doctor {
  flex-direction: row-reverse;
}

.message-item.doctor .message-content {
  align-items: flex-end;
}

.message-item.doctor .message-avatar {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: min(72%, 560px);
}

.message-text {
  padding: 12px 16px;
  border-radius: 14px 14px 14px 4px;
  background: #fff;
  color: #17324d;
  line-height: 1.7;
  box-shadow: 0 10px 20px rgba(23, 50, 77, 0.08);
  word-break: break-word;
}

.message-item.doctor .message-text {
  border-radius: 14px 14px 4px 14px;
  background: linear-gradient(135deg, #1f5fca 0%, #4a96ff 100%);
  color: #fff;
}

.message-sender,
.message-time {
  margin: 4px 2px;
}

.chat-input-area {
  padding: 14px 18px 16px;
  border-top: 1px solid #edf2f9;
  background: #fff;
}

.quick-messages {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.quick-msg-tag {
  cursor: pointer;
}

.message-input {
  margin-bottom: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.patient-detail-container {
  overflow-y: auto;
  padding: 16px;
  background: #fafcff;
}

.detail-card {
  border-radius: 10px;
}

.diagnosis-info,
.real-profile,
.visit-history {
  margin-top: 18px;
}

.diagnosis-info h4,
.real-profile h4,
.visit-history h4 {
  margin: 0 0 10px;
  color: #17324d;
}

.diagnosis-card,
.pulse-card,
.history-item {
  padding: 12px;
  border: 1px solid #e5edf7;
  border-radius: 10px;
  background: #fff;
}

.diagnosis-item {
  margin-bottom: 12px;
}

.diagnosis-item:last-child {
  margin-bottom: 0;
}

.diagnosis-item span {
  display: block;
  margin-bottom: 5px;
  color: #6b7d90;
  font-size: 12px;
}

.diagnosis-item strong,
.pulse-card strong {
  color: #17324d;
}

.diagnosis-item p,
.history-item p {
  margin: 0;
  line-height: 1.6;
}

.pulse-card {
  display: grid;
  gap: 6px;
}

.media-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.media-grid a,
.empty-text {
  display: inline-flex;
  padding: 7px 10px;
  border-radius: 999px;
  background: #edf5ff;
  color: #1f5fca;
  font-size: 12px;
  text-decoration: none;
}

.history-item {
  margin-bottom: 10px;
}

.history-item div {
  margin-bottom: 5px;
  color: #1f5fca;
  font-weight: 700;
  font-size: 13px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  grid-column: span 2;
  background: #f9fbff;
}

@media (max-width: 1240px) {
  .communication-layout {
    grid-template-columns: 280px minmax(0, 1fr);
  }

  .patient-detail-container {
    grid-column: 1 / -1;
    border-top: 1px solid #edf2f9;
  }
}

@media (max-width: 900px) {
  .top-bar,
  .top-actions,
  .input-actions {
    align-items: stretch;
    flex-direction: column;
  }

  .communication-layout {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .patient-list {
    max-height: 320px;
    border-right: none;
    border-bottom: 1px solid #edf2f9;
  }

  .chat-area {
    min-height: 560px;
    border-right: none;
  }

  .message-content {
    max-width: 86%;
  }
}
</style>
