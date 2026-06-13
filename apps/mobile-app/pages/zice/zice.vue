<template>
	<view class="self-test-container">
		<PremiumTopBar title="云枢四诊" />

		<SelfTestHeroPanel
			title="全面检测"
			description="结合面诊、舌诊、脉诊与声纹分析，生成更完整的中医体质观察结果。"
			button-text="START"
			image="../../static/jc.png"
			@tap="jump('/pages/guide/guide')"
		/>

		<view class="section-head">
			<text class="section-head__title">单项自测</text>
			<text class="section-head__caption">DIAGNOSTIC TOOLS</text>
		</view>

		<view class="grid-container">
			<DiagnosticTile
				v-for="item in gridItems"
				:key="item.url"
				:title="item.name"
				:subtitle="item.subtitle"
				:icon="item.icon"
				:wide="item.wide"
				@tap="jump(item.url)"
			/>
		</view>

		<ResultSummaryPanel
			class="self-test-container__panel"
			:physique-name="physiqueName"
			:symptoms="symptomList"
			:score="resultCoverage"
			:tendency-label="tendencyLabel"
		/>

		<RecordTrendPanel class="self-test-container__panel" :records="chartData" />

		<PremiumTabBar active="zice" />
	</view>
</template>

<script>
import DiagnosticTile from '../../components/premium/DiagnosticTile.vue';
import PremiumTabBar from '../../components/premium/PremiumTabBar.vue';
import PremiumTopBar from '../../components/premium/PremiumTopBar.vue';
import RecordTrendPanel from '../../components/premium/RecordTrendPanel.vue';
import ResultSummaryPanel from '../../components/premium/ResultSummaryPanel.vue';
import SelfTestHeroPanel from '../../components/premium/SelfTestHeroPanel.vue';

export default {
	components: {
		DiagnosticTile,
		PremiumTabBar,
		PremiumTopBar,
		RecordTrendPanel,
		ResultSummaryPanel,
		SelfTestHeroPanel
	},
	data() {
		return {
			gridItems: [
				{ name: '声纹自测', subtitle: '采集声音特征', icon: '../../static/sw.png', url: '/pages/voiceprint/voiceprint' },
				{ name: '面诊自测', subtitle: '观察面部气色', icon: '../../static/face.png', url: '/pages/photo/photo?type=2' },
				{ name: '脉诊测试', subtitle: '记录脉象变化', icon: '../../static/huan.png', url: '/pages/pulse/pulse' },
				{ name: '舌诊自测', subtitle: '分析舌象状态', icon: '../../static/st.png', url: '/pages/photo/photo?type=1' },
				{ name: '智能问答', subtitle: '继续咨询体质问题', icon: '../../static/wd.png', url: '/pages/chat/chat', wide: true }
			],
			chartData: [],
			tz: null
		};
	},
	computed: {
		physiqueName() {
			return this.tz && this.tz.physique_name ? this.tz.physique_name : '';
		},
		symptomList() {
			return this.tz && Array.isArray(this.tz.arr) ? this.tz.arr : [];
		},
		resultCoverage() {
			const coverageItems = [
				!!this.physiqueName,
				this.symptomList.length > 0,
				this.chartData.length > 0
			];
			const completed = coverageItems.filter(Boolean).length;
			return Math.round((completed / coverageItems.length) * 100);
		},
		tendencyLabel() {
			if (!this.physiqueName) return '';
			if (!this.symptomList.length) return '已识别体质';
			return `${this.symptomList.length} 项识别`;
		}
	},
	onShow() {
		this.gettz();
		this.loadPulseHistory();
	},
	methods: {
		jump(url) {
			uni.navigateTo({
				url
			});
		},
		gettz() {
			try {
				const faceData = uni.getStorageSync('faceData');
				if (faceData) {
					this.tz = JSON.parse(faceData);
					if (this.tz && this.tz.typical_symptom) {
						const arr = this.tz.typical_symptom.split(/[；;]/);
						this.tz.arr = arr.filter(item => item && item.trim());
					} else {
						this.tz = { ...this.tz, arr: [] };
					}
				} else {
					this.tz = null;
				}
			} catch (e) {
				console.error('解析体质数据失败', e);
				this.tz = null;
			}
		},
		loadPulseHistory() {
			try {
				const pulseHistory = JSON.parse(uni.getStorageSync('pulseHistory') || '[]');
				if (Array.isArray(pulseHistory) && pulseHistory.length) {
					const latestRecords = pulseHistory.slice(-6);
					this.chartData = latestRecords.map((item, index) => ({
						value: Number(item.value) || 0,
						label: item.label || `第${index + 1}次`
					}));
					return;
				}
			} catch (e) {
				console.error('解析脉诊历史失败', e);
			}
			this.chartData = [
				{ value: 76, label: '07:30' },
				{ value: 74, label: '09:00' },
				{ value: 72, label: '10:30' },
				{ value: 77, label: '12:15' },
				{ value: 73, label: '14:00' },
				{ value: 71, label: '15:30' },
				{ value: 70, label: '18:30' }
			];
		}
	}
};
</script>

<style>
.self-test-container {
	min-height: 100vh;
	padding: 18px 18px 116px;
	background-color: #f8faf7;
	background-image:
		radial-gradient(circle at 0% 0%, rgba(208, 231, 234, 0.56) 0, transparent 38%),
		radial-gradient(circle at 100% 66%, rgba(148, 211, 193, 0.18) 0, transparent 40%);
	box-sizing: border-box;
}

.section-head {
	display: flex;
	flex-direction: column;
	margin: 22px 2px 14px;
}

.section-head__title {
	color: #1f5fca;
	font-size: 24px;
	font-weight: 800;
	line-height: 1.25;
}

.section-head__caption {
	margin-top: 6px;
	color: #4d6265;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.grid-container {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 12px;
}

.self-test-container__panel {
	margin-top: 16px;
}
</style>
