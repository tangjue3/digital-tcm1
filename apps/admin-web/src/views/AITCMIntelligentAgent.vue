<template>
  <div class="agent-page">
    <div class="main-container">
      <aside class="agent-center">
        <h2 class="section-title">智能体中心</h2>
        <div class="agent-list">
          <div
            v-for="agent in agents"
            :key="agent.name"
            :class="['agent-card', { 'agent-card--active': activeAgent === agent.name }]"
            @click="activeAgent = agent.name"
          >
            <div class="agent-header">
              <div :class="['agent-icon', agent.bgColor]">
                <el-icon :size="20" color="#fff"><component :is="agent.icon" /></el-icon>
              </div>
              <el-icon class="agent-arrow"><Right /></el-icon>
            </div>
            <h3 :class="['agent-name', { 'agent-name--active': activeAgent === agent.name }]">{{ agent.name }}</h3>
            <p class="agent-desc">{{ agent.desc }}</p>
            <div class="agent-tags">
              <el-tag
                v-for="tag in agent.tags"
                :key="tag"
                :type="activeAgent === agent.name ? 'primary' : 'info'"
                size="small"
                effect="light"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>
        <div class="create-agent">
          <el-button type="primary" plain class="create-btn">
            <el-icon><Plus /></el-icon>
            <span>创建自定义智能体</span>
          </el-button>
        </div>
      </aside>

      <main class="chat-area">
        <div class="chat-banner" :style="{ background: `linear-gradient(135deg, ${currentAgent.color}15 0%, #fff 100%)` }">
          <div class="banner-content">
            <div class="banner-icon" :style="{ background: currentAgent.color }">
              <el-icon :size="32" color="#fff"><component :is="currentAgent.icon" /></el-icon>
            </div>
            <div class="banner-info">
              <div class="banner-title">
                <h2>{{ currentAgent.name }}</h2>
                <div class="banner-tags">
                  <el-tag v-for="tag in currentAgent.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
                </div>
              </div>
              <p class="banner-desc">{{ currentAgent.bannerDesc }}</p>
            </div>
          </div>
          <el-button @click="clearChat">
            <el-icon><Delete /></el-icon>
            <span>清空对话</span>
          </el-button>
        </div>

        <div class="chat-content">
          <div class="chat-empty" v-if="messages.length === 0">
            <div class="empty-icon" :style="{ background: `${currentAgent.color}15` }">
              <el-icon :size="36" :color="currentAgent.color"><component :is="currentAgent.icon" /></el-icon>
            </div>
            <h3>{{ currentAgent.welcomeTitle }}</h3>
            <p>{{ currentAgent.welcomeDesc }}</p>
            <div class="quick-questions">
              <el-button
                v-for="q in currentAgent.quickQuestions"
                :key="q"
                class="quick-btn"
                :style="{ borderColor: `${currentAgent.color}40`, color: currentAgent.color }"
                @click="inputMessage = q"
              >
                {{ q }}
              </el-button>
            </div>
          </div>

          <div class="chat-messages" v-else ref="chatContainer">
            <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.type]">
              <div class="message-avatar" v-if="msg.type === 'ai'" :style="{ background: `linear-gradient(135deg, ${currentAgent.color} 0%, ${currentAgent.color}cc 100%)` }">AI</div>
              <div class="message-content">{{ msg.content }}</div>
              <div class="message-avatar user" v-if="msg.type === 'user'">用户</div>
            </div>
          </div>
        </div>

        <div class="chat-input-area">
          <div class="input-container">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              :placeholder="currentAgent.placeholder"
              @keyup.enter.ctrl="sendMessage"
            />
            <div class="input-actions">
              <el-button-group>
                <el-button><el-icon><Document /></el-icon><span>上传病历</span></el-button>
                <el-button><el-icon><Microphone /></el-icon><span>语音输入</span></el-button>
                <el-button><el-icon><Picture /></el-icon><span>图片识别</span></el-button>
              </el-button-group>
              <el-button type="success" class="send-btn" @click="sendMessage">
                <el-icon><Promotion /></el-icon>
                <span>发送</span>
              </el-button>
            </div>
          </div>
        </div>
      </main>

      <aside class="right-sidebar">
        <section class="capabilities">
          <h3 class="section-title">智能体能力</h3>
          <div class="capability-list">
            <div v-for="cap in currentAgent.capabilities" :key="cap.title" class="capability-item">
              <div class="capability-icon" :style="{ background: `${currentAgent.color}15` }">
                <el-icon :size="16" :color="currentAgent.color"><component :is="cap.icon" /></el-icon>
              </div>
              <div class="capability-text">
                <p class="capability-title">{{ cap.title }}</p>
                <p class="capability-desc">{{ cap.desc }}</p>
              </div>
            </div>
          </div>
        </section>

        <section class="history">
          <div class="history-header">
            <h3 class="section-title">对话历史</h3>
            <el-button size="small" type="primary" link>更多</el-button>
          </div>
          <div class="history-list">
            <div v-for="(h, i) in history" :key="i" class="history-item">
              <el-icon class="history-icon"><Document /></el-icon>
              <span class="history-title">{{ h.title }}</span>
              <span class="history-time">{{ h.time }}</span>
            </div>
          </div>
        </section>

        <section class="statistics">
          <div class="stats-header">
            <h3 class="section-title">使用统计</h3>
            <el-select size="small" value="今日">
              <el-option label="今日" value="今日" />
              <el-option label="本周" value="本周" />
              <el-option label="本月" value="本月" />
            </el-select>
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <p class="stat-label">对话次数</p>
              <p class="stat-value">28</p>
              <p class="stat-change stat-change--up">较昨日 +12%</p>
            </div>
            <div class="stat-item">
              <p class="stat-label">解决问题数</p>
              <p class="stat-value">24</p>
              <p class="stat-change stat-change--up">较昨日 +15%</p>
            </div>
            <div class="stat-item">
              <p class="stat-label">满意度</p>
              <p class="stat-value">96%</p>
              <p class="stat-change stat-change--up">较昨日 +8%</p>
            </div>
          </div>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, computed, watch } from 'vue'
