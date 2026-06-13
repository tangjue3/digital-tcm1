<template>
	<view class="consult-list-page">
		<TaskTopBar title="我的报告" :show-right="false" @back="goBack" />

		<view class="hero-card">
			<text class="hero-card__eyebrow">REPORT CENTER</text>
			<text class="hero-card__title">我的问诊报告</text>
			<text class="hero-card__subtitle">已提交报告会从后端接口实时读取，你可以在这里查看处理进度和详情查看</text>
		</view>

		<view class="toolbar">
			<view>
				<text class="toolbar__title">报告列表</text>
				<text class="toolbar__subtitle">{{ reports.length ? `${reports.length} 份记录` : '还没有提交过报告' }}</text>
			</view>
			<button class="toolbar__button" @tap="createReport">新增</button>
		</view>

		<view v-if="reports.length" class="list">
			<view class="report-card" v-for="report in reports" :key="report.reportId || report.id" @tap="openDetail(report.reportId || report.id)">
				<view class="report-card__top">
					<text class="report-card__no">{{ report.reportNo }}</text>
					<RiskTag :level="report.highestRiskLevel" :show-normal="true" />
				</view>
				<text class="report-card__summary">{{ summaryOf(report) }}</text>
				<view class="report-card__meta">
					<text>{{ formatTime(report.createTime) }}</text>
					<text>{{ report.patientName || '-' }} · {{ report.age || '-' }}岁 · {{ report.gender || '-' }}</text>
				</view>
				<view class="report-card__meta">
					<text>{{ statusLabel(report.status) }}</text>
					<text>{{ riskLabel(report.highestRiskLevel) }}</text>
				</view>
			</view>
		</view>

		<view v-else class="empty-card">
			<text class="empty-card__title">还没有问诊报告</text>
			<text class="empty-card__desc">可以先提交一份标准化问诊资料，稍后在这里查看详情查看</text>
			<button class="empty-card__button" @tap="createReport">提交问诊报告</button>
		</view>
	</view>
</template>

<script>
import RiskTag from '../../../../components/consult/RiskTag.vue'
import TaskTopBar from '../../../../components/premium/TaskTopBar.vue'
import { getMyConsultReports } from '../../../../common/consultReport.js'
import { getRiskLabel } from '../../../../utils/riskRules.js'

export default {
	components: {
		RiskTag,
		TaskTopBar
	},
	data() {
		return {
			reports: []
		}
	},
	async onShow() {
		if (!uni.getStorageSync('loginType')) {
			uni.redirectTo({ url: '/pages/Login/Login' })
			return
		}
		await this.loadReports()
	},
	methods: {
		goBack() {
			uni.navigateBack({
				delta: 1,
				fail: () => {
					uni.switchTab({ url: '/pages/health-plan/health-plan' })
				}
			})
		},
		async loadReports() {
			try {
				const result = await getMyConsultReports({
					pageNum: 1,
					pageSize: 50
				})
				this.reports = result.rows || []
			} catch (error) {
				uni.showToast({
					title: error.message || '获取我的报告失败',
					icon: 'none'
				})
			}
		},
		createReport() {
			uni.navigateTo({ url: '/pages/consult/report/create/index' })
		},
		openDetail(id) {
			uni.navigateTo({ url: `/pages/consult/report/detail/index?id=${id}` })
		},
		summaryOf(report) {
			const content = report.chiefComplaint || report.diseaseSummary || ''
			if (!content) {
				return '暂无补充说明'
			}
			return content.length > 42 ? `${content.slice(0, 42)}...` : content
		},
		statusLabel(status) {
			const map = {
				draft: '草稿',
				submitted: '已提交',
				pending: '待处理',
				processing: '处理中',
				supplement_required: '需补充',
				completed: '已完成',
				rejected: '已驳回'
			}
			return map[status] || '待处理'
		},
		riskLabel(level) {
			return getRiskLabel(level || 'normal')
		},
		formatTime(value) {
			if (!value) return '-'
			const date = new Date(value)
			const pad = (num) => String(num).padStart(2, '0')
			return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
		}
	}
}
</script>

<style scoped>
.consult-list-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background-color: #f8faf7;
	background-image:
		radial-gradient(circle at 0% 0%, rgba(208, 231, 234, 0.48) 0, transparent 40%),
		radial-gradient(circle at 100% 48%, rgba(148, 211, 193, 0.18) 0, transparent 42%);
	box-sizing: border-box;
}

.hero-card {
	padding: 24px 22px;
	border-radius: 30px;
	background: linear-gradient(135deg, #1f5fca 0%, #1f5fca 100%);
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
	font-size: 14px;
	line-height: 1.75;
	color: rgba(255, 255, 255, 0.88);
}

.toolbar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	margin: 18px 2px 14px;
}

.toolbar__title {
	display: block;
	color: #1f5fca;
	font-size: 24px;
	font-weight: 800;
}

.toolbar__subtitle {
	display: block;
	margin-top: 6px;
	color: #4d6265;
	font-size: 12px;
	font-weight: 700;
}

.toolbar__button {
	width: 76px;
	height: 40px;
	line-height: 40px;
	margin: 0;
	border-radius: 999px;
	background: #1f5fca;
	color: #ffffff;
	font-size: 14px;
	font-weight: 800;
}

.list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.report-card {
	padding: 18px 16px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.86);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.06);
}

.report-card__top,
.report-card__meta {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 10px;
}

.report-card__no {
	color: #103268;
	font-size: 16px;
	font-weight: 800;
}

.report-card__summary {
	display: block;
	margin: 12px 0 8px;
	color: #344054;
	font-size: 14px;
	line-height: 1.65;
}

.report-card__meta {
	margin-top: 8px;
	color: #7a8fa8;
	font-size: 12px;
}

.empty-card {
	margin-top: 42px;
	padding: 38px 24px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.86);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.06);
	text-align: center;
}

.empty-card__title {
	color: #103268;
	font-size: 22px;
	font-weight: 800;
}

.empty-card__desc {
	display: block;
	margin: 12px 0 20px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.7;
}

.empty-card__button {
	width: 164px;
	height: 44px;
	line-height: 44px;
	margin: 0 auto;
	border-radius: 999px;
	background: #1f5fca;
	color: #ffffff;
	font-size: 14px;
	font-weight: 800;
}
</style>
