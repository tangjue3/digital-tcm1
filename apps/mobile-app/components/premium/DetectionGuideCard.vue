<template>
	<view class="detection-guide-card">
		<view class="detection-guide-card__icon">{{ marker }}</view>
		<view class="detection-guide-card__copy">
			<text class="detection-guide-card__title">{{ title }}</text>
			<text class="detection-guide-card__text">{{ text }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'DetectionGuideCard',
	props: {
		title: {
			type: String,
			required: true
		},
		text: {
			type: String,
			required: true
		},
		marker: {
			type: String,
			default: '◉'
		}
	}
};
</script>

<style>
.detection-guide-card {
	display: flex;
	align-items: center;
	gap: 14px;
	padding: 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 24px;
	background: rgba(255, 255, 255, 0.8);
	box-shadow: 0 12px 28px rgba(31, 95, 202, 0.05);
}

.detection-guide-card__icon {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 52px;
	height: 52px;
	flex-shrink: 0;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.58);
	color: #1f5fca;
	font-size: 18px;
	font-weight: 800;
}

.detection-guide-card__copy {
	display: flex;
	flex: 1;
	min-width: 0;
	flex-direction: column;
}

.detection-guide-card__title {
	color: #103268;
	font-size: 16px;
	font-weight: 800;
}

.detection-guide-card__text {
	margin-top: 6px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.6;
}
</style>
