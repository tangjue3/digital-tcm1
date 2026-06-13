<template>
	<view :class="['chat-bubble', isPatient ? 'chat-bubble--patient' : 'chat-bubble--doctor']">
		<image class="chat-bubble__avatar" :src="avatar" mode="aspectFill"></image>
		<view class="chat-bubble__body">
			<text class="chat-bubble__sender">{{ senderName }}</text>
			<view :class="['chat-bubble__panel', isPatient ? 'chat-bubble__panel--patient' : 'chat-bubble__panel--doctor']">
				<text class="chat-bubble__content">{{ content }}</text>
			</view>
			<text class="chat-bubble__time">{{ timeText }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'ChatBubble',
	props: {
		role: {
			type: String,
			default: 'doctor'
		},
		avatar: {
			type: String,
			required: true
		},
		senderName: {
			type: String,
			required: true
		},
		content: {
			type: String,
			required: true
		},
		timeText: {
			type: String,
			default: ''
		}
	},
	computed: {
		isPatient() {
			return this.role === 'patient';
		}
	}
};
</script>

<style>
.chat-bubble {
	display: flex;
	align-items: flex-end;
	gap: 10px;
	max-width: 84%;
}

.chat-bubble--patient {
	align-self: flex-end;
	flex-direction: row-reverse;
}

.chat-bubble--doctor {
	align-self: flex-start;
}

.chat-bubble__avatar {
	width: 34px;
	height: 34px;
	flex-shrink: 0;
	border-radius: 999px;
	background: #e7e9e6;
}

.chat-bubble__body {
	display: flex;
	flex-direction: column;
	min-width: 0;
}

.chat-bubble--patient .chat-bubble__body {
	align-items: flex-end;
}

.chat-bubble__sender,
.chat-bubble__time {
	color: #7a8fa8;
	font-size: 11px;
}

.chat-bubble__sender {
	margin-bottom: 5px;
}

.chat-bubble__time {
	margin-top: 5px;
}

.chat-bubble__panel {
	padding: 14px 16px;
	border-radius: 20px;
	box-shadow: 0 6px 18px rgba(31, 95, 202, 0.05);
}

.chat-bubble__panel--doctor {
	border: 1px solid rgba(255, 255, 255, 0.82);
	border-bottom-left-radius: 8px;
	background: rgba(255, 255, 255, 0.92);
}

.chat-bubble__panel--patient {
	border-bottom-right-radius: 8px;
	background: rgba(208, 231, 234, 0.9);
}

.chat-bubble__content {
	color: #103268;
	font-size: 15px;
	line-height: 1.65;
	word-break: break-all;
	white-space: pre-wrap;
}
</style>
