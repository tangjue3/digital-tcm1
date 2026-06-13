<template>
  <div class="app-container ai-assistant-dialog">
    <div class="assistant-header">
      <div>
        <h3>中医AI小助手</h3>
        <p>面向医生的简明临床辅助</p>
      </div>
      <div class="header-actions">
        <button
          v-if="radioInfoList.length > 0 || condition"
          type="button"
          class="clear-button"
          @click="clearSession"
        >
          清空记录
        </button>
        <button type="button" class="close-button" @click="$emit('closeDialog')">x</button>
      </div>
    </div>

    <div id="dataShow" ref="messageBox" class="data-show">
      <div v-if="radioInfoList.length === 0" class="ai-assistant-tip">
        输入主诉、病史、舌脉或用药情况，我会先结合本地知识库给出简明辨证思路和风险提示。
      </div>

      <div v-for="(item, index) in radioInfoList" :key="index" class="message-block">
        <div v-if="item.customerSen" class="message-row user-row">
          <div class="bubble user-bubble">{{ item.customerSen }}</div>
        </div>

        <div class="message-row assistant-row">
          <div class="avatar">AI</div>
          <div class="assistant-content">
            <div class="bubble assistant-bubble" :class="{ error: item.error }">
              <span v-if="item.loading">正在检索知识库并生成回答...</span>
              <span v-else>{{ item.robotSen }}</span>
            </div>
            <div class="message-time">{{ item.robotTime }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="composer">
      <el-input
        id="condition"
        v-model="condition"
        type="textarea"
        autofocus
        clearable
        maxlength="600"
        show-word-limit
        resize="none"
        :disabled="sending"
        placeholder="输入主诉、四诊信息、既往史、过敏史、用药史或你的临床问题"
        @keyup.ctrl.enter="submitSearch"
      />
      <el-button id="submit" type="primary" :loading="sending" @click="submitSearch">
        发送
      </el-button>
    </div>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import { sendTcmChat } from '@/api/tcmAi'
import { clearTcmAiSession, loadTcmAiSession, saveTcmAiSession } from '@/utils/tcmAiSession'

const MAX_HISTORY_TURNS = 6
const MAX_MESSAGE_RECORDS = 20

export default {
  name: 'TcmAiAssistant',
  emits: ['closeDialog'],
  props: {
    storageKey: {
      type: String,
      default: 'floating-dialog'
    }
  },
  data() {
    return {
      condition: '',
      radioInfoList: [],
      history: [],
      sending: false,
      topK: 4
    }
  },
  mounted() {
    this.restoreSession()
    this.scrollToBottom()
  },
  methods: {
    getPersistKey() {
      return this.storageKey || 'floating-dialog'
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const div = this.$refs.messageBox
        if (div) {
          div.scrollTop = div.scrollHeight
        }
      })
    },
    clearBr(value) {
      return String(value || '')
        .replace(/<\/?.+?>/g, '')
        .replace(/[\r\n]+/g, '\n')
        .trim()
    },
    formatCurrentTime() {
      const date = new Date()
      return date.toLocaleString('zh-CN', {
        hour12: false,
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    normalizeRows(rows = []) {
      return rows
        .filter(item => {
          if (!item) {
            return false
          }
          if (item.loading && !item.robotSen) {
            return false
          }
          return item.customerSen || item.robotSen
        })
        .slice(-MAX_MESSAGE_RECORDS)
        .map(item => ({
          customerSen: String(item.customerSen || ''),
          customerTime: String(item.customerTime || ''),
          robotSen: String(item.robotSen || ''),
          robotTime: String(item.robotTime || ''),
          loading: false,
          error: Boolean(item.error)
        }))
    },
    normalizeHistory(history = []) {
      return history
        .filter(item => item && item.content)
        .slice(-MAX_HISTORY_TURNS * 2)
        .map(item => ({
          role: item.role === 'assistant' ? 'assistant' : 'user',
          content: String(item.content || '')
        }))
    },
    persistSession() {
      saveTcmAiSession(this.getPersistKey(), {
        version: 1,
        draft: this.condition,
        history: this.normalizeHistory(this.history),
        messages: this.radioInfoList.slice(-MAX_MESSAGE_RECORDS).map(item => ({
          customerSen: item.customerSen || '',
          customerTime: item.customerTime || '',
          robotSen: item.robotSen || '',
          robotTime: item.robotTime || '',
          loading: Boolean(item.loading),
          error: Boolean(item.error)
        })),
        updatedAt: Date.now()
      })
    },
    restoreSession() {
      const session = loadTcmAiSession(this.getPersistKey())
      if (!session) {
        return
      }
      this.condition = String(session.draft || '')
      this.history = this.normalizeHistory(session.history)
      this.radioInfoList = this.normalizeRows(session.messages)
    },
    async clearSession() {
      try {
        await ElMessageBox.confirm('确认清空当前临时聊天记录吗？', '提示', {
          confirmButtonText: '清空',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch (error) {
        return
      }
      this.condition = ''
      this.history = []
      this.radioInfoList = []
      this.sending = false
      clearTcmAiSession(this.getPersistKey())
      ElMessage.success('临时聊天记录已清空')
    },
    async submitSearch() {
      const content = this.clearBr(this.condition)
      if (!content) {
        ElMessage.error('请输入需要咨询的问题')
        return
      }
      if (this.sending) {
        return
      }

      const row = {
        customerSen: content,
        customerTime: this.formatCurrentTime(),
        robotSen: '',
        robotTime: '',
        loading: true,
        error: false
      }
      this.radioInfoList.push(row)
      this.condition = ''
      this.sending = true
      this.persistSession()
      this.scrollToBottom()

      try {
        const response = await sendTcmChat({
          message: content,
          history: this.history.slice(-MAX_HISTORY_TURNS * 2),
          patientInfo: {},
          topK: this.topK
        })
        if (response?.code !== undefined && response.code !== 200) {
          throw new Error(response.msg || 'AI 助手调用失败')
        }
        const data = response?.data || {}
        row.robotSen = data.answer || '本次没有生成有效回答，请稍后重试。'
        row.robotTime = this.formatCurrentTime()
        this.history.push({ role: 'user', content })
        this.history.push({ role: 'assistant', content: row.robotSen })
        this.history = this.normalizeHistory(this.history)
        this.radioInfoList = this.normalizeRows(this.radioInfoList)
      } catch (error) {
        console.error('[tcm-ai-assistant] request failed', error)
        row.error = true
        row.robotSen = error?.response?.data?.msg || error?.message || 'AI 助手暂时不可用，请检查后端服务与 DeepSeek 配置。'
        row.robotTime = this.formatCurrentTime()
        ElMessage.error(row.robotSen)
        this.radioInfoList = this.normalizeRows(this.radioInfoList)
      } finally {
        row.loading = false
        this.sending = false
        this.persistSession()
        this.scrollToBottom()
      }
    }
  },
  watch: {
    condition() {
      this.persistSession()
    }
  }
}
</script>

<style scoped>
.ai-assistant-dialog {
  display: flex;
  flex-direction: column;
  width: 800px;
  max-width: calc(100vw - 32px);
  max-height: calc(100vh - 40px);
  padding: 16px;
  border-radius: 10px;
  background: #f7fbf8;
  box-shadow: 0 18px 42px rgba(36, 77, 52, 0.2);
}

.assistant-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #dbe8dc;
}

.assistant-header h3 {
  margin: 0;
  color: #1f4b35;
  font-size: 18px;
}

.assistant-header p {
  margin: 4px 0 0;
  color: #6b7d72;
  font-size: 12px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.clear-button,
.close-button {
  border: 0;
  cursor: pointer;
}

.clear-button {
  height: 32px;
  padding: 0 12px;
  border-radius: 16px;
  background: #eef4ef;
  color: #416453;
  font-size: 12px;
}

.close-button {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e8f1ea;
  color: #2d6a4f;
  font-size: 18px;
  line-height: 32px;
}

.data-show {
  flex: 1;
  min-height: 420px;
  max-height: 600px;
  overflow-y: auto;
  padding: 14px;
  border: 1px solid #dbe8dc;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.96);
}

.ai-assistant-tip {
  display: inline-block;
  max-width: 88%;
  padding: 16px 18px;
  border: 1px solid #c9bba3;
  border-radius: 8px;
  background: #f4ede4;
  color: #25342b;
  font-size: 14px;
  line-height: 1.8;
}

.message-block {
  margin-bottom: 18px;
}

.message-row {
  display: flex;
  margin-bottom: 10px;
}

.user-row {
  justify-content: flex-end;
}

.assistant-row {
  align-items: flex-start;
}

.avatar {
  flex: 0 0 34px;
  width: 34px;
  height: 34px;
  margin-right: 10px;
  border-radius: 50%;
  background: #2d6a4f;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  line-height: 34px;
  text-align: center;
}

.assistant-content {
  min-width: 0;
  max-width: 82%;
}

.bubble {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.7;
}

.user-bubble {
  max-width: 76%;
  padding: 10px 14px;
  border-radius: 14px 14px 4px 14px;
  background: #d8f3dc;
  color: #1b4332;
}

.assistant-bubble {
  padding: 10px 14px;
  border: 1px solid #e3ece5;
  border-radius: 14px 14px 14px 4px;
  background: #fff;
  color: #223128;
}

.assistant-bubble.error {
  border-color: #f3c0bd;
  background: #fff4f3;
  color: #9f2d25;
}

.message-time {
  margin-top: 5px;
  color: #8a9b90;
  font-size: 12px;
}

.composer {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 88px;
  gap: 10px;
  margin-top: 12px;
}

#condition {
  min-height: 62px;
}

#submit {
  min-height: 62px;
  background: #40916c;
  border-color: #40916c;
}
</style>
