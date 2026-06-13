<template>
	<view class="voice-report-page">
		<TaskTopBar title="声纹报告" :show-right="false" @back="goBack" />

		<view v-if="loading" class="voice-report-page__loading">
			<text class="voice-report-page__loading-text">报告生成中...</text>
		</view>

		<template v-else-if="report">
			<view class="voice-report-page__hero">
				<view class="voice-report-page__hero-top">
					<text class="voice-report-page__badge">{{ reportMeta.isMock ? '演示报告' : '智能分析报告' }}</text>
					<text class="voice-report-page__time">{{ formatTime(reportMeta.generatedAt) }}</text>
				</view>
				<text class="voice-report-page__eyebrow">VOICE HEALTH REPORT</text>
				<text class="voice-report-page__title">{{ report.syndrome_name || '待确认体质' }}</text>
				<text class="voice-report-page__subtitle">{{ report.syndrome_explain || '暂无识别说明' }}</text>
			</view>

			<view class="voice-report-page__card">
				<text class="voice-report-page__card-title">诊断关键词</text>
				<view class="voice-report-page__tags">
					<text v-for="(item, index) in keywords" :key="`${item}-${index}`" class="voice-report-page__tag">
						{{ item }}
					</text>
				</view>
			</view>

			<view v-if="report.diagnose_explain" class="voice-report-page__card">
				<text class="voice-report-page__card-title">研判说明</text>
				<text class="voice-report-page__paragraph">{{ report.diagnose_explain }}</text>
			</view>

			<view v-if="report.risk_warning" class="voice-report-page__card voice-report-page__card--warm">
				<text class="voice-report-page__card-title">风险提醒</text>
				<text class="voice-report-page__paragraph">{{ report.risk_warning }}</text>
			</view>

			<view v-if="lifestyleSuggestions.length" class="voice-report-page__card">
				<text class="voice-report-page__card-title">调理建议</text>
				<view class="voice-report-page__list">
					<view
						v-for="(item, index) in lifestyleSuggestions"
						:key="`suggestion-${index}`"
						class="voice-report-page__list-item"
					>
						<text class="voice-report-page__list-index">{{ index + 1 }}</text>
						<text class="voice-report-page__list-text">{{ item }}</text>
					</view>
				</view>
			</view>

			<view v-if="report.suggest_live" class="voice-report-page__card">
				<text class="voice-report-page__card-title">生活方式建议</text>
				<text class="voice-report-page__paragraph">{{ report.suggest_live }}</text>
			</view>

			<view v-if="commodities.length" class="voice-report-page__card">
				<text class="voice-report-page__card-title">推荐调养品</text>
				<view class="voice-report-page__commodity-grid">
					<view
						v-for="(item, index) in commodities"
						:key="`${item.commodity_name}-${index}`"
						class="voice-report-page__commodity"
					>
						<image class="voice-report-page__commodity-image" :src="assetUrl(item.commodity_img_path)" mode="aspectFit"></image>
						<text class="voice-report-page__commodity-name">{{ item.commodity_name }}</text>
					</view>
				</view>
			</view>

			<view class="voice-report-page__actions">
				<button class="voice-report-page__secondary" @click="goBack">重新录音</button>
				<button class="voice-report-page__primary" @click="goSelfTest">返回健康自测</button>
			</view>
		</template>

		<view v-else class="voice-report-page__empty">
			<text class="voice-report-page__empty-title">暂无可展示的声纹报告</text>
			<text class="voice-report-page__empty-text">请先完成一次声纹录音，再查看分析结果。</text>
			<button class="voice-report-page__primary" @click="goBack">返回重试</button>
		</view>
	</view>
</template>

<script>
import { buildAssetUrl } from '../../common/api.js';
import TaskTopBar from '../../components/premium/TaskTopBar.vue';

const VOICE_REPORT_STORAGE_KEY = 'voiceReportData';

