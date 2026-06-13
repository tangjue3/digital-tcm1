<template>
	<view class="ai-chat-page">
		<TaskTopBar title="智能问答" :show-right="false" @back="goBack" />

		<scroll-view
			class="ai-chat-page__scroll"
			scroll-y
			scroll-with-animation
			:scroll-into-view="scrollIntoViewId"
		>
			<view class="ai-chat-page__hero">
				<view class="ai-chat-page__agent-card">
					<view class="ai-chat-page__agent-head">
						<image class="ai-chat-page__agent-avatar" src="../../static/yi.png" mode="aspectFill"></image>
						<view class="ai-chat-page__agent-copy">
							<text class="ai-chat-page__eyebrow">TCM HEALTH AGENT</text>
							<text class="ai-chat-page__agent-name">云枢中医健康助手</text>
							<text class="ai-chat-page__agent-desc">帮你梳理症状、解释常见健康问题，并提供温和的调理建议。</text>
						</view>
					</view>

					<view class="ai-chat-page__agent-actions">
						<button class="ai-chat-page__ghost-btn" hover-class="ai-chat-page__ghost-btn--hover" @tap="startNewConversation">
							新建会话
						</button>
						<button class="ai-chat-page__ghost-btn" hover-class="ai-chat-page__ghost-btn--hover" @tap="clearConversation">
							清空会话
						</button>
					</view>
				</view>

				<view class="ai-chat-page__safety-card">
					<text class="ai-chat-page__safety-title">医疗安全提示</text>
					<text class="ai-chat-page__safety-text">
						本助手可提供中医健康咨询与生活方式建议，但不能替代医生诊断，也不会给出处方、剂量或自行停换药建议。若出现胸痛、呼吸困难、意识异常、大量出血等急症，请立即就医。
					</text>
				</view>

				<view class="ai-chat-page__chips-card">
					<text class="ai-chat-page__section-title">快捷提问</text>
					<view class="ai-chat-page__chips">
						<button
							v-for="item in quickQuestions"
							:key="item"
							class="ai-chat-page__chip"
							hover-class="ai-chat-page__chip--hover"
							:disabled="sending"
							@tap="sendQuickQuestion(item)"
						>
							{{ item }}
						</button>
					</view>
				</view>
			</view>

			<view class="ai-chat-page__conversation">
				<view v-if="!messages.length" class="ai-chat-page__empty-card">
					<text class="ai-chat-page__empty-title">开始一次智能中医健康咨询</text>
					<text class="ai-chat-page__empty-text">
						你可以描述当前不适、体质困扰、作息饮食问题，或让助手帮你整理就医前需要补充的信息。
					</text>
				</view>

				<view v-else class="ai-chat-page__message-list">
					<view
						v-for="message in displayMessages"
						:key="message.id"
						:class="[
							'ai-chat-page__message-row',
							message.role === 'user' ? 'ai-chat-page__message-row--user' : 'ai-chat-page__message-row--assistant'
						]"
					>
						<view v-if="message.role === 'assistant'" class="ai-chat-page__avatar ai-chat-page__avatar--assistant">医</view>
						<view
							:class="[
								'ai-chat-page__bubble',
								message.role === 'user' ? 'ai-chat-page__bubble--user' : 'ai-chat-page__bubble--assistant',
								message.status === 'error' ? 'ai-chat-page__bubble--error' : ''
							]"
						>
							<text class="ai-chat-page__bubble-label">{{ message.role === 'user' ? '我' : '云枢助手' }}</text>
							<text class="ai-chat-page__bubble-content">{{ message.content }}</text>
							<view v-if="message.status === 'loading'" class="ai-chat-page__thinking">
								<view class="ai-chat-page__thinking-dot"></view>
								<view class="ai-chat-page__thinking-dot"></view>
								<view class="ai-chat-page__thinking-dot"></view>
							</view>
							<text class="ai-chat-page__bubble-time">{{ message.time }}</text>
						</view>
						<view v-if="message.role === 'user'" class="ai-chat-page__avatar ai-chat-page__avatar--user">我</view>
					</view>
				</view>

				<view v-if="lastErrorMessage" class="ai-chat-page__error-card">
					<text class="ai-chat-page__error-title">这次回答没有成功返回</text>
					<text class="ai-chat-page__error-text">{{ lastErrorMessage }}</text>
					<button
						class="ai-chat-page__retry-btn"
						hover-class="ai-chat-page__retry-btn--hover"
						:disabled="sending || !lastFailedPayload"
						@tap="retryLastRequest"
					>
						重试上一条
					</button>
				</view>
			</view>

			<view id="chat-bottom-anchor" class="ai-chat-page__bottom-anchor"></view>
		</scroll-view>

		<view class="ai-chat-page__composer" :style="{ bottom: `${inputBottom}px` }">
			<view class="ai-chat-page__composer-inner">
				<textarea
					v-model="draft"
					class="ai-chat-page__textarea"
					auto-height
					maxlength="1000"
					placeholder="请输入你想咨询的健康问题"
					:adjust-position="false"
					@focus="handleFocus"
					@blur="handleBlur"
					@confirm="handleConfirm"
				/>
				<button
					class="ai-chat-page__send-btn"
					:class="{ 'ai-chat-page__send-btn--disabled': !canSend }"
					hover-class="ai-chat-page__send-btn--hover"
					:disabled="!canSend"
					@tap="sendMessage"
				>
					{{ sending ? '思考中' : '发送' }}
				</button>
			</view>
		</view>
	</view>
