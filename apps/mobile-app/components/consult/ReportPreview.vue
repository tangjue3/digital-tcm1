<template>
	<view class="report">
		<view class="hero-card">
			<text class="hero-card__eyebrow">CONSULT REPORT</text>
			<text class="hero-card__title">问诊报告</text>
			<text v-if="report.reportNo" class="hero-card__subtitle">报告编号：{{ report.reportNo }}</text>
		</view>

		<view class="section-card">
			<text class="section-title">一、基础信息</text>
			<view class="info-row"><text class="info-label">姓名</text><text class="info-value">{{ basic.name || '未填写' }}</text></view>
			<view class="info-row"><text class="info-label">性别</text><text class="info-value">{{ basic.gender || '未填写' }}</text></view>
			<view class="info-row"><text class="info-label">年龄</text><text class="info-value">{{ formatAge(basic.age) }}</text></view>
			<view class="info-row"><text class="info-label">就诊类型</text><text class="info-value">{{ basic.visitType || '未填写' }}</text></view>
			<view class="info-row"><text class="info-label">基础病</text><text class="info-value">{{ basic.chronicDisease || '无/ 未填写' }}</text></view>
			<view class="info-row"><text class="info-label">药物过敏史</text><text class="info-value">{{ formatConditional(basic.allergyStatus, basic.allergyDetail) }}</text></view>
			<view class="info-row"><text class="info-label">既往病史</text><text class="info-value">{{ formatConditional(basic.pastHistoryStatus, basic.pastHistoryDetail) }}</text></view>
			<view class="info-row"><text class="info-label">当前用药</text><text class="info-value">{{ formatConditional(basic.medicationStatus, basic.medicationDetail) }}</text></view>
		</view>

		<view class="section-card">
			<text class="section-title">二、病种/ 症状选择</text>
			<view v-if="diseases.length">
				<view class="info-row" v-if="selectionModeText">
					<text class="info-label">选择方式</text>
					<text class="info-value">{{ selectionModeText }}</text>
				</view>
				<view class="info-row" v-if="selectionSourceText">
					<text class="info-label">沿用来源</text>
					<text class="info-value">{{ selectionSourceText }}</text>
				</view>
				<view class="disease-card" v-for="(item, index) in diseases" :key="item.code || index">
					<view class="disease-head">
						<text class="disease-name">{{ index + 1 }}. {{ item.displayName || item.name }}</text>
						<RiskTag :level="item.riskLevel" />
					</view>
					<view class="info-row"><text class="info-label">路径</text><text class="info-value">{{ formatPath(item.path) }}</text></view>
					<view class="info-row"><text class="info-label">标准编码</text><text class="info-value">{{ item.code || '未填写' }}</text></view>
					<view class="info-row" v-if="item.riskLevel && item.riskLevel !== 'normal'"><text class="info-label">风险提示</text><text class="info-value">{{ getRiskLabel(item.riskLevel) }}</text></view>
					<view class="info-row" v-if="item.riskReason"><text class="info-label">提示说明</text><text class="info-value">{{ item.riskReason }}</text></view>
				</view>
			</view>
			<view v-else class="empty-text">未选择病种或症状</view>
		</view>

		<view class="section-card">
			<text class="section-title">三、补充说明</text>
			<view class="info-row"><text class="info-label">主要不适</text><text class="info-value">{{ complaint.description || '未填写' }}</text></view>
			<view class="info-row"><text class="info-label">出现时间</text><text class="info-value">{{ complaint.duration || '未填写' }}</text></view>
			<view class="info-row"><text class="info-label">严重程度</text><text class="info-value">{{ formatSeverity(complaint.severity) }}</text></view>
			<view class="info-row"><text class="info-label">突然加重</text><text class="info-value">{{ complaint.suddenWorse || '未填写' }}</text></view>
			<view class="info-row"><text class="info-label">影响生活</text><text class="info-value">{{ formatArray(complaint.lifeImpact) }}</text></view>
		</view>

		<view v-if="attachments.length" class="section-card">
			<text class="section-title">四、辅助资料</text>
			<view class="attachment-card" v-for="(item, index) in attachments" :key="item.id || `${item.type}-${index}`">
				<text class="attachment-name">{{ index + 1 }}. {{ item.label || '附件' }}</text>
				<text class="attachment-meta">{{ item.name || item.path || '-' }}</text>
				<text class="attachment-meta">{{ item.path || '-' }}</text>
			</view>
		</view>

		<view class="section-card">
			<text class="section-title">{{ attachments.length ? '五、提交信息' : '四、提交信息' }}</text>
			<view class="info-row"><text class="info-label">提交时间</text><text class="info-value">{{ formatTime(report.submitTime) }}</text></view>
			<view class="info-row"><text class="info-label">报告状态</text><text class="info-value">{{ statusLabel(report.status || previewStatus) }}</text></view>
			<view class="info-row" v-if="highestRiskLabel"><text class="info-label">最高风险</text><text class="info-value">{{ highestRiskLabel }}</text></view>
		</view>
	</view>
