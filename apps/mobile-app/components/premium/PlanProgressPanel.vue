<template>
	<view class="plan-progress-panel">
		<view class="plan-progress-panel__mist plan-progress-panel__mist--large"></view>
		<view class="plan-progress-panel__mist plan-progress-panel__mist--small"></view>
		<view class="plan-progress-panel__content">
			<text class="plan-progress-panel__eyebrow">{{ eyebrow }}</text>
			<text class="plan-progress-panel__title">{{ title }}</text>
			<text class="plan-progress-panel__desc">{{ description }}</text>
			<view v-if="badge" class="plan-progress-panel__badge">{{ badge }}</view>
		</view>
		<view class="plan-progress-panel__ring">
			<PremiumProgressRing :progress="progress" label="完成度" />
		</view>
	</view>
</template>

<script>
import PremiumProgressRing from './PremiumProgressRing.vue';

export default {
	name: 'PlanProgressPanel',
	components: {
		PremiumProgressRing
	},
	props: {
		eyebrow: {
			type: String,
			default: 'DAILY WELLNESS'
		},
		title: {
			type: String,
			required: true
		},
		description: {
			type: String,
			required: true
		},
		badge: {
			type: String,
			default: ''
		},
		progress: {
			type: [Number, String],
			default: 0
		}
	}
};
</script>

<style>
.plan-progress-panel {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 22px 20px;
	overflow: hidden;
	border: 1px solid rgba(255, 255, 255, 0.82);
	border-radius: 30px;
	background: rgba(255, 255, 255, 0.72);
	box-shadow: 0 16px 40px rgba(31, 95, 202, 0.08);
	backdrop-filter: blur(18px);
}

.plan-progress-panel__mist {
	position: absolute;
	border-radius: 999px;
	pointer-events: none;
}

.plan-progress-panel__mist--large {
	top: -56px;
	right: -48px;
	width: 180px;
	height: 180px;
	background: rgba(148, 211, 193, 0.18);
	filter: blur(10px);
}

.plan-progress-panel__mist--small {
	right: 120px;
	bottom: -28px;
	width: 94px;
	height: 94px;
	background: rgba(208, 231, 234, 0.34);
}

.plan-progress-panel__content {
	position: relative;
	z-index: 1;
	display: flex;
	max-width: 190px;
	flex-direction: column;
}

.plan-progress-panel__eyebrow {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.plan-progress-panel__title {
	margin-top: 10px;
	color: #103268;
	font-size: 30px;
	font-weight: 800;
	line-height: 1.15;
}

.plan-progress-panel__desc {
	margin-top: 10px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.6;
}

.plan-progress-panel__badge {
	align-self: flex-start;
	margin-top: 14px;
	padding: 6px 12px;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.62);
	color: #065043;
	font-size: 11px;
	font-weight: 800;
}

.plan-progress-panel__ring {
	position: relative;
	z-index: 1;
	margin-left: 14px;
}
</style>