</template>

<script>
import { sendTcmAiChatMessage } from '../../common/api.js'
import TaskTopBar from '../../components/premium/TaskTopBar.vue'

const CACHE_KEY = 'yunshu_tcm_ai_chat_session'
const SCENE = 'tcm_health_consultation'

function generateId(prefix = 'msg') {
	return `${prefix}_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`
}

function padTime(value) {
	return String(value).padStart(2, '0')
}

function formatNow() {
	const date = new Date()
	return `${padTime(date.getHours())}:${padTime(date.getMinutes())}`
}

export default {
	components: {
		TaskTopBar
	},
	data() {
		return {
			user: {},
			sessionId: '',
			messages: [],
			draft: '',
			sending: false,
			inputBottom: 0,
			scrollIntoViewId: '',
			lastErrorMessage: '',
			lastFailedPayload: null,
			quickQuestions: [
				'最近总觉得乏力、睡不醒，适合从哪些方面先排查？',
				'脾胃虚弱时，作息和饮食上可以怎么调理？',
				'熬夜后口干、长痘、心烦，通常要注意什么？',
				'我想去医院问诊，症状该怎么整理会更高效？'
			]
		}
	},
	computed: {
		canSend() {
			return !this.sending && Boolean(this.draft.trim())
		},
		displayMessages() {
			if (!this.sending) {
				return this.messages
			}
			return this.messages.concat([
				{
					id: 'assistant_loading',
					role: 'assistant',
					content: '云枢正在整理建议，请稍候…',
					time: formatNow(),
					status: 'loading'
				}
			])
		}
	},
	onLoad() {
		if (!uni.getStorageSync('loginType') || !uni.getStorageSync('token')) {
			uni.redirectTo({
				url: '/pages/Login/Login'
			})
			return
		}
		this.loadUser()
		this.restoreSession()
		this.scrollToBottom()
	},
	methods: {
		goBack() {
			uni.navigateBack({
				delta: 1,
				fail: () => {
					uni.switchTab({
						url: '/pages/zice/zice'
					})
				}
			})
		},
		loadUser() {
			try {
				const raw = uni.getStorageSync('userInfo')
				this.user = raw ? JSON.parse(raw) : {}
			} catch (error) {
				console.error('[ai-chat] failed to parse user info', error)
				this.user = {}
			}
		},
		handleAuthExpired() {
			uni.removeStorageSync('token')
			uni.removeStorageSync('Authorization')
			uni.removeStorageSync('loginType')
			uni.removeStorageSync('userInfo')
			uni.redirectTo({
				url: '/pages/Login/Login'
			})
		},
		cacheKey() {
			return `${CACHE_KEY}:${this.user.userId || 'guest'}`
		},
		restoreSession() {
			try {
				const raw = uni.getStorageSync(this.cacheKey())
				const cached = raw ? JSON.parse(raw) : null
				this.sessionId = cached?.sessionId || ''
				this.messages = Array.isArray(cached?.messages) ? cached.messages : []
			} catch (error) {
				console.error('[ai-chat] failed to restore session', error)
				this.sessionId = ''
				this.messages = []
			}
		},
		persistSession() {
			try {
				uni.setStorageSync(this.cacheKey(), JSON.stringify({
					sessionId: this.sessionId,
					messages: this.messages
				}))
			} catch (error) {
				console.error('[ai-chat] failed to persist session', error)
			}
		},
		removeCachedSession() {
			try {
				uni.removeStorageSync(this.cacheKey())
			} catch (error) {
				console.error('[ai-chat] failed to clear session cache', error)
			}
		},
		handleFocus(event) {
			this.inputBottom = event?.detail?.height || 0
			this.scrollToBottom()
		},
		handleBlur() {
			this.inputBottom = 0
		},
		handleConfirm() {
			if (this.canSend) {
				this.sendMessage()
			}
		},
		scrollToBottom() {
			this.$nextTick(() => {
				this.scrollIntoViewId = ''
				setTimeout(() => {
					this.scrollIntoViewId = 'chat-bottom-anchor'
				}, 20)
			})
		},
		buildMessage(role, content, status = 'done') {
			return {
				id: generateId(role),
				role,
				content,
				time: formatNow(),
				status
			}
		},
		exportHistory() {
			return this.messages
				.filter((item) => (item.role === 'user' || item.role === 'assistant') && item.status !== 'error')
				.slice(-10)
				.map((item) => ({
					role: item.role,
					content: item.content
				}))
		},
		sendQuickQuestion(question) {
			if (this.sending) {
				return
			}
			this.draft = question
			this.sendMessage()
		},
		async sendMessage() {
			const content = this.draft.trim()
			if (!content || this.sending) {
				return
			}

			const payload = {
				sessionId: this.sessionId,
				message: content,
				history: this.exportHistory(),
				scene: SCENE
			}

			this.messages.push(this.buildMessage('user', content))
			this.draft = ''
			this.lastErrorMessage = ''
			this.lastFailedPayload = null
			this.persistSession()
			this.scrollToBottom()

			await this.executeChatRequest(payload)
		},
		async executeChatRequest(payload) {
			this.sending = true
			try {
				const response = await sendTcmAiChatMessage(payload)
				const answer = String(response?.answer || '').trim()
				if (!answer) {
					throw new Error('AI 服务返回内容为空，请检查模型接口配置')
				}
				this.sessionId = response?.sessionId || this.sessionId || generateId('session')
				this.messages.push(this.buildMessage('assistant', answer))
				this.lastErrorMessage = ''
				this.lastFailedPayload = null
				this.persistSession()
				this.scrollToBottom()
			} catch (error) {
				console.error('[ai-chat] failed to send message', error)
				if (error?.code === 401 || /获取用户信息异常|请先登录/.test(error?.message || '')) {
					this.handleAuthExpired()
					return
				}
				this.lastErrorMessage = error?.message || 'AI 服务暂时不可用，请稍后重试'
				this.lastFailedPayload = payload
				this.messages.push(
					this.buildMessage(
						'assistant',
						`${this.lastErrorMessage}。你刚才的问题已保留，可以直接重试。`,
						'error'
					)
				)
				this.persistSession()
				this.scrollToBottom()
			} finally {
				this.sending = false
			}
		},
		retryLastRequest() {
			if (!this.lastFailedPayload || this.sending) {
				return
			}
			if (this.messages.length && this.messages[this.messages.length - 1].status === 'error') {
				this.messages.pop()
			}
			this.persistSession()
			this.executeChatRequest(this.lastFailedPayload)
		},
		startNewConversation() {
			this.sessionId = generateId('session')
			this.messages = []
			this.lastErrorMessage = ''
			this.lastFailedPayload = null
			this.persistSession()
			uni.showToast({
				title: '已新建会话',
				icon: 'none'
			})
		},
		clearConversation() {
			uni.showModal({
				title: '清空当前会话',
				content: '仅清空本地缓存的聊天记录，是否继续？',
				success: (res) => {
					if (!res.confirm) {
						return
					}
					this.sessionId = ''
					this.messages = []
					this.lastErrorMessage = ''
					this.lastFailedPayload = null
					this.removeCachedSession()
				}
			})
		}
	}
}
</script>