import {
  Search,
  Bell,
  Service,
  Document,
  Picture,
  Microphone,
  Promotion,
  Right,
  Plus,
  Delete,
  FirstAidKit,
  Collection,
  TrendCharts,
  Check,
  DataAnalysis,
  Edit,
  MagicStick,
  Tools,
  Position,
} from '@element-plus/icons-vue'

const activeAgent = ref('中医诊疗智能体')
const inputMessage = ref('')
const messages = ref([])
const chatContainer = ref(null)

const agents = [
  {
    name: '中医诊疗智能体',
    desc: '中医辩证施治、方剂推荐、针灸推拿',
    tags: ['中医', '辩证论治', '方剂库'],
    bgColor: 'bg-green',
    icon: Service,
    color: '#65a30d',
    bannerDesc: '基于中医理论，结合现代医学数据，为您提供辩证施治、方剂推荐、针灸推拿等专业建议',
    welcomeTitle: '您好，张医生！我是中医诊疗智能体',
    welcomeDesc: '您可以输入患者症状、体征或咨询中医相关问题，我将为您提供专业的中医诊疗建议',
    quickQuestions: [
      '请分析患者的中医辩证分型',
      '推荐适合的中药方剂',
      '建议针灸治疗方案',
      '中药配伍禁忌查询',
    ],
    capabilities: [
      { title: '辩证分析', desc: '四诊合参，辨识证型', icon: Check },
      { title: '方剂推荐', desc: '智能匹配，方剂推荐', icon: Edit },
      { title: '针灸建议', desc: '穴位推荐，手法指导', icon: DataAnalysis },
      { title: '中药查询', desc: '药性查询，配伍禁忌', icon: Search },
    ],
    placeholder: '请输入您的问题，例如：患者头痛眩晕，口苦咽干，如何辩证？',
  },
  {
    name: '西医诊疗智能体',
    desc: '西医疾病诊断、治疗方案、循证医学',
    tags: ['西医', '循证医学', '指南库'],
    bgColor: 'bg-blue',
    icon: FirstAidKit,
    color: '#3b82f6',
    bannerDesc: '基于现代医学知识和临床指南，提供疾病诊断、治疗方案、用药建议等专业医疗服务',
    welcomeTitle: '您好，张医生！我是西医诊疗智能体',
    welcomeDesc: '您可以输入患者症状、检查结果或咨询西医相关问题，我将为您提供专业的西医诊疗建议',
    quickQuestions: [
      '请分析患者的西医诊断思路',
      '推荐适合的治疗方案',
      '解读最新临床指南',
      '药物使用注意事项',
    ],
    capabilities: [
      { title: '疾病诊断', desc: '基于症状分析，辅助诊断', icon: FirstAidKit },
      { title: '治疗方案', desc: '结合指南，推荐方案', icon: MagicStick },
      { title: '用药建议', desc: '剂量指导，注意事项', icon: Search },
      { title: '循证医学', desc: '引用文献，证据支持', icon: Document },
    ],
    placeholder: '请输入您的问题，例如：患者发热咳嗽，如何诊断和治疗？',
  },
  {
    name: '针灸推拿智能体',
    desc: '经络穴位、针灸方案、推拿手法',
    tags: ['中医', '经络穴位', '治疗方案'],
    bgColor: 'bg-orange',
    icon: DataAnalysis,
    color: '#f59e0b',
    bannerDesc: '专注于经络穴位、针灸方案、推拿手法，为您提供专业的针灸推拿治疗指导',
    welcomeTitle: '您好，张医生！我是针灸推拿智能体',
    welcomeDesc: '您可以咨询经络穴位、针灸处方、推拿手法等相关问题，我将为您提供专业建议',
    quickQuestions: [
      '查询常用穴位的定位和主治',
      '推荐针灸治疗处方',
      '推拿手法指导',
      '经络循行路线',
    ],
    capabilities: [
      { title: '穴位查询', desc: '定位主治，精准取穴', icon: Position },
      { title: '针灸处方', desc: '配穴原则，处方推荐', icon: Edit },
      { title: '推拿手法', desc: '手法详解，操作指导', icon: Tools },
      { title: '经络循行', desc: '十二经脉，奇经八脉', icon: DataAnalysis },
    ],
    placeholder: '请输入您的问题，例如：治疗失眠应该取哪些穴位？',
  },
  {
    name: '中药药事智能体',
    desc: '中药药性、配伍禁忌、用药指导',
    tags: ['中医', '中药库', '配伍禁忌'],
    bgColor: 'bg-purple',
    icon: Collection,
    color: '#8b5cf6',
    bannerDesc: '提供中药药性查询、配伍禁忌检查、用药指导等专业中药药事服务',
    welcomeTitle: '您好，张医生！我是中药药事智能体',
    welcomeDesc: '您可以查询中药药性、配伍禁忌、煎服方法等，我将为您提供专业的中药药事服务',
    quickQuestions: [
      '查询中药的性味归经',
      '检查方剂配伍禁忌',
      '中药煎服方法指导',
      '常用药对介绍',
    ],
    capabilities: [
      { title: '药性查询', desc: '性味归经，功效主治', icon: Search },
      { title: '配伍禁忌', desc: '十八反十九畏，安全用药', icon: Check },
      { title: '煎服指导', desc: '煎药方法，服用时间', icon: Document },
      { title: '药对推荐', desc: '经典药对，协同增效', icon: Collection },
    ],
    placeholder: '请输入您的问题，例如：查询黄芪的性味归经和功效？',
  },
  {
    name: '影像诊断智能体',
    desc: '影像分析、报告解读、辅助诊断',
    tags: ['西医', '影像分析', '报告解读'],
    bgColor: 'bg-cyan',
    icon: Picture,
    color: '#14b8a6',
    bannerDesc: '专注于医学影像分析、报告解读和辅助诊断，为您提供专业的影像医学支持',
    welcomeTitle: '您好，张医生！我是影像诊断智能体',
    welcomeDesc: '您可以咨询影像分析、报告解读等相关问题，我将为您提供专业的影像医学建议',
    quickQuestions: [
      '解读CT/MRI影像报告',
      '分析胸部X线片',
      '常见影像征象识别',
      '影像检查建议',
    ],
    capabilities: [
      { title: '影像解读', desc: '报告分析，征象识别', icon: Picture },
      { title: '辅助诊断', desc: '结合影像，提示可能', icon: FirstAidKit },
      { title: '检查建议', desc: '选择合适的影像检查', icon: Search },
      { title: '随访对比', desc: '前后对比，评估变化', icon: TrendCharts },
    ],
    placeholder: '请输入您的问题，例如：如何解读这份胸部CT报告？',
  },
  {
    name: '检验分析智能体',
    desc: '检验结果解读、异常分析、趋势预测',
    tags: ['西医', '检验解读', '趋势分析'],
    bgColor: 'bg-indigo',
    icon: TrendCharts,
    color: '#6366f1',
    bannerDesc: '提供检验结果解读、异常指标分析、趋势评估等专业检验医学服务',
    welcomeTitle: '您好，张医生！我是检验分析智能体',
    welcomeDesc: '您可以上传或描述检验结果，我将为您提供专业的结果解读和分析建议',
    quickQuestions: [
      '解读血常规报告',
      '分析肝肾功能异常',
      '肿瘤标志物解读',
      '检验结果趋势分析',
    ],
    capabilities: [
      { title: '结果解读', desc: '正常范围，临床意义', icon: Document },
      { title: '异常分析', desc: '原因分析，建议措施', icon: Search },
      { title: '趋势评估', desc: '动态观察，变化分析', icon: TrendCharts },
      { title: '检查建议', desc: '后续检查，随访方案', icon: Check },
    ],
    placeholder: '请输入您的问题，例如：如何解读这份血常规报告？',
  },
]

