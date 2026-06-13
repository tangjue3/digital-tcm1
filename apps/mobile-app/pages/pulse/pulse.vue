<template>
	<view class="pulse-page">
		<TaskTopBar title="云枢四诊" @back="goBack" />

		<DetectionGuideCard
			title="脉诊测试"
			text="请先佩戴手环，并在测试过程中尽量保持手臂静止，确保采集到稳定的脉诊数据。"
			marker="?"
		/>

		<DetectionStatusPanel
			:title="statusTitle"
			:description="statusDescription"
			:core-text="statusCoreText"
			:show-progress="isMeasuring"
			:pulse-rate="!isMeasuring && pulseRate ? pulseRate : 0"
			:sync-status-text="!isMeasuring && pulseRate ? syncStatusText : ''"
		/>

		<LastRecordCard v-if="lastPulseRate" :value="lastPulseRate" chip="平稳" />

		<view class="pulse-page__actions">
			<button class="pulse-page__primary" :disabled="isMeasuring || syncing" @tap="handleStartTest">
				{{ pulseRate ? '重新测试' : '开始测试' }}
			</button>
			<button v-if="pulseRate" class="pulse-page__secondary" :disabled="isMeasuring || syncing" @tap="finishTest">完成</button>
		</view>
	</view>
</template>

<script>
import { getAppUserProfile, saveAppUserProfile, savePulseRecord } from '../../common/api.js';
import DetectionGuideCard from '../../components/premium/DetectionGuideCard.vue';
import DetectionStatusPanel from '../../components/premium/DetectionStatusPanel.vue';
import LastRecordCard from '../../components/premium/LastRecordCard.vue';
import TaskTopBar from '../../components/premium/TaskTopBar.vue';

const MIN_PULSE = 60;
const MAX_PULSE = 100;
const SESSION_PULSE_BASE_KEY = 'pulseSessionBase';

