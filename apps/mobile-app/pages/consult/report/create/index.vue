<template>
	<view class="consult-create-page">
		<TaskTopBar title="提交问诊报告" :show-right="false" @back="goBack" />

		<view class="hero-card">
			<text class="hero-card__eyebrow">CONSULT WORKFLOW</text>
			<text class="hero-card__title">{{ steps[step - 1] }}</text>
			<text class="hero-card__subtitle">按步骤填写标准化问诊资料，便于医生更快理解你的情况。</text>
		</view>

		<view class="stepper-card">
			<view v-for="(item, index) in steps" :key="item" class="stepper-item">
				<view :class="['stepper-dot', { 'stepper-dot--active': step === index + 1, 'stepper-dot--done': step > index + 1 }]">
					{{ index + 1 }}
				</view>
				<text :class="['stepper-label', { 'stepper-label--active': step === index + 1 || step > index + 1 }]">{{ item }}</text>
			</view>
		</view>

		<BasicInfoForm v-if="step === 1" v-model="form.basicInfo" />

		<view v-if="step === 2">
			<view v-if="canReuseLastDiseases" class="reuse-card">
				<text class="reuse-card__eyebrow">FOLLOW-UP SHORTCUT</text>
				<text class="reuse-card__title">复诊可沿用上次病情选择</text>
				<text class="reuse-card__desc">
					上次报告：{{ lastReport.reportNo }} · {{ formatShortTime(lastReport.createTime || lastReport.submitTime) }}
					你可以直接沿用上次的病种 / 症状，再继续填写主诉。
				</text>
				<button class="reuse-card__button" @tap="reuseLastDiseases">沿用上次选择</button>
			</view>
			<DiseaseTreeSelector v-model="form.diseases" />
		</view>

		<ComplaintForm v-if="step === 3" v-model="form.complaint" />
		<ReportPreview v-if="step === 4" :report="previewReport" />

		<view class="footer-space"></view>
		<view class="bottom-actions">
			<button v-if="step > 1" class="secondary-button" @tap="prevStep">上一步</button>
			<button v-if="step < 4" class="primary-button" @tap="nextStep">下一步</button>
			<button v-else class="primary-button" :disabled="submitting" @tap="submitReport">
				{{ submitting ? '提交中...' : '提交报告' }}
			</button>
		</view>
	</view>
</template>

<script>
import BasicInfoForm from '../../../../components/consult/BasicInfoForm.vue'
import ComplaintForm from '../../../../components/consult/ComplaintForm.vue'
import DiseaseTreeSelector from '../../../../components/consult/DiseaseTreeSelector.vue'
import ReportPreview from '../../../../components/consult/ReportPreview.vue'
import TaskTopBar from '../../../../components/premium/TaskTopBar.vue'
import { getLatestMyConsultReport, getConsultReportDetail, submitConsultReport } from '../../../../common/consultReport.js'
import { saveConsultationReport } from '../../../../utils/consultationStorage.js'
import { getHighestRiskLevel } from '../../../../utils/riskRules.js'

function getStoredUserInfo() {
	try {
		return JSON.parse(uni.getStorageSync('userInfo') || '{}')
	} catch (error) {
		return {}
	}
}