<style scoped>
.ai-chat-page {
	position: relative;
	display: flex;
	flex-direction: column;
	height: 100vh;
	padding: 18px 18px 124px;
	box-sizing: border-box;
	background:
		radial-gradient(circle at top right, rgba(167, 215, 204, 0.34) 0, transparent 30%),
		radial-gradient(circle at bottom left, rgba(214, 231, 225, 0.42) 0, transparent 28%),
		linear-gradient(180deg, #f7f7f1 0%, #eef2ed 100%);
}

.ai-chat-page__scroll {
	flex: 1;
	min-height: 0;
}

.ai-chat-page__hero {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.ai-chat-page__agent-card,
.ai-chat-page__safety-card,
.ai-chat-page__chips-card,
.ai-chat-page__empty-card,
.ai-chat-page__error-card {
	position: relative;
	overflow: hidden;
	padding: 18px;
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.84);
	box-shadow: 0 18px 40px rgba(22, 63, 53, 0.08);
}

.ai-chat-page__agent-card::after,
.ai-chat-page__chips-card::after {
	content: '';
	position: absolute;
	right: -36px;
	bottom: -44px;
	width: 128px;
	height: 128px;
	border-radius: 999px;
	background: radial-gradient(circle, rgba(174, 223, 210, 0.8) 0%, rgba(174, 223, 210, 0) 72%);
}

.ai-chat-page__agent-head {
	display: flex;
	align-items: center;
	gap: 14px;
}

.ai-chat-page__agent-avatar {
	width: 64px;
	height: 64px;
	border-radius: 22px;
	background: linear-gradient(135deg, #edf5f0 0%, #d0e6dd 100%);
}

.ai-chat-page__agent-copy {
	display: flex;
	flex-direction: column;
	gap: 4px;
	flex: 1;
}

.ai-chat-page__eyebrow {
	color: #55736d;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.ai-chat-page__agent-name,
.ai-chat-page__section-title,
.ai-chat-page__empty-title,
.ai-chat-page__error-title,
.ai-chat-page__safety-title {
	color: #0d2f28;
	font-size: 22px;
	font-weight: 800;
}

.ai-chat-page__agent-desc,
.ai-chat-page__safety-text,
.ai-chat-page__empty-text,
.ai-chat-page__error-text {
	margin-top: 2px;
	color: #4b6560;
	font-size: 14px;
	line-height: 1.65;
}

.ai-chat-page__agent-actions {
	display: flex;
	gap: 10px;
	margin-top: 16px;
}

.ai-chat-page__ghost-btn,
.ai-chat-page__chip,
.ai-chat-page__retry-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 14px;
	border: 0;
	border-radius: 999px;
	font-size: 13px;
	font-weight: 700;
}

.ai-chat-page__ghost-btn {
	height: 38px;
	background: rgba(12, 88, 72, 0.08);
	color: #0c5848;
}

.ai-chat-page__ghost-btn--hover,
.ai-chat-page__chip--hover,
.ai-chat-page__retry-btn--hover,
.ai-chat-page__send-btn--hover {
	opacity: 0.88;
}

.ai-chat-page__safety-card {
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.86) 0%, rgba(241, 248, 245, 0.92) 100%);
}

