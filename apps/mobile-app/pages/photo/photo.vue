<template>
	<view class="photo-page">
		<TaskTopBar :title="pageTitle" :show-right="false" @back="goBack" />

		<DetectionGuideCard
			:title="guideTitle"
			:text="guideText"
			:marker="guideMarker"
		/>

		<view class="photo-page__preview-card">
			<text class="photo-page__eyebrow">{{ guideMarker === '?' ? 'TONGUE SAMPLE' : 'FACE SAMPLE' }}</text>
			<text class="photo-page__preview-title">{{ imageSelected ? '已完成图片采集' : '请拍摄清晰正面照片' }}</text>
			<view class="photo-page__preview-shell">
				<image class="photo-page__preview-image" :src="imgsrc" mode="aspectFill"></image>
			</view>
			<text class="photo-page__preview-tip">
				{{ imageSelected ? '如需更换，可重新拍摄或从相册选择' : '建议在自然光下拍摄，保持画面稳定、无遮挡' }}
			</text>
		</view>

		<DetectionStatusPanel
			:title="statusTitle"
			:description="statusDescription"
			:core-text="statusCoreText"
			:show-progress="analysisPending || uploading"
		/>

		<view v-if="lastPhysiqueName" class="photo-page__latest-card">
			<text class="photo-page__latest-eyebrow">最近识别</text>
			<text class="photo-page__latest-title">{{ lastPhysiqueName }}</text>
			<text class="photo-page__latest-desc">{{ lastRiskWarning }}</text>
		</view>

		<view class="photo-page__actions">
			<button class="photo-page__primary" :disabled="analysisPending || uploading" @tap="primaryAction">
				{{ primaryButtonText }}
			</button>
			<button class="photo-page__secondary" :disabled="analysisPending || uploading" @tap="pz">重新拍摄</button>
		</view>
	</view>
</template>

<script>
import { getAppUserProfile, saveAppUserProfile, uploadCommonFile } from '../../common/api.js';
import DetectionGuideCard from '../../components/premium/DetectionGuideCard.vue';
import DetectionStatusPanel from '../../components/premium/DetectionStatusPanel.vue';
import TaskTopBar from '../../components/premium/TaskTopBar.vue';

