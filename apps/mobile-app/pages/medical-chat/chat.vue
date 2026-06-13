<template>
	<view class="chat-page">
		<TaskTopBar title="云枢问答" :show-right="false" @back="goBack" />

		<view class="chat-header">
			<view>
				<text class="chat-title">医生在线回复</text>
				<text class="chat-subtitle">{{ doctorName }}</text>
			</view>
		</view>

		<scroll-view class="message-list" scroll-y :scroll-top="scrollTop" scroll-with-animation>
			<view v-if="!messages.length" class="empty-state">
				<text class="empty-title">还没有聊天记录</text>
				<text class="empty-text">发送后会同步到后台，并在当前设备保留一份本地缓存。</text>
			</view>

			<view v-for="message in messages" :key="message.messageId" class="message-row">
				<view v-if="message.senderRole === 'patient'" class="patient-row">
					<image class="avatar patient-avatar" :src="patientAvatar" mode="aspectFill" />
					<view class="bubble-wrap patient-wrap">
						<text class="sender-name patient-name">{{ message.senderName || fallbackSenderName(message.senderRole) }}</text>
						<view class="bubble patient-bubble">
							<text class="bubble-content">{{ message.content }}</text>
						</view>
						<text class="time-text patient-time">{{ message.sendTime || '' }}</text>
					</view>
				</view>
				<view v-else class="doctor-row">
					<image class="avatar doctor-avatar" :src="doctorAvatar" mode="aspectFill" />
					<view class="bubble-wrap doctor-wrap">
						<text class="sender-name doctor-name">{{ message.senderName || fallbackSenderName(message.senderRole) }}</text>
						<view class="bubble doctor-bubble">
							<text class="bubble-content">{{ message.content }}</text>
						</view>
						<text class="time-text doctor-time">{{ message.sendTime || '' }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<view class="composer" :style="[{ bottom: inputBottom + 'px' }]">
			<textarea
				v-model="draft"
				class="composer-input"
				:adjust-position="false"
				:show-confirm-bar="false"
				maxlength="300"
				placeholder="输入想咨询医生的问题"
				cursor-spacing="18"
				@focus="handleFocus"
				@blur="handleBlur"
				@confirm="sendMessage"
			/>
			<view class="send-button" :class="{ disabled: sending || !draft.trim() }" @tap="sendMessage">
				<text class="send-button-text">{{ sending ? '发送中' : '发送' }}</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getAppChatConversation, sendAppChatMessage } from '../../common/api.js'
import TaskTopBar from '../../components/premium/TaskTopBar.vue'

const CACHE_PREFIX = 'medical-chat-cache:'

export default {
	components: {
		TaskTopBar
	},
	data() {
		return {
			doctorName: '接诊医生',
			patientAvatar: '../../static/huan.png',
			doctorAvatar: '../../static/yi.png',
			messages: [],
			draft: '',
			sending: false,
			scrollTop: 0,
			inputBottom: 0,
			user: {},
			pollTimer: null,
			lastConversationSignature: ''
		}
	},
	onLoad() {
		if (!uni.getStorageSync('loginType')) {
			uni.redirectTo({
				url: '/pages/Login/Login'
			})
			return
		}
		this.loadUser()
		this.restoreConversation()
		this.fetchConversation()
	},
	onShow() {
		this.startPolling()
	},
	onHide() {
		this.stopPolling()
	},
	onUnload() {
		this.stopPolling()
	},
	methods: {
		goBack() {
			uni.navigateBack({
				delta: 1,
				fail: () => {
					uni.switchTab({
						url: '/pages/my/my'
					})
				}
			})
		},
		cacheKey() {
			return `${CACHE_PREFIX}${this.user.userId || 'guest'}`
		},
		buildConversationSignature(conversation) {
			return JSON.stringify({
				doctorName: conversation?.doctorName || '',
				messages: conversation?.messages || []
			})
		},
		loadUser() {
			try {
				const raw = uni.getStorageSync('userInfo')
				this.user = raw ? JSON.parse(raw) : {}
			} catch (error) {
				console.error('[chat] failed to parse user info', error)
				this.user = {}
			}
		},
		restoreConversation() {
			try {
				const cached = uni.getStorageSync(this.cacheKey())
				const parsed = cached ? JSON.parse(cached) : null
				if (parsed?.messages) {
					this.messages = parsed.messages
					this.doctorName = parsed.doctorName || this.doctorName
					this.lastConversationSignature = this.buildConversationSignature(parsed)
					this.scrollToBottom()
				}
			} catch (error) {
				console.error('[chat] failed to restore cache', error)
			}
		},
		cacheConversation(conversation) {
			try {
				uni.setStorageSync(this.cacheKey(), JSON.stringify(conversation))
			} catch (error) {
				console.error('[chat] failed to cache conversation', error)
			}
		},
		applyConversation(conversation) {
			const nextConversation = {
				doctorName: conversation?.doctorName || '接诊医生',
				messages: conversation?.messages || []
			}
			const nextSignature = this.buildConversationSignature(nextConversation)
			if (nextSignature === this.lastConversationSignature) {
				return
			}
			this.messages = nextConversation.messages
			this.doctorName = nextConversation.doctorName
			this.lastConversationSignature = nextSignature
			this.cacheConversation(nextConversation)
			this.scrollToBottom()
		},
		async fetchConversation(showError = false) {
			try {
				const conversation = await getAppChatConversation()
				this.applyConversation(conversation)
			} catch (error) {
				console.error('[chat] failed to fetch conversation', error)
				if (showError) {
					uni.showToast({
						title: error?.message || '聊天记录加载失败',
						icon: 'none'
					})
				}
			}
		},
		scrollToBottom() {
			this.$nextTick(() => {
				this.scrollTop += 100000
			})
		},
		fallbackSenderName(role) {
			if (role === 'patient') {
				return this.user.nickName || this.user.nickname || this.user.userName || '我'
			}
			return this.doctorName || '接诊医生'
		},
		handleFocus(event) {
			this.inputBottom = event?.detail?.height || 0
		},
		handleBlur() {
			this.inputBottom = 0
		},
		startPolling() {
			this.stopPolling()
			this.pollTimer = setInterval(() => {
				this.fetchConversation(false)
			}, 4000)
		},
		stopPolling() {
			if (this.pollTimer) {
				clearInterval(this.pollTimer)
				this.pollTimer = null
			}
		},
		async sendMessage() {
			const content = this.draft.trim()
			if (!content || this.sending) {
				return
			}
			this.sending = true
			try {
				await sendAppChatMessage(content)
				this.draft = ''
				await this.fetchConversation(true)
			} catch (error) {
				console.error('[chat] failed to send message', error)
				uni.showToast({
					title: error?.message || '发送失败',
					icon: 'none'
				})
			} finally {
				this.sending = false
			}
		}
	}
}
</script>

