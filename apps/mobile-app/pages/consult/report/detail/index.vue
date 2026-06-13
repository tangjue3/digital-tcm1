<template>
	<view class="consult-detail-page">
		<TaskTopBar title="报告详情" :show-right="false" @back="backToList" />

		<view v-if="loading" class="state-card">
			<text class="state-card__title">正在加载报告详情...</text>
			<text class="state-card__desc">请稍候，我们正在同步最新问诊报告。</text>
		</view>

		<ReportPreview v-else-if="report" :report="report" />

		<view v-else class="state-card">
			<text class="state-card__title">未找到报告</text>
			<text class="state-card__desc">{{ errorMessage || '报告不存在，或当前账号暂时无查看权限。' }}</text>
			<button class="state-card__button" @tap="backToList">返回我的报告</button>
		</view>
	</view>
</template>

<script>
import ReportPreview from '../../../../components/consult/ReportPreview.vue'
import TaskTopBar from '../../../../components/premium/TaskTopBar.vue'
import { getConsultReportDetail } from '../../../../common/consultReport.js'

export default {
	components: {
		ReportPreview,
		TaskTopBar
	},
	data() {
		return {
			loading: true,
			report: null,
			errorMessage: '',
			reportId: ''
		}
	},
	async onLoad(options = {}) {
		if (!uni.getStorageSync('loginType')) {
			uni.redirectTo({ url: '/pages/Login/Login' })
			return
		}
		this.reportId = options.id || ''
		await this.loadReport()
	},
	methods: {
		async loadReport() {
			if (!this.reportId) {
				this.loading = false
				this.errorMessage = '缺少报告编号参数'
				return
			}
			this.loading = true
			this.errorMessage = ''
			try {
				this.report = await getConsultReportDetail(this.reportId)
			} catch (error) {
				this.report = null
				this.errorMessage = error.message || '获取报告详情失败，请稍后重试';
			} finally {
				this.loading = false
			}
		},
		backToList() {
			uni.redirectTo({ url: '/pages/consult/report/list/index' })
		}
	}
}
</script>

<style scoped>
.consult-detail-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background-color: #f8faf7;
	background-image:
		radial-gradient(circle at 0% 0%, rgba(208, 231, 234, 0.48) 0, transparent 40%),
		radial-gradient(circle at 100% 48%, rgba(148, 211, 193, 0.18) 0, transparent 42%);
	box-sizing: border-box;
}

.state-card {
	margin-top: 84px;
	padding: 38px 24px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.86);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.06);
	text-align: center;
}

.state-card__title {
	display: block;
	color: #103268;
	font-size: 22px;
	font-weight: 800;
}

.state-card__desc {
	display: block;
	margin-top: 12px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.7;
}

.state-card__button {
	width: 164px;
	height: 44px;
	line-height: 44px;
	margin-top: 20px;
	border-radius: 999px;
	background: #1f5fca;
	color: #ffffff;
	font-size: 14px;
	font-weight: 800;
}
</style>
