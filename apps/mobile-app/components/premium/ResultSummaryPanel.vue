<template>
	<view class="summary-panel">
		<view class="summary-panel__watermark">
			<text class="summary-panel__watermark-text">TCM</text>
		</view>
		<view class="summary-panel__head">
			<view class="summary-panel__title-wrap">
				<text class="summary-panel__title">当前结果</text>
				<text class="summary-panel__subtitle">RESULT SUMMARY</text>
			</view>
		</view>

		<view v-if="physiqueName" class="summary-panel__body">
			<view class="summary-panel__section">
				<view class="summary-panel__stripe"></view>
				<view class="summary-panel__main">
					<text class="summary-panel__label">主导偏颇体质</text>
					<text class="summary-panel__value">{{ physiqueName }}</text>
				</view>
				<view class="summary-panel__badge">{{ tendencyLabel }}</view>
			</view>

			<view v-if="symptoms.length" class="summary-panel__chips">
				<text v-for="(item, index) in symptoms" :key="index" class="summary-panel__chip">{{ item }}</text>
			</view>

			<view class="summary-panel__metric">
				<view class="summary-panel__metric-head">
					<text class="summary-panel__metric-label">结果完整度</text>
					<text class="summary-panel__metric-value">{{ score }}%</text>
				</view>
				<view class="summary-panel__bar">
					<view class="summary-panel__bar-fill" :style="{ width: `${score}%` }"></view>
				</view>
			</view>
		</view>

		<view v-else class="summary-panel__empty">
			<text class="summary-panel__empty-title">暂无检测结果</text>
			<text class="summary-panel__empty-desc">完成面诊、舌诊、脉诊或声纹检测后，这里会自动同步你的体质摘要。</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'ResultSummaryPanel',
	props: {
		physiqueName: {
			type: String,
			default: ''
		},
		symptoms: {
			type: Array,
			default: () => []
		},
		score: {
			type: Number,
			default: 0
		},
		tendencyLabel: {
			type: String,
			default: ''
		}
	}
};
</script>

<style>
.summary-panel {
	position: relative;
	padding: 22px 20px;
	overflow: hidden;
	border: 1px solid rgba(255, 255, 255, 0.84);
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.7);
	box-shadow: 0 16px 34px rgba(31, 95, 202, 0.08);
	backdrop-filter: blur(18px);
}

.summary-panel__watermark {
	position: absolute;
	top: 10px;
	right: 6px;
}

.summary-panel__watermark-text {
	color: rgba(31, 95, 202, 0.08);
	font-size: 52px;
	font-weight: 900;
	letter-spacing: 0.04em;
}

.summary-panel__head {
	position: relative;
	z-index: 1;
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 16px;
}

.summary-panel__title {
	color: #1f5fca;
	font-size: 26px;
	font-weight: 800;
	line-height: 1.2;
}

.summary-panel__subtitle {
	margin-top: 6px;
	color: #4d6265;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.summary-panel__title-wrap {
	display: flex;
	flex-direction: column;
}

.summary-panel__body,
.summary-panel__empty {
	position: relative;
	z-index: 1;
}

.summary-panel__section {
	display: flex;
	align-items: center;
	padding: 16px;
	border-radius: 22px;
	background: rgba(240, 245, 255, 0.88);
}

.summary-panel__stripe {
	width: 8px;
	height: 48px;
	margin-right: 12px;
	border-radius: 999px;
	background: #d0e7ea;
}

.summary-panel__main {
	flex: 1;
	min-width: 0;
}

.summary-panel__label {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 700;
	letter-spacing: 0.08em;
}

.summary-panel__value {
	display: block;
	margin-top: 4px;
	color: #1f5fca;
	font-size: 22px;
	font-weight: 800;
}

.summary-panel__badge {
	padding: 7px 12px;
	border: 1px solid rgba(31, 95, 202, 0.12);
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.52);
	color: #065043;
	font-size: 11px;
	font-weight: 700;
}

.summary-panel__chips {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	margin-top: 14px;
}

.summary-panel__chip {
	padding: 7px 12px;
	border-radius: 999px;
	background: rgba(224, 227, 229, 0.75);
	color: #3f4945;
	font-size: 12px;
	font-weight: 700;
}

.summary-panel__metric {
	margin-top: 16px;
	padding: 16px;
	border-radius: 22px;
	background: rgba(248, 250, 247, 0.9);
}

.summary-panel__metric-head {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 10px;
}

.summary-panel__metric-label {
	color: #103268;
	font-size: 14px;
}

.summary-panel__metric-value {
	color: #1f5fca;
	font-size: 24px;
	font-weight: 800;
}

.summary-panel__bar {
	height: 8px;
	border-radius: 999px;
	background: #e1e3e0;
	overflow: hidden;
}

.summary-panel__bar-fill {
	height: 100%;
	border-radius: 999px;
	background: linear-gradient(90deg, #1f5fca 0%, #29695b 100%);
}

.summary-panel__empty {
	padding: 18px;
	border-radius: 22px;
	background: rgba(240, 245, 255, 0.86);
}

.summary-panel__empty-title {
	color: #1f5fca;
	font-size: 16px;
	font-weight: 800;
}

.summary-panel__empty-desc {
	display: block;
	margin-top: 8px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.6;
}
</style>
