<template>
	<view class="result-page">
		<TaskTopBar :title="reportTitle" :show-right="false" @back="goBack" />

		<view v-if="loading" class="result-page__loading">
			<text class="result-page__loading-text">加载中...</text>
		</view>

		<template v-else-if="res && res.physique_name">
			<view class="result-page__hero">
				<text class="result-page__eyebrow">PHYSIQUE REPORT</text>
				<text class="result-page__hero-title">{{ res.physique_name }}</text>
				<text class="result-page__hero-subtitle">{{ res.risk_warning || '暂无风险提示' }}</text>
			</view>

			<view class="result-page__card">
				<text class="result-page__card-title">具体描述</text>
				<text class="result-page__paragraph">{{ res.risk_warning || '暂无描述' }}</text>
			</view>

			<view class="result-page__card">
				<text class="result-page__card-title">可能病因</text>
				<text class="result-page__paragraph">{{ res.physique_analysis || '暂无分析' }}</text>
			</view>

			<view v-for="section in adviceSections" :key="section.key" class="result-page__card">
				<text class="result-page__card-title">{{ section.title }}</text>
				<view class="result-page__list">
					<view v-for="(item, index) in section.items" :key="`${section.key}-${index}`" class="result-page__list-item">
						<text class="result-page__list-title">{{ item.title }}</text>
						<text class="result-page__list-text">{{ item.advice }}</text>
					</view>
				</view>
			</view>

			<view class="result-page__actions">
				<button class="result-page__secondary" @click="goBack">返回上一页</button>
				<button class="result-page__primary" @click="goToTest">继续检测</button>
			</view>
		</template>

		<view v-else class="result-page__empty">
			<text class="result-page__empty-title">暂无检测数据</text>
			<text class="result-page__empty-text">请先完成一次健康检测，再查看体质报告。</text>
			<button class="result-page__primary" @click="goToTest">去检测</button>
		</view>
	</view>
</template>

<script>
import TaskTopBar from '../../components/premium/TaskTopBar.vue';

const mockData = {
	physique_name: '气虚质',
	physique_analysis: '气虚质多见于元气不足，常表现为面色偏淡、语声较低、容易疲乏。',
	risk_warning: '近期应注意保暖，避免劳累过度，保持规律睡眠与轻量活动。',
	typical_symptom: '气短乏力；容易疲倦；声音低弱；易出汗；面色偏淡。',
	advices: {
		food: [
			{
				title: '宜食补气食物',
				advice: '可多选山药、黄芪、莲子、鸡肉、牛肉等益气健脾食材。'
			},
			{
				title: '少食生冷',
				advice: '减少冷饮和寒凉瓜果，避免损伤脾胃阳气。'
			}
		],
		treatment: [
			{
				title: '穴位按揉',
				advice: '可按揉足三里、气海、关元等穴位，每次10至15分钟。'
			}
		],
		sport: [
			{
				title: '适度运动',
				advice: '可进行太极、八段锦、散步等柔和运动，避免大汗淋漓。'
			}
		],
		sleep: [
			{
				title: '规律作息',
				advice: '保持早睡早起，减少熬夜，有助于恢复元气。'
			}
		]
	}
};

export default {
	components: {
		TaskTopBar
	},
	data() {
		return {
			res: null,
			loading: true
		};
	},
	computed: {
		reportTitle() {
			return this.res?.physique_name || '检测报告';
		},
		adviceSections() {
			const advices = this.res?.advices || {};
			return [
				{ key: 'food', title: '饮食调理', items: Array.isArray(advices.food) ? advices.food : [] },
				{ key: 'treatment', title: '穴位按揉', items: Array.isArray(advices.treatment) ? advices.treatment : [] },
				{ key: 'sport', title: '运动建议', items: Array.isArray(advices.sport) ? advices.sport : [] },
				{ key: 'sleep', title: '作息建议', items: Array.isArray(advices.sleep) ? advices.sleep : [] }
			].filter((section) => section.items.length);
		}
	},
	methods: {
		goBack() {
			uni.navigateBack({
				delta: 1,
				fail: () => {
					uni.switchTab({
						url: '/pages/zice/zice'
					});
				}
			});
		},
		goToTest() {
			uni.navigateTo({
				url: '/pages/guide/guide'
			});
		},
		loadData() {
			try {
				const faceData = uni.getStorageSync('faceData');
				this.res = faceData ? JSON.parse(faceData) : mockData;
			} catch (error) {
				console.error('解析体质数据失败', error);
				this.res = mockData;
			}
			this.loading = false;
		}
	},
	onLoad() {
		this.loadData();
	}
};
</script>

<style>
.result-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background:
		linear-gradient(180deg, #ffffff 0%, #f8faf7 100%),
		radial-gradient(circle at 100% 0%, rgba(208, 231, 234, 0.24) 0, transparent 42%);
	box-sizing: border-box;
}

.result-page__loading,
.result-page__empty {
	margin-top: 120px;
	padding: 56px 28px;
	border-radius: 30px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 18px 42px rgba(31, 95, 202, 0.07);
	text-align: center;
}

.result-page__loading-text,
.result-page__empty-title {
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
}

.result-page__empty-text {
	display: block;
	margin: 14px 0 20px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.7;
}

.result-page__hero {
	padding: 24px 22px;
	border-radius: 30px;
	background: linear-gradient(135deg, #1f5fca 0%, #3f8cff 100%);
	box-shadow: 0 20px 42px rgba(31, 95, 202, 0.18);
	color: #ffffff;
}

.result-page__eyebrow {
	display: block;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
	color: rgba(255, 255, 255, 0.8);
}

.result-page__hero-title {
	display: block;
	margin-top: 14px;
	font-size: 34px;
	font-weight: 800;
	line-height: 1.15;
}

.result-page__hero-subtitle {
	display: block;
	margin-top: 12px;
	font-size: 14px;
	line-height: 1.75;
	color: rgba(255, 255, 255, 0.88);
}

.result-page__card {
	margin-top: 16px;
	padding: 20px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.result-page__card-title {
	display: block;
	color: #103268;
	font-size: 18px;
	font-weight: 800;
}

.result-page__paragraph {
	display: block;
	margin-top: 12px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.8;
}

.result-page__list {
	display: flex;
	flex-direction: column;
	gap: 12px;
	margin-top: 14px;
}

.result-page__list-item {
	padding: 14px;
	border-radius: 20px;
	background: #f0f5ff;
}

.result-page__list-title {
	display: block;
	color: #1f5fca;
	font-size: 15px;
	font-weight: 800;
}

.result-page__list-text {
	display: block;
	margin-top: 8px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.7;
}

.result-page__actions {
	display: flex;
	gap: 12px;
	margin-top: 20px;
}

.result-page__primary,
.result-page__secondary {
	flex: 1;
	border-radius: 22px;
	font-size: 16px;
	font-weight: 800;
}

.result-page__primary {
	background: #1f5fca;
	color: #ffffff;
}

.result-page__secondary {
	background: rgba(255, 255, 255, 0.92);
	color: #1f5fca;
	border: 1px solid rgba(163, 196, 232, 0.5);
}
</style>