</template>

<script>
import RiskTag from './RiskTag.vue'
import { getRiskLabel } from '../../utils/riskRules.js'

export default {
	name: 'ReportPreview',
	components: { RiskTag },
	props: {
		report: {
			type: Object,
			default: () => ({})
		},
		previewStatus: {
			type: String,
			default: '待提交'
		}
	},
	computed: {
		basic() {
			return this.report.basicInfo || {}
		},
		diseases() {
			return this.report.diseases || []
		},
		attachments() {
			return this.report.attachments || []
		},
		complaint() {
			return this.report.complaint || {}
		},
		highestRiskLabel() {
			return this.report.highestRiskLevel && this.report.highestRiskLevel !== 'normal'
				? getRiskLabel(this.report.highestRiskLevel)
				: ''
		},
		selectionModeText() {
			if (this.report.diseaseSelectionMode === 'reused_previous') {
				return '复诊沿用上次病情选择'
			}
			return this.diseases.length ? '本次手动选择' : ''
		},
		selectionSourceText() {
			const source = this.report.diseaseSelectionSource
			if (!source || !source.reportNo) {
				return ''
			}
			return `${source.reportNo}（${this.formatTime(source.submitTime)}）`
		}
	},
	methods: {
		getRiskLabel,
		formatAge(age) {
			return age === 0 || age === '0' || age ? `${age} 岁` : '未填写'
		},
		formatConditional(status, detail) {
			if (!status) {
				return '未填写'
			}
			return status === '有' && detail ? `有，${detail}` : status
		},
		formatPath(path = []) {
			return Array.isArray(path) && path.length ? path.join(' > ') : '未填写'
		},
		formatArray(value) {
			return Array.isArray(value) && value.length ? value.join('、') : '未填写'
		},
		formatSeverity(value) {
			return value || value === 0 ? `${value} 分` : '未填写'
		},
		statusLabel(status) {
			const map = {
				draft: '草稿',
				submitted: '已提交',
				pending: '待处理',
				processing: '处理中',
				supplement_required: '需补充',
				completed: '已完成',
				rejected: '已驳回',
				待提交: '待提交'
			}
			return map[status] || status || '待提交'
		},
		formatTime(value) {
			if (!value) {
				return '待提交'
			}
			const date = new Date(value)
			if (Number.isNaN(date.getTime())) {
				return value
			}
			const pad = (num) => String(num).padStart(2, '0')
			return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
		}
	}
}
</script>

<style scoped>
.report {
	padding-bottom: 4px;
}

.hero-card {
	padding: 24px 22px;
	border-radius: 30px;
	background: linear-gradient(135deg, #1f5fca 0%, #3f8cff 100%);
	box-shadow: 0 20px 42px rgba(31, 95, 202, 0.18);
	color: #ffffff;
}

.hero-card__eyebrow {
	display: block;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
	color: rgba(255, 255, 255, 0.8);
}

.hero-card__title {
	display: block;
	margin-top: 12px;
	font-size: 30px;
	font-weight: 800;
	line-height: 1.15;
}

.hero-card__subtitle {
	display: block;
	margin-top: 12px;
	font-size: 13px;
	line-height: 1.65;
	color: rgba(255, 255, 255, 0.88);
}

.section-card {
	margin-top: 16px;
	padding: 20px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.section-title {
	display: block;
	margin-bottom: 14px;
	color: #1f5fca;
	font-size: 18px;
	font-weight: 800;
}

.info-row {
	display: flex;
	align-items: flex-start;
	gap: 12px;
	margin-bottom: 12px;
	line-height: 1.65;
}

.info-row:last-child {
	margin-bottom: 0;
}

.info-label {
	width: 84px;
	flex-shrink: 0;
	color: #7a8fa8;
	font-size: 13px;
	font-weight: 700;
}

.info-value {
	flex: 1;
	color: #103268;
	font-size: 14px;
	word-break: break-word;
}

.disease-card,
.attachment-card {
	margin-top: 12px;
	padding: 14px;
	border-radius: 20px;
	background: #f0f5ff;
}

.disease-head {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	margin-bottom: 10px;
}

.disease-name {
	flex: 1;
	color: #103268;
	font-size: 15px;
	font-weight: 800;
}

.attachment-name {
	display: block;
	color: #103268;
	font-size: 14px;
	font-weight: 800;
}

.attachment-meta {
	display: block;
	margin-top: 6px;
	color: #7a8fa8;
	font-size: 12px;
	line-height: 1.55;
	word-break: break-all;
}

.empty-text {
	color: #98a2b3;
	font-size: 14px;
}
</style>