export default {
	components: {
		BasicInfoForm,
		ComplaintForm,
		DiseaseTreeSelector,
		ReportPreview,
		TaskTopBar
	},
	data() {
		const userInfo = getStoredUserInfo()
		return {
			step: 1,
			steps: ['基础信息', '选择病种/症状', '补充说明/主诉', '问诊预览并提交'],
			lastReport: null,
			lastReportDetail: null,
			submitting: false,
			form: {
				basicInfo: {
					name: userInfo.nickName || userInfo.nickname || userInfo.userName || '',
					gender: '',
					age: '',
					chronicDisease: '',
					allergyStatus: '',
					allergyDetail: '',
					pastHistoryStatus: '',
					pastHistoryDetail: '',
					medicationStatus: '',
					medicationDetail: '',
					visitType: ''
				},
				diseases: [],
				complaint: {
					description: '',
					duration: '',
					severity: 0,
					suddenWorse: '',
					lifeImpact: []
				},
				attachments: [],
				diseaseSelectionMode: 'manual',
				diseaseSelectionSource: null
			}
		}
	},
	computed: {
		canReuseLastDiseases() {
			return this.form.basicInfo.visitType === '复诊'
				&& this.lastReport
				&& this.lastReportDetail
				&& Array.isArray(this.lastReportDetail.diseases)
				&& this.lastReportDetail.diseases.length > 0
		},
		previewReport() {
			return {
				...this.form,
				status: '待提交',
				highestRiskLevel: getHighestRiskLevel(this.form.diseases)
			}
		}
	},
	async onLoad() {
		if (!uni.getStorageSync('loginType')) {
			uni.redirectTo({ url: '/pages/Login/Login' })
			return
		}
		await this.loadLastReport()
	},
	methods: {
		goBack() {
			if (this.step > 1) {
				this.prevStep()
				return
			}
			uni.navigateBack({
				delta: 1,
				fail: () => {
					uni.redirectTo({ url: '/pages/consult/report/list/index' })
				}
			})
		},
		async loadLastReport() {
			try {
				this.lastReport = await getLatestMyConsultReport()
				if (this.lastReport && this.lastReport.reportId) {
					this.lastReportDetail = await getConsultReportDetail(this.lastReport.reportId)
				}
			} catch (error) {
				console.warn('[consult-report] load last report failed', error)
			}
		},
		validateBasicInfo() {
			const basic = this.form.basicInfo
			if (!basic.name || !basic.gender || basic.age === '' || !basic.visitType) {
				uni.showToast({ title: '请完善姓名、性别、年龄和就诊类型', icon: 'none' })
				return false
			}
			const age = Number(basic.age)
			if (Number.isNaN(age) || age < 0 || age > 120) {
				uni.showToast({ title: '年龄需填写 0-120 之间的数字', icon: 'none' })
				return false
			}
			if (basic.allergyStatus === '有' && !basic.allergyDetail) {
				uni.showToast({ title: '请填写药物过敏情况', icon: 'none' })
				return false
			}
			if (basic.pastHistoryStatus === '有' && !basic.pastHistoryDetail) {
				uni.showToast({ title: '请填写既往病史', icon: 'none' })
				return false
			}
			if (basic.medicationStatus === '有' && !basic.medicationDetail) {
				uni.showToast({ title: '请填写当前用药', icon: 'none' })
				return false
			}
			return true
		},
		validateCurrentStep() {
			if (this.step === 1) {
				return this.validateBasicInfo()
			}
			if (this.step === 2 && !this.form.diseases.length) {
				uni.showToast({ title: '请至少选择一个病种或症状', icon: 'none' })
				return false
			}
			return true
		},
		nextStep() {
			if (!this.validateCurrentStep()) {
				return
			}
			this.step += 1
		},
		prevStep() {
			if (this.step > 1) {
				this.step -= 1
			}
		},
		submitReport() {
			if (this.submitting) {
				return
			}
			const basicValid = this.validateBasicInfo()
			if (!basicValid || !this.form.diseases.length) {
				this.step = basicValid ? 2 : 1
				if (basicValid) {
					uni.showToast({ title: '请至少选择一个病种或症状', icon: 'none' })
				}
				return
			}
			this.submitRealReport()
		},
		async submitRealReport() {
			this.submitting = true
			uni.showLoading({
				title: '提交中...',
				mask: true
			})
			try {
				const result = await submitConsultReport(this.form)
				const localReport = saveConsultationReport({
					...this.form,
					id: String(result.reportId),
					reportId: result.reportId,
					reportNo: result.reportNo,
					status: result.status,
					highestRiskLevel: getHighestRiskLevel(this.form.diseases)
				})
				uni.hideLoading()
				uni.showToast({ title: '提交成功', icon: 'success' })
				setTimeout(() => {
					uni.redirectTo({
						url: `/pages/consult/report/detail/index?id=${result.reportId || localReport.id}`
					})
				}, 500)
			} catch (error) {
				uni.hideLoading()
				uni.showToast({
					title: error.message || '提交失败，请稍后重试',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		},
		reuseLastDiseases() {
			if (!this.canReuseLastDiseases) {
				return
			}
			this.form.diseases = (this.lastReportDetail.diseases || []).map((item) => ({ ...item }))
			this.form.diseaseSelectionMode = 'reused_previous'
			this.form.diseaseSelectionSource = {
				reportId: this.lastReport.reportId || this.lastReport.id,
				reportNo: this.lastReport.reportNo,
				submitTime: this.lastReport.createTime || this.lastReport.submitTime
			}
			uni.showToast({
				title: '已沿用上次病情选择',
				icon: 'success'
			})
		},
		formatShortTime(value) {
			if (!value) {
				return '-'
			}
			const date = new Date(value)
			const pad = (num) => String(num).padStart(2, '0')
			return `${date.getMonth() + 1}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
		}
	}
}
</script>

<style scoped>
.consult-create-page {
	min-height: 100vh;
	padding: 18px 18px 118px;
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

.stepper-card {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 10px;
	margin: 16px 0;
	padding: 16px 14px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.stepper-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	text-align: center;
}

.stepper-dot {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 34px;
	height: 34px;
	border-radius: 999px;
	background: #a3c4e8;
	color: #7a8fa8;
	font-size: 13px;
	font-weight: 800;
}

.stepper-dot--active,
.stepper-dot--done {
	background: #1f5fca;
	color: #ffffff;
}

.stepper-label {
	margin-top: 8px;
	color: #7a8fa8;
	font-size: 11px;
	line-height: 1.45;
}

.stepper-label--active {
	color: #1f5fca;
	font-weight: 700;
}

.reuse-card {
	margin-bottom: 14px;
	padding: 20px 18px;
	border-radius: 26px;
	background: linear-gradient(180deg, #f0f5ff 0%, #ffffff 100%);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.reuse-card__eyebrow {
	display: block;
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.reuse-card__title {
	display: block;
	margin-top: 8px;
	color: #103268;
	font-size: 20px;
	font-weight: 800;
}

.reuse-card__desc {
	display: block;
	margin-top: 10px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.7;
}

.reuse-card__button {
	margin-top: 14px;
	border-radius: 999px;
	background: #1f5fca;
	color: #ffffff;
	font-size: 14px;
	font-weight: 700;
}

.footer-space {
	height: 96px;
}

.bottom-actions {
	position: fixed;
	right: 16px;
	bottom: calc(14px + env(safe-area-inset-bottom));
	left: 16px;
	z-index: 40;
	display: flex;
	gap: 12px;
	padding: 10px;
	border: 1px solid rgba(255, 255, 255, 0.88);
	border-radius: 999px;
	background: rgba(240, 245, 255, 0.92);
	box-shadow: 0 16px 40px rgba(31, 95, 202, 0.14);
	backdrop-filter: blur(18px);
}

.primary-button,
.secondary-button {
	flex: 1;
	height: 46px;
	line-height: 46px;
	margin: 0;
	border-radius: 999px;
	font-size: 16px;
	font-weight: 800;
}

.primary-button {
	background: #1f5fca;
	color: #ffffff;
}

.secondary-button {
	background: #a3c4e8;
	color: #1f5fca;
}
</style>
