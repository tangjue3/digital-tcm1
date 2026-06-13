<template>
	<view class="consult-action-card" @tap="$emit('tap')">
		<view class="consult-action-card__icon">{{ marker }}</view>
		<view class="consult-action-card__body">
			<text class="consult-action-card__title">{{ title }}</text>
			<text class="consult-action-card__subtitle">{{ subtitle }}</text>
		</view>
		<view class="consult-action-card__glow"></view>
	</view>
</template>

<script>
export default {
	name: 'ConsultActionCard',
	props: {
		title: {
			type: String,
			required: true
		},
		subtitle: {
			type: String,
			default: ''
		},
		marker: {
			type: String,
			default: '📋'
		}
	}
};
</script>

<style>
.consult-action-card {
	position: relative;
	display: flex;
	align-items: center;
	gap: 14px;
	min-height: 94px;
	padding: 18px;
	overflow: hidden;
	border: 1px solid rgba(255, 255, 255, 0.86);
	border-radius: 24px;
	background: linear-gradient(145deg, rgba(255, 255, 255, 0.9), rgba(163, 196, 232, 0.3));
	box-shadow: 0 10px 28px rgba(31, 95, 202, 0.07);
}

.consult-action-card__icon {
	position: relative;
	z-index: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 52px;
	height: 52px;
	flex-shrink: 0;
	border-radius: 18px;
	background: rgba(163, 196, 232, 0.76);
	color: #1f5fca;
	font-size: 18px;
	font-weight: 800;
}

.consult-action-card__body {
	position: relative;
	z-index: 1;
	display: flex;
	flex: 1;
	min-width: 0;
	flex-direction: column;
}

.consult-action-card__title {
	color: #1f5fca;
	font-size: 16px;
	font-weight: 800;
}

.consult-action-card__subtitle {
	margin-top: 6px;
	color: #5a7a99;
	font-size: 12px;
	line-height: 1.5;
}

.consult-action-card__glow {
	position: absolute;
	right: -22px;
	bottom: -30px;
	width: 94px;
	height: 94px;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.24);
}
</style>