<style scoped>
.chat-page {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background:
		radial-gradient(circle at top, rgba(63, 140, 255, 0.12), transparent 36%),
		linear-gradient(180deg, #f5f9ff 0%, #eef5ff 100%);
}

.chat-header {
	padding: 16px 14px 10px;
	background: rgba(255, 255, 255, 0.94);
	border-bottom: 1px solid #dbe7fb;
}

.chat-title {
	display: block;
	color: #17324d;
	font-size: 18px;
	font-weight: 700;
}

.chat-subtitle {
	display: block;
	margin-top: 4px;
	color: #6b7d90;
	font-size: 12px;
}

.message-list {
	flex: 1;
	padding: 12px 12px 90px;
	box-sizing: border-box;
}

.message-row {
	margin-bottom: 14px;
}

.doctor-row,
.patient-row {
	position: relative;
	width: 100%;
	min-height: 76rpx;
}

.avatar {
	width: 38px;
	height: 38px;
	position: absolute;
	top: 0;
	border-radius: 50%;
	background: #dce9ff;
}

.doctor-avatar {
	left: 0;
}

.patient-avatar {
	right: 0;
}

.bubble-wrap {
	display: block;
	min-width: 0;
}

.doctor-wrap {
	margin-left: 48px;
	margin-right: 60px;
	text-align: left;
}

.patient-wrap {
	margin-left: 60px;
	margin-right: 48px;
	text-align: right;
}

.sender-name {
	display: block;
	margin-bottom: 5px;
	color: #6b7d90;
	font-size: 12px;
}

.patient-name,
.patient-time {
	text-align: right;
}

.doctor-name,
.doctor-time {
	text-align: left;
}

.bubble {
	display: inline-block;
	max-width: 100%;
	padding: 10px 12px;
	border-radius: 13px 13px 13px 5px;
	box-sizing: border-box;
	box-shadow: 0 6px 12px rgba(23, 50, 77, 0.08);
}

.doctor-bubble {
	background: #ffffff;
	color: #17324d;
}

.patient-bubble {
	border-radius: 13px 13px 5px 13px;
	background: linear-gradient(135deg, #1f5fca 0%, #4a96ff 100%);
	color: #ffffff;
}

.bubble-content {
	display: block;
	max-width: 100%;
	color: inherit;
	font-size: 16px;
	line-height: 1.65;
	word-break: break-all;
	white-space: pre-wrap;
	text-align: left;
}

.time-text {
	display: block;
	margin-top: 5px;
	color: #8ea0b3;
	font-size: 12px;
}

.composer {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	min-height: 70px;
	padding: 10px 12px calc(10px + env(safe-area-inset-bottom));
	background: rgba(255, 255, 255, 0.96);
	border-top: 1px solid #dbe7fb;
	box-sizing: border-box;
}

.composer-input {
	display: block;
	min-height: 50px;
	max-height: 120px;
	margin-right: 90px;
	padding: 10px 12px;
	border-radius: 14px;
	background: #f4f8ff;
	color: #17324d;
	font-size: 16px;
	line-height: 1.5;
	box-sizing: border-box;
}

.send-button {
	position: absolute;
	right: 12px;
	top: 10px;
	width: 72px;
	height: 50px;
	line-height: 50px;
	padding: 0;
	margin: 0;
	border-radius: 14px;
	background: linear-gradient(135deg, #1f5fca 0%, #4a96ff 100%);
	color: #ffffff;
	font-size: 16px;
	font-weight: 700;
}

.send-button-text {
	display: block;
	width: 100%;
	color: inherit;
	font-size: 16px;
	font-weight: 700;
	line-height: 50px;
	text-align: center;
	white-space: nowrap;
}

.send-button.disabled {
	opacity: 0.6;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	min-height: 60vh;
	padding: 0 60rpx;
	text-align: center;
}

.empty-title {
	display: block;
	color: #17324d;
	font-size: 18px;
	font-weight: 700;
}

.empty-text {
	display: block;
	margin-top: 8px;
	color: #6b7d90;
	font-size: 14px;
	line-height: 1.7;
}
</style>