const history = [
  { title: '患者头痛眩晕的辩证分析', time: '10:30' },
  { title: '湿热证的中药方剂推荐', time: '09:15' },
  { title: '针灸治疗失眠的方案', time: '昨天 16:20' },
  { title: '中药配伍禁忌查询', time: '昨天 14:15' },
]

// 计算属性：获取当前激活的智能体
const currentAgent = computed(() => {
  return agents.find(a => a.name === activeAgent.value) || agents[0]
})

// 监听智能体切换
watch(activeAgent, (newAgent, oldAgent) => {
  if (newAgent !== oldAgent) {
    // 清空聊天记录
    messages.value = []
  }
})

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  messages.value.push({
    type: 'user',
    content: inputMessage.value.trim(),
  })

  inputMessage.value = ''

  messages.value.push({
    type: 'ai',
    content: `感谢您的描述。我是${currentAgent.value.name}，正在为您分析，请稍候...`,
  })

  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }

  setTimeout(() => {
    messages.value.push({
      type: 'ai',
      content: `根据您的描述，我作为${currentAgent.value.name}为您提供以下建议：\n1. 请提供更详细的信息以便精准分析\n2. 建议结合临床实际情况综合判断\n3. 如有需要可进一步检查明确诊断\n4. 请遵医嘱进行治疗和随访`,
    })
    nextTick(() => {
      if (chatContainer.value) {
        chatContainer.value.scrollTop = chatContainer.value.scrollHeight
      }
    })
  }, 1500)
}

