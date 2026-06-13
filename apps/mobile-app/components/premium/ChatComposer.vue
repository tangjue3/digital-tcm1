<template>
	<view class="chat-composer" :style="[{ bottom: `${bottom}px` }]">
		<textarea
			class="chat-composer__input"
			:value="modelValue"
			:adjust-position="false"
			:show-confirm-bar="false"
			maxlength="300"
			placeholder="输入想咨询医生的问题..."
			cursor-spacing="18"
			@input="handleInput"
			@focus="$emit('focus', $event)"
			@blur="$emit('blur', $event)"
			@confirm="$emit('send')"
		/>
		<view class="chat-composer__send" :class="{ 'chat-composer__send--disabled': disabled }" @tap="!disabled && $emit('send')">
			<text class="chat-composer__send-text">{{ sending ? '发送中' : '发送' }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'ChatComposer',
	props: {
		modelValue: {
			type: String,
			default: ''
		},
		sending: {
			type: Boolean,
			default: false
		},
		disabled: {
			type: Boolean,
			default: false
		},
		bottom: {
			type: Number,
			default: 0
		}
	},
	methods: {
		handleInput(event) {
			this.$emit('update:modelValue', event.detail.value);
		}
	}
};
</script>

<style>
.chat-composer {
	position: fixed;
	right: 0;
	left: 0;
	padding: 12px 14px calc(12px + env(safe-area-inset-bottom));
	border-top: 1px solid rgba(163, 196, 232, 0.34);
	background: rgba(240, 245, 255, 0.88);
	backdrop-filter: blur(18px);
	box-sizing: border-box;
}

.chat-composer__input {
	display: block;
	min-height: 54px;
	max-height: 130px;
	margin-right: 78px;
	padding: 14px 16px;
	border-radius: 24px;
	border: 1px solid rgba(163, 196, 232, 0.36);
	background: rgba(255, 255, 255, 0.92);
	color: #103268;
	font-size: 15px;
	line-height: 1.5;
	box-sizing: border-box;
}

.chat-composer__send {
	position: absolute;
	top: 12px;
	right: 14px;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 60px;
	height: 54px;
	border-radius: 999px;
	background: #1f5fca;
	box-shadow: 0 8px 20px rgba(31, 95, 202, 0.18);
}

.chat-composer__send--disabled {
	opacity: 0.55;
}

.chat-composer__send-text {
	color: #ffffff;
	font-size: 14px;
	font-weight: 800;
}
</style>