const mockVoiceData = {
	code: 20000,
	success: true,
	msg: '成功',
	data: {
		answer_type: 2,
		report: {
			syndrome_name: '气虚质',
			syndrome_explain: '声音偏弱、气息不足，提示气虚倾向较明显。',
			syndrome_point: null,
			contraindication: null,
			diagnose_key_words: ['声音低弱', '气息不足', '容易疲乏'],
			diagnose_explain: '肺脾之气偏弱时，常会出现说话易累、声音不够洪亮的表现。',
			symptom_names: [
				{
					name: '声音低弱',
					desc: '说话时气息不足，声音较轻，连续表达容易疲劳。',
					type: '声纹'
				}
			],
			commodity: [
				{
					commodity_name: '补中益气丸9g*10袋',
					commodity_img_path: 'https://img.alicdn.com/imgextra/i4/2200724907121/O1CN01.jpg',
					commodity_shopping_link: 'packages/goods/detail/index?alias=xxx'
				},
				{
					commodity_name: '黄芪口服液10ml*10支',
					commodity_img_path: 'https://img.alicdn.com/imgextra/i4/2200724907121/O1CN02.jpg',
					commodity_shopping_link: 'packages/goods/detail/index?alias=xxx2'
				}
			],
			combine_prescriptions: [],
			risk_warning: '建议保证休息，避免过度用嗓，近期注意保暖与规律饮食。',
			suggest_live: '保持作息规律，适度活动，连续讲话后注意补水休息。',
			suggest_food: '饮食管理：多选山药、黄芪、莲子等益气食材。\n运动管理：可做八段锦、散步等柔和运动。\n防护措施：避免熬夜，减少久坐。'
		},
		addition_questions: [],
		session_id: 'mock-session-id-12345'
	},
	meta: {
		reportNo: 'SV-DEMO',
		generatedAt: Date.now(),
		sourceText: '春夏养阳，秋冬养阴。',
		isMock: true
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
		report() {
			return this.res?.data?.report || null;
		},
		reportMeta() {
			return this.res?.meta || {};
		},
		keywords() {
			return Array.isArray(this.report?.diagnose_key_words) ? this.report.diagnose_key_words : [];
		},
		commodities() {
			return Array.isArray(this.report?.commodity) ? this.report.commodity : [];
		},
		lifestyleSuggestions() {
			if (!this.report?.suggest_food) {
				return [];
			}
			return this.report.suggest_food
				.split('\n')
				.map((item) => item.trim())
				.filter(Boolean);
		}
	},
	methods: {
		assetUrl(path) {
			return buildAssetUrl(path);
		},
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
		goSelfTest() {
			uni.switchTab({
				url: '/pages/zice/zice'
			});
		},
		formatTime(timestamp) {
			if (!timestamp) {
				return '刚刚生成';
			}
			const date = new Date(timestamp);
			const year = date.getFullYear();
			const month = `${date.getMonth() + 1}`.padStart(2, '0');
			const day = `${date.getDate()}`.padStart(2, '0');
			const hour = `${date.getHours()}`.padStart(2, '0');
			const minute = `${date.getMinutes()}`.padStart(2, '0');
			return `${year}-${month}-${day} ${hour}:${minute}`;
		},
		parseVoiceReport(rawValue) {
			if (!rawValue) {
				return null;
			}
			try {
				const parsed = typeof rawValue === 'string' ? JSON.parse(rawValue) : rawValue;
				if (parsed?.data?.report) {
					return parsed;
				}
			} catch (error) {
				console.error('解析声纹报告失败', error);
			}
			return null;
		},
		loadData() {
			const cachedVoiceReport = this.parseVoiceReport(uni.getStorageSync(VOICE_REPORT_STORAGE_KEY));
			const legacyVoiceReport = this.parseVoiceReport(uni.getStorageSync('faceData'));
			this.res = cachedVoiceReport || legacyVoiceReport || mockVoiceData;
			this.loading = false;
		}
	},
	onLoad() {
		this.loadData();
	}
};
</script>

