<template>
	<view class="health-plan-container">
		<PremiumTopBar title="云枢四诊" />

		<DatePillSelector :items="datePills" :value="selectedDate" @change="selectDate" />

		<PlanProgressPanel
			title="今日健康计划"
			:description="planDescription"
			:badge="planBadge"
			:progress="dayProgressPercentage"
		/>

		<view class="pulse-divider">
			<view class="pulse-divider__line"></view>
			<view class="pulse-divider__dots">
				<view></view>
				<view></view>
				<view></view>
			</view>
			<view class="pulse-divider__line"></view>
		</view>

		<view class="consult-section">
			<ConsultActionCard
				title="提交问诊报告"
				subtitle="填写症状与资料，生成标准化问诊报告"
				marker="问"
				@tap="goConsultReportCreate"
			/>
			<ConsultActionCard
				title="我的报告"
				subtitle="查看已提交的问诊记录"
				marker="记"
				@tap="goConsultReportList"
			/>
		</view>

		<view class="plan-section">
			<view class="section-heading">
				<view>
					<text class="section-heading__eyebrow">DIGITAL PRESCRIPTION</text>
					<text class="section-heading__title">执行处方</text>
				</view>
				<text class="section-heading__date">{{ selectedDate }}</text>
			</view>

			<view class="task-list" v-if="selectedPlans.length">
				<PrescriptionTaskCard
					v-for="(plan, index) in selectedPlans"
					:key="index"
					:title="getTaskTitle(plan, index)"
					:content="plan.content"
					:icon-text="getTaskIcon(index)"
					:badge="getTaskBadge(index)"
					:completed="plan.completed"
					@toggle="togglePlanCompletion(index)"
				/>
			</view>

			<view v-else class="no-plans">
				<text class="no-plans__title">暂无计划，请先进行健康检测</text>
				<text class="no-plans__desc">完成一次体质检测后，系统会根据结果为你生成连续 7 天的调理建议。</text>
			</view>
		</view>

		<PremiumTabBar active="health-plan" />
	</view>
</template>

<script>
import ConsultActionCard from '../../components/premium/ConsultActionCard.vue';
import DatePillSelector from '../../components/premium/DatePillSelector.vue';
import PlanProgressPanel from '../../components/premium/PlanProgressPanel.vue';
import PremiumTabBar from '../../components/premium/PremiumTabBar.vue';
import PremiumTopBar from '../../components/premium/PremiumTopBar.vue';
import PrescriptionTaskCard from '../../components/premium/PrescriptionTaskCard.vue';

const defaultAdvices = {
	food: [
		{ title: '宜食补气食物', advice: '宜食用具有补气作用的食物，如山药、莲子、黄芪、党参、白扁豆、鸡肉、牛肉等。' },
		{ title: '忌食生冷', advice: '忌食生冷寒凉食物，如冷饮、生冷瓜果等，以免损伤脾胃之气。' }
	],
	music: [
		{ title: '舒缓音乐', advice: '可聆听舒缓的古典音乐，如《高山流水》《春江花月夜》等，有助于调节情志。' }
	],
	sport: [
		{ title: '适度运动', advice: '适合进行柔和的运动，如太极拳、八段锦、散步等，避免剧烈运动导致大汗淋漓。' }
	],
	sleep: [
		{ title: '规律作息', advice: '保持规律作息，避免熬夜，保证充足睡眠，有助于恢复元气。' }
	],
	treatment: [
		{ title: '穴位按摩', advice: '可按摩足三里、气海、关元等穴位，每次 10-15 分钟，每日 1-2 次，有助于补益元气。' }
	]
};

const taskMeta = [
	{ title: '饮食调理', icon: '食', badge: '必做' },
	{ title: '五音疗法', icon: '乐', badge: '必做' },
	{ title: '适度运动', icon: '动', badge: '必做' },
	{ title: '规律作息', icon: '息', badge: '建议' },
	{ title: '脉诊监测', icon: '脉', badge: '必做' }
];

