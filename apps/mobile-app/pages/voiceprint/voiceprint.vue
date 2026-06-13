<template>
	<view class="voiceprint-page">
		<TaskTopBar title="声纹自测" :show-right="false" @back="goBack" />

		<DetectionGuideCard
			title="朗读采样"
			text="请在安静环境中朗读提示短句，按住按钮时尽量保持语速平稳、发音自然。"
			marker="声"
		/>

		<view class="voiceprint-page__prompt-card">
			<text class="voiceprint-page__eyebrow">VOICE SAMPLE</text>
			<text class="voiceprint-page__prompt-title">请均匀地读出以下短句</text>
			<text class="voiceprint-page__prompt-text">{{ textlist[index] }}</text>
			<text class="voiceprint-page__platform">{{ platformTip }}</text>
		</view>

		<DetectionStatusPanel
			:title="statusTitle"
			:description="statusDescription"
			:core-text="statusCoreText"
			:show-progress="isRecording || analysisPending || uploading"
		/>

		<view class="voiceprint-page__record-card">
			<view
				class="voiceprint-page__record-button"
				:class="{ 'voiceprint-page__record-button--active': isRecording }"
				@touchstart="onTouchStart"
				@touchend="onTouchEnd"
				@mousedown="onMouseDown"
				@mouseup="onMouseUp"
				@mouseleave="onMouseLeave"
			>
				<text class="voiceprint-page__record-text">{{ isRecording ? '松开结束' : '按住录音' }}</text>
			</view>

			<text class="voiceprint-page__record-tip">
				{{ voicePath ? '录音已完成，可播放试听或直接生成报告。' : '按住圆形按钮开始采样。' }}
			</text>
		</view>

		<view v-if="latestReportName" class="voiceprint-page__latest-card">
			<text class="voiceprint-page__latest-eyebrow">上次报告</text>
			<text class="voiceprint-page__latest-title">{{ latestReportName }}</text>
			<text class="voiceprint-page__latest-desc">{{ latestReportTime }}</text>
		</view>

		<view class="voiceprint-page__actions">
			<button class="voiceprint-page__primary" :disabled="analysisPending || uploading || !create" @tap="createfunc">
				{{ analysisPending ? '分析中...' : '生成报告' }}
			</button>
			<button class="voiceprint-page__secondary" :disabled="!voicePath || analysisPending" @tap="playVoice">播放录音</button>
		</view>
	</view>
</template>

<script>
import recorder from '../../common/recorder.js';
import { getAppUserProfile, saveAppUserProfile, uploadCommonFile } from '../../common/api.js';
import DetectionGuideCard from '../../components/premium/DetectionGuideCard.vue';
import DetectionStatusPanel from '../../components/premium/DetectionStatusPanel.vue';
import TaskTopBar from '../../components/premium/TaskTopBar.vue';

const VOICE_REPORT_STORAGE_KEY = 'voiceReportData';