<style>
.voice-report-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background:
			linear-gradient(180deg, #ffffff 0%, #f0f5ff 100%),
			radial-gradient(circle at 100% 0%, rgba(208, 231, 234, 0.24) 0, transparent 42%);
	box-sizing: border-box;
}

.voice-report-page__loading,
.voice-report-page__empty {
	margin-top: 120px;
	padding: 56px 28px;
	border-radius: 30px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 18px 42px rgba(31, 95, 202, 0.07);
	text-align: center;
}

.voice-report-page__loading-text,
.voice-report-page__empty-title {
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
}

.voice-report-page__empty-text {
	display: block;
	margin: 14px 0 20px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.7;
}

.voice-report-page__hero {
	padding: 24px 22px;
	border-radius: 30px;
	background: linear-gradient(135deg, #1f5fca 0%, #0f5d52 100%);
	box-shadow: 0 20px 42px rgba(31, 95, 202, 0.18);
	color: #ffffff;
}

.voice-report-page__hero-top {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
}

.voice-report-page__badge {
	padding: 6px 12px;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.18);
	font-size: 11px;
	font-weight: 800;
}

.voice-report-page__time {
	font-size: 12px;
	color: rgba(255, 255, 255, 0.82);
}

.voice-report-page__eyebrow {
	display: block;
	margin-top: 18px;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
	color: rgba(255, 255, 255, 0.8);
}

.voice-report-page__title {
	display: block;
	margin-top: 12px;
	font-size: 34px;
	font-weight: 800;
	line-height: 1.15;
}

.voice-report-page__subtitle {
	display: block;
	margin-top: 12px;
	font-size: 14px;
	line-height: 1.75;
	color: rgba(255, 255, 255, 0.88);
}

.voice-report-page__card {
	margin-top: 16px;
	padding: 20px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.voice-report-page__card--warm {
	background: linear-gradient(180deg, #fff8ef 0%, #fffdf9 100%);
}

.voice-report-page__card-title {
	display: block;
	color: #103268;
	font-size: 18px;
	font-weight: 800;
}

.voice-report-page__tags {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-top: 14px;
}

.voice-report-page__tag {
	padding: 10px 14px;
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.48);
	color: #065043;
	font-size: 13px;
	font-weight: 700;
}

.voice-report-page__paragraph {
	display: block;
	margin-top: 12px;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.8;
	white-space: pre-line;
}

.voice-report-page__list {
	display: flex;
	flex-direction: column;
	gap: 14px;
	margin-top: 14px;
}

.voice-report-page__list-item {
	display: flex;
	align-items: flex-start;
	gap: 12px;
}

.voice-report-page__list-index {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 26px;
	height: 26px;
	flex-shrink: 0;
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.48);
	color: #065043;
	font-size: 13px;
	font-weight: 800;
}

.voice-report-page__list-text {
	flex: 1;
	color: #5a7a99;
	font-size: 14px;
	line-height: 1.75;
}

.voice-report-page__commodity-grid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 14px;
	margin-top: 14px;
}

.voice-report-page__commodity {
	padding: 14px;
	border-radius: 22px;
	background: #f0f5ff;
}

.voice-report-page__commodity-image {
	width: 100%;
	height: 120px;
}

.voice-report-page__commodity-name {
	display: block;
	margin-top: 10px;
	color: #425466;
	font-size: 12px;
	line-height: 1.6;
}

.voice-report-page__actions {
	display: flex;
	gap: 12px;
	margin-top: 20px;
}

.voice-report-page__primary,
.voice-report-page__secondary {
	flex: 1;
	border-radius: 22px;
	font-size: 16px;
	font-weight: 800;
}

.voice-report-page__primary {
	background: #1f5fca;
	color: #ffffff;
}

.voice-report-page__secondary {
	background: rgba(255, 255, 255, 0.92);
	color: #1f5fca;
	border: 1px solid rgba(163, 196, 232, 0.5);
}
</style>