const clearChat = () => {
  messages.value = []
}
</script>

<style scoped>
.agent-page {
  background: #f4f7fa;
  min-height: calc(100vh - 140px);
  border-radius: 16px;
  padding: 4px;
}

.main-container {
  display: flex;
  gap: 16px;
  height: calc(100vh - 180px);
}

.agent-center {
  width: 280px;
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
  flex-shrink: 0;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px 0;
}

.agent-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-right: 4px;
}

.agent-list::-webkit-scrollbar {
  width: 4px;
}

.agent-list::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}

.agent-card {
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.agent-card:hover {
  border-color: #e2e8f0;
  background: #f8fafc;
}

.agent-card--active {
  border-color: #3b82f6;
  background: #fff;
  box-shadow: 0 4px 12px -4px rgba(59, 130, 246, 0.2);
}

.agent-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.agent-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bg-green { background: #65a30d; }
.bg-blue { background: #3b82f6; }
.bg-orange { background: #f59e0b; }
.bg-purple { background: #8b5cf6; }
.bg-cyan { background: #14b8a6; }
.bg-indigo { background: #6366f1; }

.agent-arrow {
  color: #cbd5e1;
  opacity: 0;
  transition: opacity 0.2s;
}

.agent-card:hover .agent-arrow {
  opacity: 1;
}

.agent-card--active .agent-arrow {
  opacity: 1;
  color: #3b82f6;
}

.agent-name {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
  margin: 12px 0 4px 0;
}

.agent-name--active {
  color: #3b82f6;
}

.agent-desc {
  font-size: 11px;
  color: #94a3b8;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.agent-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}

.create-agent {
  padding-top: 16px;
  margin-top: 8px;
  border-top: 1px solid #f8fafc;
}

.create-btn {
  width: 100%;
  justify-content: center;
  border-style: dashed;
}

.chat-area {
  flex: 1;
  background: #fff;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
  min-width: 0;
}

.chat-banner {
  padding: 16px;
  border-bottom: 1px solid #f8fafc;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #f0fdf4 0%, #fff 100%);
  border-radius: 16px 16px 0 0;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.banner-icon {
  width: 56px;
  height: 56px;
  background: #65a30d;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(101, 163, 13, 0.3);
}

.banner-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.banner-title h2 {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.banner-tags {
  display: flex;
  gap: 6px;
}

.banner-desc {
  font-size: 12px;
  color: #64748b;
  max-width: 500px;
  margin: 0;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  overflow-y: auto;
}

.chat-empty {
  text-align: center;
}

.empty-icon {
  width: 72px;
  height: 72px;
  background: #f0fdf4;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.chat-empty h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.chat-empty p {
  font-size: 13px;
  color: #94a3b8;
  max-width: 400px;
  margin: 0 auto;
  line-height: 1.6;
}

.quick-questions {
  margin-top: 40px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
  max-width: 600px;
}

.quick-btn {
  padding: 10px 20px;
  background: #fff;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  border-radius: 9999px;
  font-size: 13px;
  transition: all 0.2s;
}

.quick-btn:hover {
  background: #f0fdf4;
  border-color: #86efac;
}

.chat-messages {
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.message-avatar.user {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.message.user .message-content {
  background: #dcf8c6;
  color: #333;
}

.message.ai .message-content {
  background: #fff;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-input-area {
  padding: 24px;
  margin-top: auto;
}

.input-container {
  background: #fff;
  border-radius: 16px;
  padding: 10px;
  box-shadow: 0 4px 15px -3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  max-width: 800px;
  margin: 0 auto;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  padding: 0 4px;
}

.send-btn {
  background: #22c55e;
  border-color: #22c55e;
}

.send-btn:hover {
  background: #16a34a;
  border-color: #16a34a;
}

.right-sidebar {
  width: 280px;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
  flex-shrink: 0;
}

.capabilities {
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.capability-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.capability-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.capability-icon {
  width: 40px;
  height: 40px;
  background: #f0fdf4;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.capability-title {
  font-size: 13px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.capability-desc {
  font-size: 11px;
  color: #94a3b8;
  margin: 4px 0 0 0;
}

.history {
  flex: 1;
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.history-header .section-title {
  margin: 0;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.history-item:hover .history-title {
  color: #3b82f6;
}

.history-icon {
  color: #cbd5e1;
  flex-shrink: 0;
}

.history-title {
  font-size: 12px;
  color: #475569;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s;
}

.history-time {
  font-size: 10px;
  color: #cbd5e1;
  flex-shrink: 0;
}

.statistics {
  padding-bottom: 8px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-header .section-title {
  margin: 0;
}

.stats-grid {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  text-align: center;
  flex: 1;
  padding: 0 8px;
}

.stat-item:not(:last-child) {
  border-right: 1px solid #f1f5f9;
}

.stat-label {
  font-size: 11px;
  color: #94a3b8;
  margin: 0 0 6px 0;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
  line-height: 1;
}

.stat-change {
  font-size: 10px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-change--up {
  color: #16a34a;
}
</style>