export default {
	components: {
		ConsultActionCard,
		DatePillSelector,
		PlanProgressPanel,
		PremiumTabBar,
		PremiumTopBar,
		PrescriptionTaskCard
	},
	data() {
		return {
			dateList: this.generateDateList(),
			selectedDate: this.getTodayDate(),
			dailyPlans: {},
			userdata: {}
		};
	},
	computed: {
		datePills() {
			return this.dateList.map(date => ({
				value: date,
				week: this.getDateWeek(date),
				day: date.slice(8)
			}));
		},
		selectedPlans() {
			return this.dailyPlans[this.selectedDate] || [];
		},
		dayProgressPercentage() {
			if (!this.selectedPlans.length) return 0;
			const completed = this.selectedPlans.filter(plan => plan.completed).length;
			return Math.round((completed / this.selectedPlans.length) * 100);
		},
		planBadge() {
			if (this.userdata && this.userdata.physique_name) {
				return `${this.userdata.physique_name}调理`;
			}
			return '调理建议';
		},
		planDescription() {
			if (this.userdata && this.userdata.typical_symptom) {
				return '顺应体质记录，按日推进饮食、作息、运动与调理任务。';
			}
			return '完成检测后，这里会根据你的体质结果生成每日调理处方。';
		}
	},
	onShow() {
		this.getdailyPlans();
	},
	onHide() {
		uni.setStorageSync('hishory', JSON.stringify(this.dailyPlans));
	},
	onLoad() {
		if (!uni.getStorageSync('loginType')) {
			uni.redirectTo({
				url: '/pages/Login/Login'
			});
		}
	},
	methods: {
		remo() {
			uni.removeStorageSync('hishory');
		},
		getAdvice(advices, category, index = 0) {
			try {
				if (advices && advices[category] && advices[category][index]) {
					return advices[category][index].advice;
				}
				return defaultAdvices[category][index]?.advice || '暂无建议';
			} catch (e) {
				return defaultAdvices[category][index]?.advice || '暂无建议';
			}
		},
		getdailyPlans() {
			try {
				const faceData = uni.getStorageSync('faceData');
				if (faceData) {
					this.userdata = JSON.parse(faceData);
				}
			} catch (e) {
				console.error('解析用户数据失败', e);
			}

			const hishory = uni.getStorageSync('hishory');
			if (hishory && hishory !== '') {
				try {
					this.dailyPlans = JSON.parse(hishory);
				} catch (e) {
					console.error('解析历史数据失败', e);
					this.generateDefaultPlans();
				}
			} else {
				this.generateDefaultPlans();
			}
		},
		generateDefaultPlans() {
			const list = this.generateDateList();
			const plans = {};
			const advices = this.userdata.advices || defaultAdvices;

			list.forEach((item) => {
				plans[item] = [
					{ type: '饮食', content: this.getAdvice(advices, 'food', 0), completed: false },
					{ type: '音乐', content: this.getAdvice(advices, 'music', 0), completed: false },
					{ type: '运动', content: this.getAdvice(advices, 'sport', 0), completed: false },
					{ type: '作息', content: this.getAdvice(advices, 'sleep', 0), completed: false },
					{ type: '按摩', content: this.getAdvice(advices, 'treatment', 0), completed: false }
				];
			});

			this.dailyPlans = plans;
		},
		generateDateList() {
			const dates = [];
			const today = new Date();
			for (let i = 0; i < 7; i++) {
				const date = new Date(today);
				date.setDate(today.getDate() + i);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				dates.push(`${year}-${month}-${day}`);
			}
			return dates;
		},
		getTodayDate() {
			const today = new Date();
			const year = today.getFullYear();
			const month = String(today.getMonth() + 1).padStart(2, '0');
			const day = String(today.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		getDateWeek(date) {
			const weeks = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
			const target = new Date(date.replace(/-/g, '/'));
			return date === this.getTodayDate() ? '今日' : weeks[target.getDay()];
		},
		selectDate(date) {
			this.selectedDate = date;
		},
		getTaskMeta(index) {
			return taskMeta[index] || { title: '调理任务', icon: '养', badge: '建议' };
		},
		getTaskTitle(plan, index) {
			const meta = this.getTaskMeta(index);
			return plan.type || meta.title;
		},
		getTaskIcon(index) {
			return this.getTaskMeta(index).icon;
		},
		getTaskBadge(index) {
			return this.getTaskMeta(index).badge;
		},
		togglePlanCompletion(index) {
			const plans = this.dailyPlans[this.selectedDate];
			if (plans && plans[index]) {
				plans[index].completed = !plans[index].completed;
			}
		},
		goConsultReportCreate() {
			uni.navigateTo({
				url: '/pages/consult/report/create/index'
			});
		},
		goConsultReportList() {
			uni.navigateTo({
				url: '/pages/consult/report/list/index'
			});
		}
	}
};
</script>

<style>
.health-plan-container {
	min-height: 100vh;
	padding: 18px 18px 116px;
	background-color: #f5f8ff;
	background-image:
		radial-gradient(circle at 6% 0%, rgba(59, 140, 255, 0.25) 0, transparent 36%),
		radial-gradient(circle at 100% 30%, rgba(31, 95, 202, 0.15) 0, transparent 34%);
	box-sizing: border-box;
}

.pulse-divider {
	display: flex;
	align-items: center;
	margin: 16px 4px 18px;
	opacity: 0.58;
}

.pulse-divider__line {
	height: 1px;
	flex: 1;
	background: #a3c4e8;
}

.pulse-divider__dots {
	display: flex;
	align-items: center;
	gap: 5px;
	padding: 0 14px;
}

.pulse-divider__dots view {
	width: 6px;
	height: 6px;
	border-radius: 999px;
	background: rgba(31, 95, 202, 0.48);
}

.pulse-divider__dots view:nth-child(2) {
	width: 9px;
	height: 9px;
	background: rgba(31, 95, 202, 0.76);
}

.consult-section {
	display: grid;
	grid-template-columns: 1fr;
	gap: 14px;
	margin-bottom: 18px;
}

.plan-section {
	padding-bottom: 12px;
}

.section-heading {
	display: flex;
	align-items: flex-end;
	justify-content: space-between;
	margin-bottom: 14px;
	padding: 0 2px;
}

.section-heading__eyebrow {
	display: block;
	margin-bottom: 6px;
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.14em;
}

.section-heading__title {
	display: block;
	color: #103268;
	font-size: 26px;
	font-weight: 800;
}

.section-heading__date {
	color: #7a8fa8;
	font-size: 12px;
	font-weight: 600;
}

.task-list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.no-plans {
	padding: 28px 16px;
	border-radius: 22px;
	background: rgba(255, 255, 255, 0.74);
	text-align: center;
}

.no-plans__title {
	color: #1f5fca;
	font-size: 16px;
	font-weight: 800;
}

.no-plans__desc {
	display: block;
	margin-top: 8px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.6;
}
</style>