const mockVoiceData = {
	code: 20000,
	success: true,
	msg: '成功',
	data: {
		answer_type: 2,
		report: {
			syndrome_name: '气虚证',
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
					commodity_name: '补中益气丸 9g*10丸',
					commodity_img_path: 'https://img.alicdn.com/imgextra/i4/2200724907121/O1CN01.jpg',
					commodity_shopping_link: 'packages/goods/detail/index?alias=xxx'
				},
				{
					commodity_name: '黄芪口服液 10ml*10支',
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
			textlist: [
				'春夏养阳，秋冬养阴。',
				'起不在鸡鸣前，晚起不在日出后。',
				'五谷为养，五果为助，五畜为益，五菜为充。',
				'饱食即卧，乃生百病。',
				'养生之道，莫先于饮食。',
				'精、气、神，是养生家所说的三宝。'
			],
			index: 0,
			create: false,
			analysisPending: false,
			analysisReadyAt: 0,
			voicePath: '',
			isRecording: false,
			platformTip: '',
			innerAudioContext: null,
			isMouseDown: false,
			uploading: false,
			latestReport: null,
			syncMessage: ''
		};
	},
	computed: {
		statusTitle() {
			if (this.analysisPending) return '正在生成声纹报告';
			if (this.uploading) return '正在识别录音内容';
			if (this.isRecording) return '正在采集声纹';
			if (this.create) return '录音已就绪';
			if (this.voicePath) return '等待识别完成';
			return '准备开始录音';
		},
		statusDescription() {
			if (this.analysisPending) return '系统正在整理声纹特征并生成调理建议，请稍候。';
			if (this.uploading) return '录音已保存，系统正在上传并识别声纹信息。';
			if (this.syncMessage) return this.syncMessage;
			if (this.isRecording) return '保持正常语速和音量，录满一句后松开即可。';
			if (this.create) return '采样已完成，可以先试听，也可以直接生成报告。';
			if (this.voicePath) return '录音已结束，正在等待识别返回结果。';
			return '按住下方按钮开始录音，读完一遍提示短句即可。';
		},
		statusCoreText() {
			if (this.analysisPending) return '析';
			if (this.uploading) return '传';
			if (this.isRecording) return '录';
			if (this.create) return '声';
			if (this.voicePath) return '待';
			return '声';
		},
		latestReportName() {
			return this.latestReport?.data?.report?.syndrome_name || '';
		},
		latestReportTime() {
			const generatedAt = this.latestReport?.meta?.generatedAt;
			if (!generatedAt) return '最近一次已保存到本地';
			const date = new Date(generatedAt);
			const month = `${date.getMonth() + 1}`.padStart(2, '0');
			const day = `${date.getDate()}`.padStart(2, '0');
			const hour = `${date.getHours()}`.padStart(2, '0');
			const minute = `${date.getMinutes()}`.padStart(2, '0');
			return `${month}-${day} ${hour}:${minute}`;
		}
	},
	onLoad() {
		this.index = Math.floor(Math.random() * this.textlist.length);
		this.checkPlatform();
		this.loadLatestReport();
		// #ifdef H5
		document.addEventListener('mouseup', this.globalMouseUp);
		// #endif
	},
	onUnload() {
		// #ifdef H5
		document.removeEventListener('mouseup', this.globalMouseUp);
		// #endif
		if (this.innerAudioContext) {
			this.innerAudioContext.destroy();
			this.innerAudioContext = null;
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
		loadLatestReport() {
			try {
				const cached = uni.getStorageSync(VOICE_REPORT_STORAGE_KEY);
				this.latestReport = cached ? JSON.parse(cached) : null;
			} catch (error) {
				console.error('读取声纹报告失败', error);
				this.latestReport = null;
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
		persistVoiceReport(payload, filePath, isMock = false) {
			const reportPayload = {
				...(payload || mockVoiceData),
				meta: {
					reportNo: `SV${Date.now()}`,
					generatedAt: Date.now(),
					sourceText: this.textlist[this.index],
					voicePath: filePath || '',
					isMock
				}
			};
			uni.setStorageSync(VOICE_REPORT_STORAGE_KEY, JSON.stringify(reportPayload));
			this.latestReport = reportPayload;
		},
		async syncVoiceProfile(filePath) {
			if (!uni.getStorageSync('loginType') || !filePath) {
				return;
			}

			const guide = this.getStoredGuideData();
			const profile = await getAppUserProfile().catch(() => null);
			const remark = this.parseProfileRemark(profile);
			const voiceFile = await uploadCommonFile(filePath, {
				defaultName: 'voice',
				contentType: 'audio/webm'
			});

			await saveAppUserProfile({
				sex: profile?.sex || guide.gender || '',
				age: profile?.age ?? this.toNumberOrNull(guide.age),
				voiceFile,
				tongueImage: profile?.tongueImage || guide.tonguePhoto || '',
				faceImage: profile?.faceImage || guide.facePhoto || '',
				remark: JSON.stringify({
					weight: remark.weight || guide.weight || '',
					height: remark.height || guide.height || '',
					pulseRate: remark.pulseRate || guide.pulseRate || '',
					medicalHistory: remark.medicalHistory || guide.medicalHistory || []
				})
			});

			const nextGuide = {
				...guide,
				audioPath: voiceFile || filePath
			};
			uni.setStorageSync('userGuide', JSON.stringify(nextGuide));
			uni.setStorageSync('hasCompletedGuide', true);
		},
		openVoiceReport() {
			uni.navigateTo({
				url: '/pages/voiceRes/voiceRes'
			});
		},
		globalMouseUp() {
			if (this.isMouseDown && this.isRecording) {
				this.isMouseDown = false;
				this.endRecord();
			}
		},
		onTouchStart(event) {
			event.preventDefault();
			this.startRecord();
		},
		onTouchEnd(event) {
			event.preventDefault();
			this.endRecord();
		},
		onMouseDown(event) {
			event.preventDefault();
			this.isMouseDown = true;
			this.startRecord();
		},
		onMouseUp(event) {
			event.preventDefault();
			this.isMouseDown = false;
			this.endRecord();
		},
		onMouseLeave() {
			if (this.isMouseDown && this.isRecording) {
				this.isMouseDown = false;
				this.endRecord();
			}
		},
		checkPlatform() {
			// #ifdef H5
			if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
				this.platformTip = '网页端录音，请允许使用麦克风。';
			} else {
				this.platformTip = '当前浏览器录音支持较弱，建议使用最新版 Chrome。';
			}
			// #endif
			// #ifdef APP-PLUS
			this.platformTip = 'App 端将直接调用系统录音权限。';
			// #endif
			// #ifdef MP-WEIXIN
			this.platformTip = '微信小程序端录音，请确保已授权麦克风。';
			// #endif
		},
		async finishVoiceRecognition(filePath, payload, isMock) {
			this.persistVoiceReport(payload, filePath, isMock);
			this.create = true;
			try {
				await this.syncVoiceProfile(filePath);
				this.syncMessage = '录音与识别结果已同步到管理端档案。';
			} catch (error) {
				console.error('同步声纹资料失败', error);
				this.syncMessage = '识别已完成，但同步管理端失败，请稍后重试。';
				uni.showToast({
					title: '管理端同步失败',
					icon: 'none'
				});
			}
		},
		uploadVoice(filePath) {
			this.uploading = true;
			this.syncMessage = '';
			// #ifdef H5
			setTimeout(async () => {
				await this.finishVoiceRecognition(filePath, mockVoiceData, true);
				this.uploading = false;
			}, 1500);
			// #endif

			// #ifdef APP-PLUS || MP-WEIXIN
			uni.uploadFile({
				url: 'http://your-server-host:8089/iseC',
				filePath,
				name: 'file',
				formData: {
					text: this.textlist[this.index]
				},
				success: async (uploadFileRes) => {
					try {
						const data = JSON.parse(uploadFileRes.data);
						if (data.data) {
							await this.finishVoiceRecognition(filePath, data, false);
						} else {
							await this.finishVoiceRecognition(filePath, mockVoiceData, true);
						}
					} catch (error) {
						console.error('解析声纹响应失败', error);
						await this.finishVoiceRecognition(filePath, mockVoiceData, true);
					}
				},
				fail: async (error) => {
					console.error('上传录音失败，改用演示数据', error);
					await this.finishVoiceRecognition(filePath, mockVoiceData, true);
				},
				complete: () => {
					this.uploading = false;
				}
			});
			// #endif
		},
		startRecord() {
			if (this.isRecording || this.analysisPending || this.uploading) {
				return;
			}

			uni.showLoading({
				title: '启动录音...',
				mask: true
			});

			recorder.start(
				{
					duration: 30000,
					format: 'mp3',
					sampleRate: 16000
				},
				() => {
					this.isRecording = true;
					this.create = false;
					this.voicePath = '';
					this.syncMessage = '';
					uni.hideLoading();
					uni.showLoading({
						title: '录音中...',
						mask: true
					});
				},
				(res) => {
					this.isRecording = false;
					uni.hideLoading();
					if (res.tempFilePath) {
						this.voicePath = res.tempFilePath;
						uni.showToast({
							title: '录音完成',
							icon: 'success'
						});
						this.uploadVoice(res.tempFilePath);
					}
				},
				(error) => {
					console.error('录音失败', error);
					this.isRecording = false;
					this.uploading = false;
					uni.hideLoading();
					uni.showModal({
						title: '录音失败',
						content: error.message || '请检查麦克风权限后重试。',
						showCancel: false
					});
				}
			);
		},
		endRecord() {
			if (!this.isRecording) {
				return;
			}
			recorder.stop();
			this.isRecording = false;
			uni.hideLoading();
		},
		playVoice() {
			if (!this.voicePath) {
				uni.showToast({
					title: '请先完成录音',
					icon: 'none'
				});
				return;
			}

			// #ifdef H5
			const audio = new Audio(this.voicePath);
			audio.play().catch(() => {
				uni.showToast({
					title: '播放失败',
					icon: 'none'
				});
			});
			// #endif

			// #ifdef APP-PLUS || MP-WEIXIN
			if (!this.innerAudioContext) {
				this.innerAudioContext = uni.createInnerAudioContext();
			}
			this.innerAudioContext.src = this.voicePath;
			this.innerAudioContext.play();
			// #endif
		},
		createfunc() {
			if (this.analysisPending || !this.create) {
				return;
			}
			this.beginAnalysis();
			this.completeAnalysis(() => {
				this.openVoiceReport();
			});
		}
	}
};
</script>

<style>
.voiceprint-page {
	min-height: 100vh;
	padding: 18px 18px 28px;
	background:
		linear-gradient(180deg, #ffffff 0%, #f8faf7 100%),
		radial-gradient(circle at 100% 0%, rgba(208, 231, 234, 0.24) 0, transparent 42%);
	box-sizing: border-box;
}

.voiceprint-page__prompt-card,
.voiceprint-page__record-card,
.voiceprint-page__latest-card {
	margin-top: 16px;
	padding: 20px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 26px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 14px 32px rgba(31, 95, 202, 0.05);
}

.voiceprint-page__eyebrow,
.voiceprint-page__latest-eyebrow {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.voiceprint-page__prompt-title {
	display: block;
	margin-top: 10px;
	color: #103268;
	font-size: 18px;
	font-weight: 800;
}

.voiceprint-page__prompt-text {
	display: block;
	margin-top: 12px;
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
	line-height: 1.55;
}

.voiceprint-page__platform {
	display: block;
	margin-top: 14px;
	color: #7a8fa8;
	font-size: 13px;
	line-height: 1.6;
}

.voiceprint-page__record-card {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.voiceprint-page__record-button {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 132px;
	height: 132px;
	border-radius: 999px;
	background: linear-gradient(135deg, #1f5fca 0%, #3f8cff 100%);
	box-shadow: 0 20px 40px rgba(31, 95, 202, 0.18);
}

.voiceprint-page__record-button--active {
	background: linear-gradient(135deg, #9a4d4d 0%, #8a2e2e 100%);
	transform: scale(1.04);
}

.voiceprint-page__record-text {
	color: #ffffff;
	font-size: 17px;
	font-weight: 800;
}

.voiceprint-page__record-tip {
	margin-top: 16px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.7;
	text-align: center;
}

.voiceprint-page__latest-title {
	display: block;
	margin-top: 10px;
	color: #103268;
	font-size: 20px;
	font-weight: 800;
}

.voiceprint-page__latest-desc {
	display: block;
	margin-top: 8px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.6;
}

.voiceprint-page__actions {
	display: flex;
	flex-direction: column;
	gap: 14px;
	margin-top: 20px;
}

.voiceprint-page__primary,
.voiceprint-page__secondary {
	border-radius: 22px;
	font-size: 17px;
	font-weight: 800;
}

.voiceprint-page__primary {
	background: #1f5fca;
	color: #ffffff;
}

.voiceprint-page__secondary {
	background: rgba(255, 255, 255, 0.92);
	color: #1f5fca;
	border: 1px solid rgba(163, 196, 232, 0.5);
}
</style>