.ai-chat-page__safety-title {
	font-size: 17px;
}

.ai-chat-page__chips-card {
	padding-bottom: 20px;
}

.ai-chat-page__section-title {
	font-size: 17px;
}

.ai-chat-page__chips {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-top: 14px;
}

.ai-chat-page__chip {
	min-height: 40px;
	padding: 10px 14px;
	background: #f0f5f1;
	color: #24483f;
	text-align: left;
	line-height: 1.4;
}

.ai-chat-page__conversation {
	display: flex;
	flex-direction: column;
	gap: 14px;
	padding: 18px 0 20px;
}

.ai-chat-page__empty-card {
	margin-top: 4px;
}

.ai-chat-page__message-list {
	display: flex;
	flex-direction: column;
	gap: 14px;
}

.ai-chat-page__message-row {
	display: flex;
	align-items: flex-end;
	gap: 10px;
}

.ai-chat-page__message-row--user {
	justify-content: flex-end;
}

.ai-chat-page__message-row--assistant {
	justify-content: flex-start;
}

.ai-chat-page__avatar {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 38px;
	height: 38px;
	flex-shrink: 0;
	border-radius: 14px;
	font-size: 14px;
	font-weight: 800;
}

.ai-chat-page__avatar--assistant {
	background: linear-gradient(135deg, #d6ebe3 0%, #b7dccf 100%);
	color: #0c5848;
}

.ai-chat-page__avatar--user {
	background: linear-gradient(135deg, #0c5848 0%, #186f5d 100%);
	color: #ffffff;
}

.ai-chat-page__bubble {
	display: flex;
	flex-direction: column;
	gap: 8px;
	max-width: calc(100% - 56px);
	padding: 14px 16px;
	border-radius: 24px;
	box-shadow: 0 10px 28px rgba(20, 59, 50, 0.06);
}

.ai-chat-page__bubble--assistant {
	background: rgba(255, 255, 255, 0.92);
	border-top-left-radius: 10px;
}

.ai-chat-page__bubble--user {
	background: linear-gradient(135deg, #0d5647 0%, #176e5d 100%);
	border-top-right-radius: 10px;
}

.ai-chat-page__bubble--error {
	background: linear-gradient(135deg, #fff7f3 0%, #fff2eb 100%);
}

.ai-chat-page__bubble-label {
	color: #58716b;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.1em;
}

.ai-chat-page__bubble--user .ai-chat-page__bubble-label,
.ai-chat-page__bubble--user .ai-chat-page__bubble-content,
.ai-chat-page__bubble--user .ai-chat-page__bubble-time {
	color: #ffffff;
}

.ai-chat-page__bubble-content {
	color: #173531;
	font-size: 15px;
	line-height: 1.75;
	white-space: pre-wrap;
	word-break: break-word;
}

.ai-chat-page__bubble-time {
	color: rgba(82, 106, 101, 0.82);
	font-size: 11px;
}

.ai-chat-page__thinking {
	display: flex;
	align-items: center;
	gap: 6px;
}

.ai-chat-page__thinking-dot {
	width: 7px;
	height: 7px;
	border-radius: 999px;
	background: #7caea1;
	animation: ai-chat-thinking 1.2s infinite ease-in-out;
}

.ai-chat-page__thinking-dot:nth-child(2) {
	animation-delay: 0.15s;
}

.ai-chat-page__thinking-dot:nth-child(3) {
	animation-delay: 0.3s;
}

.ai-chat-page__error-card {
	padding-top: 16px;
}

.ai-chat-page__error-title {
	font-size: 16px;
}

.ai-chat-page__retry-btn {
	width: 124px;
	height: 40px;
	margin-top: 14px;
	background: #0c5848;
	color: #ffffff;
}

.ai-chat-page__bottom-anchor {
	height: 6px;
}

.ai-chat-page__composer {
	position: fixed;
	right: 18px;
	left: 18px;
	bottom: 0;
	padding-bottom: calc(env(safe-area-inset-bottom) + 12px);
}

.ai-chat-page__composer-inner {
	display: flex;
	align-items: flex-end;
	gap: 12px;
	padding: 14px;
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.96);
	box-shadow: 0 18px 40px rgba(22, 63, 53, 0.12);
}

.ai-chat-page__textarea {
	flex: 1;
	min-height: 24px;
	max-height: 120px;
	color: #173531;
	font-size: 15px;
	line-height: 1.6;
}

.ai-chat-page__send-btn {
	width: 92px;
	height: 48px;
	flex-shrink: 0;
	border: 0;
	border-radius: 18px;
	background: linear-gradient(135deg, #0d5647 0%, #196a5a 100%);
	color: #ffffff;
	font-size: 15px;
	font-weight: 800;
}

.ai-chat-page__send-btn--disabled {
	background: #d7dfdb;
	color: #7f9791;
}

@keyframes ai-chat-thinking {
	0%,
	80%,
	100% {
		transform: scale(0.7);
		opacity: 0.5;
	}

	40% {
		transform: scale(1);
		opacity: 1;
	}
}
</style>
