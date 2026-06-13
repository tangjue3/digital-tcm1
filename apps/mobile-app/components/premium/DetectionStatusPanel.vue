<template>
	<view class="detection-status-panel">
		<view class="detection-status-panel__orb-shell">
			<view class="detection-status-panel__ring detection-status-panel__ring--outer"></view>
			<view class="detection-status-panel__ring detection-status-panel__ring--middle"></view>
			<view class="detection-status-panel__ring detection-status-panel__ring--inner"></view>
			<view class="detection-status-panel__core">
				<text class="detection-status-panel__core-text">{{ coreText }}</text>
			</view>
		</view>

		<view class="detection-status-panel__copy">
			<text class="detection-status-panel__title">{{ title }}</text>
			<text class="detection-status-panel__desc">{{ description }}</text>
		</view>

		<view v-if="showProgress" class="detection-status-panel__progress">
			<view class="detection-status-panel__progress-fill"></view>
		</view>

		<view v-if="pulseRate" class="detection-status-panel__result">
			<text class="detection-status-panel__result-value">{{ pulseRate }}</text>
			<text class="detection-status-panel__result-unit">次/ 分钟</text>
			<text v-if="syncStatusText" class="detection-status-panel__result-tip">{{ syncStatusText }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'DetectionStatusPanel',
	props: {
		title: {
			type: String,
			required: true
		},
		description: {
			type: String,
			required: true
		},
		coreText: {
			type: String,
			default: '0'
		},
		showProgress: {
			type: Boolean,
			default: false
		},
		pulseRate: {
			type: [Number, String],
			default: 0
		},
		syncStatusText: {
			type: String,
			default: ''
		}
	}
};
</script>

<style>
.detection-status-panel {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 24px 20px;
	border-radius: 30px;
	background: rgba(255, 255, 255, 0.76);
	box-shadow: 0 18px 42px rgba(31, 95, 202, 0.07);
	backdrop-filter: blur(18px);
}

.detection-status-panel__orb-shell {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 300px;
	height: 300px;
	max-width: 100%;
}

.detection-status-panel__ring {
	position: absolute;
	border-radius: 999px;
}

.detection-status-panel__ring--outer {
	inset: 0;
	border: 1px solid rgba(31, 95, 202, 0.05);
	background: rgba(31, 95, 202, 0.01);
}

.detection-status-panel__ring--middle {
	inset: 32px;
	border: 1px solid rgba(31, 95, 202, 0.1);
	background: rgba(31, 95, 202, 0.03);
}

.detection-status-panel__ring--inner {
	inset: 64px;
	border: 1px solid rgba(31, 95, 202, 0.18);
	background: rgba(31, 95, 202, 0.06);
}

.detection-status-panel__core {
	position: relative;
	z-index: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 126px;
	height: 126px;
	border: 1px solid rgba(255, 255, 255, 0.3);
	border-radius: 999px;
	background: linear-gradient(135deg, #1f5fca 0%, #1f5fca 100%);
	box-shadow: 0 16px 36px rgba(31, 95, 202, 0.28);
}

.detection-status-panel__core-text {
	color: #ffffff;
	font-size: 44px;
	font-weight: 800;
}

.detection-status-panel__copy {
	margin-top: 14px;
	text-align: center;
}

.detection-status-panel__title {
	color: #1f5fca;
	font-size: 28px;
	font-weight: 800;
	line-height: 1.2;
}

.detection-status-panel__desc {
	display: block;
	margin-top: 8px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.6;
}

.detection-status-panel__progress {
	width: 190px;
	height: 7px;
	margin-top: 18px;
	border-radius: 999px;
	background: #e1e3e0;
	overflow: hidden;
}

.detection-status-panel__progress-fill {
	width: 62%;
	height: 100%;
	border-radius: 999px;
	background: linear-gradient(90deg, #1f5fca 0%, #a3c4e8 100%);
}

.detection-status-panel__result {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-top: 18px;
}

.detection-status-panel__result-value {
	color: #103268;
	font-size: 54px;
	font-weight: 800;
	line-height: 1;
}

.detection-status-panel__result-unit {
	margin-top: 8px;
	color: #5a7a99;
	font-size: 14px;
}

.detection-status-panel__result-tip {
	margin-top: 10px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.6;
	text-align: center;
}
</style>