export default {
	components: {
		DetectionGuideCard,
		DetectionStatusPanel,
		LastRecordCard,
		TaskTopBar
	},
	data() {
		return {
			isMeasuring: false,
			pulseRate: 0,
			lastPulseRate: '',
			sessionPulseBase: 0,
			timer: null,
			syncing: false,
			syncStatusText: ''
		};
	},
	computed: {
		statusTitle() {
			if (this.isMeasuring) return '正在采集脉诊波形...';
			if (this.pulseRate) return '本次测试结果';
			return '准备开始测试';
		},
		statusDescription() {
			if (this.isMeasuring) return '请保持手指稳定，自然呼吸，系统正在模拟采集脉诊数据。';
			if (this.pulseRate) return '本次测试结果已同步到本地健康档案，可继续重新测试或完成返回。';
			return '点击下方按钮后，会先提示佩戴手环，再开始模拟测量。';
		},
		statusCoreText() {
			if (this.isMeasuring) return '';
			if (this.pulseRate) return String(this.pulseRate);
			return '';
		}
	},
	onLoad() {
		this.loadLastPulseRate();
		this.loadSessionPulseBase();
	},
	onUnload() {
		this.clearTimer();
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
		loadLastPulseRate() {
			try {
				const guide = JSON.parse(uni.getStorageSync('userGuide') || '{}');
				this.lastPulseRate = guide.pulseRate || '';
			} catch (error) {
				console.error('读取最近脉诊数据失败', error);
				this.lastPulseRate = '';
			}
		},
		loadSessionPulseBase() {
			const sessionPulseBase = Number(uni.getStorageSync(SESSION_PULSE_BASE_KEY));
			this.sessionPulseBase = Number.isFinite(sessionPulseBase) && sessionPulseBase > 0 ? sessionPulseBase : 0;
		},
		handleStartTest() {
			if (this.isMeasuring || this.syncing) {
				return;
			}
			uni.showModal({
				title: '佩戴提示',
				content: '请先戴上手环，并保持手臂静止。点击确定后开始测试脉诊。',
				success: (res) => {
					if (res.confirm) {
						this.startFakeTest();
					}
				}
			});
		},
		startFakeTest() {
			this.clearTimer();
			this.syncStatusText = '';
			this.pulseRate = 0;
			this.isMeasuring = true;
			uni.showToast({ title: '开始检测，请保持手指贴在传感器上', icon: 'none' });
			this.timer = setTimeout(() => {
				this.completeFakeTest();
			}, this.getRandomInt(12, 16) * 1000);
		},
		async completeFakeTest() {
			this.clearTimer();
			this.isMeasuring = false;
			this.pulseRate = this.generatePulseRate();
			this.lastPulseRate = this.pulseRate;
			const synced = await this.savePulseResult(this.pulseRate);
			uni.showToast({ title: '检测完成，正在分析数据', icon: 'none' });
		},
		async savePulseResult(pulseRate) {
			this.syncing = true;
			this.updateLocalPulseData(pulseRate);
			try {
				const guide = JSON.parse(uni.getStorageSync('userGuide') || '{}');
				const profile = await getAppUserProfile().catch(() => null);
				const remark = this.buildProfileRemark(profile, guide, pulseRate);
				await saveAppUserProfile({
					sex: profile?.sex || guide.gender || '',
					age: profile?.age ?? this.toNumberOrNull(guide.age),
					voiceFile: profile?.voiceFile || guide.audioPath || '',
					tongueImage: profile?.tongueImage || guide.tonguePhoto || '',
					faceImage: profile?.faceImage || guide.facePhoto || '',
					remark: JSON.stringify(remark)
				});
				await savePulseRecord({
					pulseRate: Number(pulseRate),
					deviceId: 'mock-band',
					signalQuality: 100,
					source: 'app',
					remark: 'app端重新测试脉诊'
				});
				this.syncStatusText = '结果已上传到后台，管理端会显示这次新的脉诊数据。';
				return true;
			} catch (error) {
				console.error('同步脉诊结果失败', error);
				this.syncStatusText = '本地结果已更新，但后台同步失败，请稍后重试。';
				return false;
			} finally {
				this.syncing = false;
			}
		},
		updateLocalPulseData(pulseRate) {
			try {
				const guide = JSON.parse(uni.getStorageSync('userGuide') || '{}');
				guide.pulseRate = pulseRate;
				uni.setStorageSync('userGuide', JSON.stringify(guide));

				const pulseHistory = JSON.parse(uni.getStorageSync('pulseHistory') || '[]');
				pulseHistory.push({
					value: pulseRate,
					label: this.formatLabel(new Date()),
					time: Date.now()
				});
				uni.setStorageSync('pulseHistory', JSON.stringify(pulseHistory.slice(-10)));
			} catch (error) {
				console.error('保存本地脉诊结果失败', error);
			}
		},
		generatePulseRate() {
			if (!this.sessionPulseBase) {
				const initialPulseRate = this.getRandomInt(MIN_PULSE, MAX_PULSE);
				this.sessionPulseBase = initialPulseRate;
				uni.setStorageSync(SESSION_PULSE_BASE_KEY, initialPulseRate);
				return initialPulseRate;
			}
			const minPulseRate = Math.max(MIN_PULSE, Math.round(this.sessionPulseBase * 0.9));
			const maxPulseRate = Math.min(MAX_PULSE, Math.round(this.sessionPulseBase * 1.1));
			return this.getRandomInt(minPulseRate, Math.max(minPulseRate, maxPulseRate));
		},
		buildProfileRemark(profile, guide, pulseRate) {
			let remark = {};
			try {
				remark = profile?.remark ? JSON.parse(profile.remark) : {};
			} catch (error) {
				remark = {};
			}
			return {
				weight: remark.weight || guide.weight || '',
				height: remark.height || guide.height || '',
				pulseRate,
				medicalHistory: remark.medicalHistory || guide.medicalHistory || []
			};
		},
		toNumberOrNull(value) {
			const numberValue = Number(value);
			return Number.isFinite(numberValue) ? numberValue : null;
		},
		finishTest() {
			uni.navigateBack({
				delta: 1
			});
		},
		formatLabel(date) {
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${month}-${day}`;
		},
		getRandomInt(min, max) {
			return Math.floor(Math.random() * (max - min + 1)) + min;
		},
		clearTimer() {
			if (this.timer) {
				clearTimeout(this.timer);
				this.timer = null;
			}
			if (this.pulseTimer) {
				clearInterval(this.pulseTimer);
				this.pulseTimer = null;
				this.stateMessage = '检测中断，请重试';
			}
		}
	}
};
</script>

<style>
.pulse-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background:
			linear-gradient(180deg, #ffffff 0%, #f0f5ff 100%),
			radial-gradient(circle at 100% 0%, rgba(208, 231, 234, 0.24) 0, transparent 42%);
	box-sizing: border-box;
}

.pulse-page__actions {
	display: flex;
	flex-direction: column;
	gap: 14px;
	margin-top: 20px;
}

.pulse-page__primary,
.pulse-page__secondary {
	border-radius: 22px;
	font-size: 17px;
	font-weight: 800;
}

.pulse-page__primary {
	background: #1f5fca;
	color: #ffffff;
}

.pulse-page__secondary {
	background: rgba(255, 255, 255, 0.92);
	color: #1f5fca;
	border: 1px solid rgba(163, 196, 232, 0.5);
}
</style>