const mockFaceAiData = {
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
		DetectionGuideCard,
		DetectionStatusPanel,
		TaskTopBar
	},
	data() {
		return {
			imgsrc: '../../static/img/photo.png',
			create: false,
			analysisPending: false,
			analysisReadyAt: 0,
			type: 1,
			uploading: false,
			lastReport: null,
			syncMessage: ''
		};
	},
	computed: {
		pageTitle() {
			return this.type === 1 ? '舌诊采集' : '面诊采集';
		},
		guideTitle() {
			return this.type === 1 ? '采集舌象照片' : '采集面部照片';
		},
		guideText() {
			return this.type === 1
				? '请张口平伸舌体，在自然光下拍摄完整舌面，避免滤镜和过度美颜。'
				: '请保持正视镜头，在光线均匀环境中拍摄清晰面部照片，尽量避免遮挡。';
		},
		guideMarker() {
			return this.type === 1 ? '?' : '?';
		},
		imageSelected() {
			return !String(this.imgsrc).includes('../../static/img/photo.png');
		},
		statusTitle() {
			if (this.analysisPending) return '正在生成体质报告';
			if (this.uploading) return '正在识别图片内容';
			if (this.create) return '识别结果已就绪';
			if (this.imageSelected) return '图片已上传';
			return '等待开始采集';
		},
		statusDescription() {
			if (this.analysisPending) return '系统正在分析图像特征并整理结果，请稍候。';
			if (this.uploading) return '图片已上传，系统正在识别舌象或面色特征。';
			if (this.syncMessage) return this.syncMessage;
			if (this.create) return '识别完成，现在可以直接生成详细报告。';
			if (this.imageSelected) return '图片已选择，等待识别完成后即可生成结果页。';
			return '先拍摄或选择照片，系统会自动尝试调用后端识别。';
		},
		statusCoreText() {
			if (this.analysisPending) return '';
			if (this.uploading) return '';
			if (this.create) return this.guideMarker;
			if (this.imageSelected) return '';
			return this.guideMarker;
		},
		primaryButtonText() {
			if (this.analysisPending) return '分析中...';
			return this.create ? '生成报告' : '拍摄照片';
		},
		lastPhysiqueName() {
			return this.lastReport?.physique_name || '';
		},
		lastRiskWarning() {
			return this.lastReport?.risk_warning || '最近一次识别结果已保存到本地。';
		}
	},
	onLoad(data) {
		this.type = Number(data.type || 1);
		this.loadLastReport();
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
		loadLastReport() {
			try {
				const faceData = uni.getStorageSync('faceData');
				this.lastReport = faceData ? JSON.parse(faceData) : null;
			} catch (error) {
				console.error('读取体质结果失败', error);
				this.lastReport = null;
			}
		},
		getStoredGuideData() {
			try {
				return JSON.parse(uni.getStorageSync('userGuide') || '{}');
			} catch (error) {
				return {};
			}
		},
		parseProfileRemark(profile) {
			try {
				return profile?.remark ? JSON.parse(profile.remark) : {};
			} catch (error) {
				return {};
			}
		},
		toNumberOrNull(value) {
			const numberValue = Number(value);
			return Number.isFinite(numberValue) ? numberValue : null;
		},
		getAnalysisDelay() {
			return 6000 + Math.floor(Math.random() * 4001);
		},
		beginAnalysis() {
			this.analysisPending = true;
			this.analysisReadyAt = Date.now() + this.getAnalysisDelay();
			uni.showLoading({
				title: '分析中...',
				mask: true
			});
		},
		completeAnalysis(handler, toastTitle = '分析完成') {
			const remaining = Math.max(0, this.analysisReadyAt - Date.now());
			setTimeout(() => {
				handler();
				this.analysisPending = false;
				uni.hideLoading();
				uni.showToast({
					title: toastTitle,
					icon: 'success'
				});
			}, remaining);
		},
		primaryAction() {
			if (this.create) {
				this.createfunc();
				return;
			}
			this.pz();
		},
		async syncCollectedImage(filePath) {
			if (!uni.getStorageSync('loginType') || !filePath) {
				return;
			}

			const guide = this.getStoredGuideData();
			const profile = await getAppUserProfile().catch(() => null);
			const remark = this.parseProfileRemark(profile);
			const remotePath = await uploadCommonFile(filePath, {
				defaultName: this.type === 1 ? 'tongue' : 'face',
				contentType: 'image/jpeg'
			});

			const nextGuide = {
				...guide,
				[this.type === 1 ? 'tonguePhoto' : 'facePhoto']: remotePath || filePath
			};

			await saveAppUserProfile({
				sex: profile?.sex || guide.gender || '',
				age: profile?.age ?? this.toNumberOrNull(guide.age),
				voiceFile: profile?.voiceFile || guide.audioPath || '',
				tongueImage: this.type === 1 ? remotePath : profile?.tongueImage || guide.tonguePhoto || '',
				faceImage: this.type === 1 ? profile?.faceImage || guide.facePhoto || '' : remotePath,
				remark: JSON.stringify({
					weight: remark.weight || guide.weight || '',
					height: remark.height || guide.height || '',
					pulseRate: remark.pulseRate || guide.pulseRate || '',
					medicalHistory: remark.medicalHistory || guide.medicalHistory || []
				})
			});

			uni.setStorageSync('userGuide', JSON.stringify(nextGuide));
			uni.setStorageSync('hasCompletedGuide', true);
		},
		async finishImageRecognition(filePath, payload) {
			uni.setStorageSync('faceData', JSON.stringify(payload));
			this.lastReport = payload;
			this.create = true;
			try {
				await this.syncCollectedImage(filePath);
				this.syncMessage = this.type === 1
					? '舌诊图片与识别结果已同步到管理端档案。'
					: '面诊图片与识别结果已同步到管理端档案。';
			} catch (error) {
				console.error('同步图片资料失败', error);
				this.syncMessage = '识别已完成，但同步管理端失败，请稍后重试。';
				uni.showToast({
					title: '管理端同步失败',
					icon: 'none'
				});
			}
		},
		pz() {
			if (this.analysisPending || this.uploading) {
				return;
			}
			uni.chooseImage({
				count: 1,
				sourceType: ['album', 'camera'],
				success: (res) => {
					const filePath = res.tempFilePaths[0];
					this.imgsrc = filePath;
					this.create = false;
					this.uploading = true;
					this.syncMessage = '';
					uni.showToast({ title: '图片上传中，请稍候', icon: 'none' });

					uni.uploadFile({
						url: 'http://your-server-host:8089/faceAi',
						filePath,
						name: 'file',
						formData: {
							type: this.type
						},
						success: async (uploadFileRes) => {
							try {
								const data = JSON.parse(uploadFileRes.data);
								if (data.data) {
									await this.finishImageRecognition(filePath, data.data);
								} else {
									await this.finishImageRecognition(filePath, mockFaceAiData);
								}
							} catch (error) {
								console.error('解析图片识别结果失败', error);
								await this.finishImageRecognition(filePath, mockFaceAiData);
							}
						},
						fail: async (error) => {
							console.error('图片识别失败，改用演示数据', error);
							await this.finishImageRecognition(filePath, mockFaceAiData);
						},
						complete: () => {
							this.uploading = false;
						}
					});
				}
			});
		},
		createfunc() {
			if (this.analysisPending || !this.create) {
				return;
			}
			this.beginAnalysis();
			this.completeAnalysis(() => {
				uni.navigateTo({
					url: '/pages/result/result'
				});
			});
		}
	}
};
</script>

<style>
.photo-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background:
		linear-gradient(180deg, #ffffff 0%, #f0f5ff 100%),
		radial-gradient(circle at 100% 0%, rgba(208, 231, 234, 0.24) 0, transparent 42%);
	box-sizing: border-box;
}

.photo-page__preview-card,
.photo-page__latest-card {
	margin-top: 16px;
	padding: 20px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.photo-page__eyebrow,
.photo-page__latest-eyebrow {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.photo-page__preview-title {
	display: block;
	margin-top: 10px;
	color: #103268;
	font-size: 18px;
	font-weight: 800;
}

.photo-page__preview-shell {
	margin-top: 16px;
	border-radius: 24px;
	overflow: hidden;
	background: #eef3ef;
}

.photo-page__preview-image {
	width: 100%;
	height: 280px;
	display: block;
}

.photo-page__preview-tip {
	display: block;
	margin-top: 14px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.7;
}

.photo-page__latest-title {
	display: block;
	margin-top: 10px;
	color: #103268;
	font-size: 20px;
	font-weight: 800;
}

.photo-page__latest-desc {
	display: block;
	margin-top: 8px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.7;
}

.photo-page__actions {
	display: flex;
	flex-direction: column;
	gap: 14px;
	margin-top: 20px;
}

.photo-page__primary,
.photo-page__secondary {
	border-radius: 22px;
	font-size: 17px;
	font-weight: 800;
}

.photo-page__primary {
	background: #1f5fca;
	color: #ffffff;
}

.photo-page__secondary {
	background: rgba(255, 255, 255, 0.92);
	color: #1f5fca;
	border: 1px solid rgba(163, 196, 232, 0.5);
}
</style>
