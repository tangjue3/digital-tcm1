<template>
	<view class="premium-progress-ring" :style="ringStyle">
		<view class="premium-progress-ring__inner">
			<text class="premium-progress-ring__value">{{ safeProgress }}%</text>
			<text class="premium-progress-ring__label">{{ label }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'PremiumProgressRing',
	props: {
		progress: {
			type: [Number, String],
			default: 0
		},
		label: {
			type: String,
			default: '完成度'
		}
	},
	computed: {
		safeProgress() {
			const value = Number(this.progress);
			if (Number.isNaN(value)) return 0;
			return Math.max(0, Math.min(100, Math.round(value)));
		},
		ringStyle() {
			return {
				background: `conic-gradient(#1f5fca 0 ${this.safeProgress}%, #e1e3e0 ${this.safeProgress}% 100%)`
			};
		}
	}
};
</script>

<style>
.premium-progress-ring {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 112px;
	height: 112px;
	border-radius: 999px;
	box-shadow: 0 10px 26px rgba(0, 52, 43, 0.16);
}

.premium-progress-ring__inner {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 86px;
	height: 86px;
	flex-direction: column;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.92);
}

.premium-progress-ring__value {
	color: #1f5fca;
	font-size: 24px;
	font-weight: 800;
	line-height: 1;
}

.premium-progress-ring__label {
	margin-top: 6px;
	color: #5a7a99;
	font-size: 10px;
	font-weight: 700;
	letter-spacing: 0.12em;
}
</style>
