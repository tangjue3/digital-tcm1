<template>
    <view class="guide-container">
		<view class="title">
			本页面采集的信息，只做本程序健康数据分析使用，我们会严格保密，请放心填写。
		</view>
        <!-- 基本信息步骤 -->
        <view class="step" v-if="step === 1">
            <view class="section-title">请填写您的基本信息</view>
            <view class="input-group">
                <text>性别</text>
                <radio-group @change="onGenderChange">
                    <label>
                        <radio value="男" /> 男
                    </label>
                    <label>
                        <radio value="女" /> 女
                    </label>
                </radio-group>
            </view>
            <view class="input-group">
                <text>年龄</text>
                <input type="number" placeholder="请输入年龄" v-model="age" />
            </view>
            <view class="input-group">
                <text>体重（kg）</text>
                <input type="number" placeholder="请输入体重" v-model="weight" />
            </view>
            <view class="input-group">
                <text>身高（cm）</text>
                <input type="number" placeholder="请输入身高" v-model="height" />
            </view>
            <view class="input-group">
                <text>过往病史</text>
                <checkbox-group @change="onMedicalHistoryChange">
                    <label>
                        <checkbox value="高血压" /> 高血压
                    </label>
                    <label>
                        <checkbox value="糖尿病" /> 糖尿病
                    </label>
                    <label>
                        <checkbox value="心脏病" /> 心脏病
                    </label>
                    <label>
                        <checkbox value="无" /> 无
                    </label>
                </checkbox-group>
            </view>
            <button :class="isBasicInfoValid?'action-button':'disabled-button'" @click="nextStep" :disabled="!isBasicInfoValid">下一步</button>
        </view>
		<!-- 音频录制步骤 -->
		<view class="step" v-if="step === 2">
		    <view class="section-title">请在安静的环境中，录制一段不会少于3秒的说话音频，便于分析您的呼吸疾病</view>
		    <view class="record-container">
				<view class="record-button"
					:class="isRecording ? 'recording' : ''"
					@touchstart="onTouchStart"
					@touchend="onTouchEnd"
					@mousedown="onMouseDown"
					@mouseup="onMouseUp"
					@mouseleave="onMouseLeave">
					{{ isRecording ? '松开停止' : '长按录音' }}
				</view>
				<view class="record-tip" v-if="isRecording">
					<text class="recording-indicator"></text>
					录音中...
				</view>
			</view>
			<view class="audio-status" v-if="audioPath">
				<text class="success-text">已录制完成</text>
			</view>
			<view class="platform-tip">
				<text>{{ platformTip }}</text>
			</view>
			<button class="action-button" @click="playVoice" :disabled="!audioPath">播放录音</button>
		    <button class="action-button" @click="prevStep" :disabled="step === 1">上一步</button>
			<button :class="audioPath?'action-button':'disabled-button'" @click="nextStep" :disabled="!audioPath">下一步</button>
		</view>
        <!-- 面相照片步骤 -->
        <view class="step" v-if="step === 3">
            <view class="section-title">请上传您的面部照片，有助于我们进一步辨别您的健康状况</view>
             <view @click="chooseFacePhoto"  class="imgbox">
                            <image :src="facePhoto || imgsrc" mode="aspectFit" class="preview-image"></image>
                        </view>
            <button class="action-button" @click="prevStep" :disabled="step === 1">上一步</button>
            <button :class="facePhoto?'action-button':'disabled-button'" @click="nextStep" :disabled="!facePhoto">下一步</button>
        </view>
        <!-- 舌苔照片步骤 -->
        <view class="step" v-if="step === 4">
            <view class="section-title">请上传您的舌苔照片，有助于我们进一步辨别您的健康状况</view>
            <view @click="chooseTonguePhoto" class="imgbox">
                           <image :src="tonguePhoto || imgsrc" mode="aspectFit" class="preview-image"></image>
                       </view>
            <button class="action-button" @click="prevStep" :disabled="step === 1">上一步</button>
			<button  :class="tonguePhoto?'action-button':'disabled-button'" @click="submit" :disabled="!tonguePhoto">提交</button>
        </view>

    </view>
</template>

<script>
import recorder from '../../common/recorder.js';
import { getAppUserProfile, uploadCommonFile, saveAppUserProfile } from '../../common/api.js';
import { profileToGuideData } from '../../common/auth.js';

export default {
    data() {
        return {
            step: 1,
            gender: '',
            age: '',
            weight: '',
            height: '',
            medicalHistory: [],
            facePhoto: '',
            tonguePhoto: '',
            isRecording: false,
            audioPath: '',
			imgsrc: "../../static/img/photo.png",
			platformTip: '',
			innerAudioContext: null,
			isMouseDown: false,
			startingRecord: false,
			lastTouchTime: 0
        };
    },
    computed: {
        isBasicInfoValid() {
            return this.gender && this.age && this.weight && this.height && this.medicalHistory.length > 0;
        }
    },
	onLoad(options = {}) {
		if (!uni.getStorageSync('loginType')) {
			uni.redirectTo({
				url: '/pages/Login/Login'
			});
			return;
		}
		if (options.force !== '1') {
			this.redirectIfProfileExists();
		}
		this.checkPlatform();
		// H5 端添加全局 mouseup 监听
		// #ifdef H5
		document.addEventListener('mouseup', this.globalMouseUp);
		// #endif
	},
	onUnload() {
		// 移除全局监听
		// #ifdef H5
		document.removeEventListener('mouseup', this.globalMouseUp);
		// #endif
	},
    methods: {
		async redirectIfProfileExists() {
			try {
				const profile = await getAppUserProfile();
				const guideData = profileToGuideData(profile);
				if (guideData) {
					uni.setStorageSync('userGuide', JSON.stringify(guideData));
					uni.setStorageSync('hasCompletedGuide', true);
					uni.switchTab({
						url: '/pages/health-plan/health-plan'
					});
				}
			} catch (error) {
				console.warn('[guide] profile check failed', error);
				if (!uni.getStorageSync('loginType')) {
					uni.redirectTo({
						url: '/pages/Login/Login'
					});
				}
			}
		},
		// 全局 mouseup 处理（H5 端）
		globalMouseUp(e) {
			if (this.isMouseDown && this.isRecording) {
				console.log('全局 mouseup 触发');
				this.isMouseDown = false;
				this.stopRecord();
			}
		},

		// 触摸开始
		onTouchStart(e) {
			console.log('touchstart');
			e.preventDefault();
			this.lastTouchTime = Date.now();
			this.startRecord();
		},

		// 触摸结束
		onTouchEnd(e) {
			console.log('touchend');
			e.preventDefault();
			this.stopRecord();
		},

		// 鼠标按下
		onMouseDown(e) {
			console.log('mousedown');
			if (Date.now() - this.lastTouchTime < 500) {
				return;
			}
			e.preventDefault();
			this.isMouseDown = true;
			this.startRecord();
		},

		// 鼠标松开
		onMouseUp(e) {
			console.log('mouseup');
			e.preventDefault();
			this.isMouseDown = false;
			this.stopRecord();
		},

		// 鼠标离开
		onMouseLeave(e) {
			console.log('mouseleave');
			if (this.isMouseDown && this.isRecording) {
				this.isMouseDown = false;
				this.stopRecord();
			}
		},

		// 检测平台
		checkPlatform() {
			// #ifdef H5
			this.platformTip = '当前为网页版，录音功能需要浏览器支持';
			// 检查浏览器是否支持录音
			if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
				this.platformTip = '当前为网页版，请允许使用麦克风';
			} else {
				this.platformTip = '当前浏览器不支持录音，建议使用 Chrome 浏览器';
			}
			// #endif
			// #ifdef APP-PLUS
			this.platformTip = '当前为 App 版本';
			// #endif
			// #ifdef MP-WEIXIN
			this.platformTip = '当前为微信小程序';
			// #endif
		},

        onGenderChange(e) {
            this.gender = e.detail.value;
        },
        onMedicalHistoryChange(e) {
            this.medicalHistory = e.detail.value;
        },
        nextStep() {
            this.step++;
        },
        prevStep() {
            this.step--;
        },
        skipStep() {
            this.step++;
        },
        chooseFacePhoto() {
            uni.chooseImage({
                count: 1,
                sourceType: ['album', 'camera'],
                success: (res) => {
                    this.facePhoto = res.tempFilePaths[0];
                }
            });
        },
        chooseTonguePhoto() {
            uni.chooseImage({
                count: 1,
                sourceType: ['album', 'camera'],
                success: (res) => {
                    this.tonguePhoto = res.tempFilePaths[0];
                }
            });
        },
		playVoice() {
			console.log('播放录音');
			if (this.audioPath) {
				// #ifdef H5
				// H5 端使用 Audio 对象播放
				const audio = new Audio(this.audioPath);
				audio.play().catch(err => {
					console.error('播放失败', err);
					uni.showToast({
						title: '播放失败',
						icon: 'none'
					});
				});
				// #endif
				// #ifdef APP-PLUS || MP-WEIXIN
				// App 端使用 uni 播放
				if (!this.innerAudioContext) {
					this.innerAudioContext = uni.createInnerAudioContext();
				}
				this.innerAudioContext.src = this.audioPath;
				this.innerAudioContext.play();
				// #endif
			} else {
				uni.showToast({
					title: '请先录音',
					icon: 'none'
				});
			}
		},
        startRecord() {
			// 防止重复触发
			if (this.isRecording || this.startingRecord) return;

			this.startingRecord = true;
			uni.showLoading({
				title: "启动录音...",
				mask: false
			});

			recorder.start({
				duration: 30000,
				format: "mp3",
				sampleRate: 16000
			},
			// 开始回调
			() => {
				this.isRecording = true;
				this.startingRecord = false;
				uni.hideLoading();
			},
			// 停止回调
			(res) => {
				console.log('录音完成', res);
				this.isRecording = false;
				this.startingRecord = false;
				uni.hideLoading();

				if (res.tempFilePath) {
					this.audioPath = res.tempFilePath;
					uni.showToast({
						title: '录音完成',
						icon: 'success'
					});
				}
			},
			// 错误回调
			(err) => {
				console.error('录音错误', err);
				this.isRecording = false;
				this.startingRecord = false;
				uni.hideLoading();
				uni.showModal({
					title: '录音失败',
					content: err.message || '请检查麦克风权限是否开启',
					showCancel: false
				});
			});
        },
        stopRecord() {
			if (!this.isRecording) return;
			recorder.stop();
			this.isRecording = false;
			uni.hideLoading();
        },
        async submit() {
            console.log('用户信息：', {
                gender: this.gender,
                age: this.age,
                weight: this.weight,
                height: this.height,
                medicalHistory: this.medicalHistory,
                facePhoto: this.facePhoto,
                tonguePhoto: this.tonguePhoto,
                audioPath: this.audioPath
            });
			uni.showLoading({
				title: '正在提交...',
				mask: true
			});
			try {
				const uploadCollectedFile = async (label, filePath, options) => {
					try {
						return await uploadCommonFile(filePath, options);
					} catch (error) {
						throw new Error(`${label}上传失败：${error.message || '文件格式不正确'}`);
					}
				};
				const [voiceFile, faceImage, tongueImage] = await Promise.all([
					uploadCollectedFile('声音文件', this.audioPath, { defaultName: 'voice', extension: 'webm', contentType: 'audio/webm' }),
					uploadCollectedFile('面部照片', this.facePhoto, { defaultName: 'face', extension: 'jpg', contentType: 'image/jpeg' }),
					uploadCollectedFile('舌苔照片', this.tonguePhoto, { defaultName: 'tongue', extension: 'jpg', contentType: 'image/jpeg' })
				]);
				const existingGuide = this.getStoredGuideData();
				const existingProfile = await getAppUserProfile().catch(() => null);
				const existingRemark = this.parseProfileRemark(existingProfile);
				const currentPulseRate = existingRemark.pulseRate || existingGuide.pulseRate || '';
				const guideData = {
					gender: this.gender,
					age: this.age,
					weight: this.weight,
					height: this.height,
					pulseRate: currentPulseRate,
					medicalHistory: this.medicalHistory,
					facePhoto: faceImage || this.facePhoto,
					tonguePhoto: tongueImage || this.tonguePhoto,
					audioPath: voiceFile || this.audioPath
				};
				await saveAppUserProfile({
					sex: this.gender,
					age: Number(this.age) || null,
					voiceFile,
					tongueImage,
					faceImage,
					remark: JSON.stringify({
						weight: this.weight,
						height: this.height,
						pulseRate: currentPulseRate,
						medicalHistory: this.medicalHistory
					})
				});
				uni.setStorageSync('userGuide', JSON.stringify(guideData));
				uni.setStorageSync('hasCompletedGuide', true);
				uni.showToast({
					title: '提交成功',
					icon: 'success'
				});
				uni.redirectTo({
					url: '/pages/report/report'
				});
			} catch (error) {
				console.error('保存采集资料失败', error);
				uni.showToast({
					title: error.message || '提交失败',
					icon: 'none'
				});
			} finally {
				uni.hideLoading();
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
		}
    }
};
</script>

<style>
.guide-container {
    padding: 20px;
    background-color: #f5f9ff;
    min-height: 100vh;
}

.step {
    background-color: #ffffff;
    border-radius: 10px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.imgbox {
	text-align: center;
}

.imgbox image {
	width: 100%;
}

.section-title {
    font-size: 18px;
    font-weight: bold;
    color: #007aff;
    margin-bottom: 15px;
}

.input-group {
    margin-bottom: 15px;
}

.input-group text {
    display: block;
    font-size: 16px;
    margin-bottom: 5px;
    color: #333;
}

.input-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-size: 16px;
	height: 50px;
}

.error-tip {
	color: #ff4d4f;
	font-size: 12px;
	margin-top: 6px;
}

.action-button {
    padding: 8px 16px;
    background: linear-gradient(to right, #007aff, #00bfff);
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
    margin-top: 15px;
    margin-right: 10px;
    transition: background 0.3s ease;
}

.disabled-button {
    background: linear-gradient(to right, #ccc, #ccc) !important;
	padding: 8px 16px;
	color: white;
	border: none;
	border-radius: 5px;
	font-size: 14px;
	cursor: pointer;
	margin-top: 15px;
	margin-right: 10px;
	transition: background 0.3s ease;
}

.action-button:hover {
    background: linear-gradient(to right, #0056b3, #0086b3);
}

.preview-image {
    width: 200px;
    height: 200px;
    margin-top: 15px;
    border-radius: 5px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.title {
	font-size: 18px;
	color: #666;
	margin-bottom: 15px;
}

/* 录音按钮样式 */
.record-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin: 20px 0;
}

.record-button {
	width: 120px;
	height: 120px;
	border-radius: 50%;
	background: linear-gradient(to right, #007aff, #00bfff);
	color: white;
	font-size: 16px;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	border: none;
	box-shadow: 0 4px 15px rgba(0, 122, 255, 0.4);
	transition: all 0.3s ease;
	cursor: pointer;
	user-select: none;
}

.record-button.recording {
	background: linear-gradient(to right, #ff3b30, #ff6b6b);
	box-shadow: 0 4px 15px rgba(255, 59, 48, 0.4);
	transform: scale(1.1);
}

.record-tip {
	display: flex;
	align-items: center;
	margin-top: 15px;
	color: #ff3b30;
	font-size: 14px;
}

.recording-indicator {
	display: inline-block;
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background-color: #ff3b30;
	margin-right: 8px;
	animation: pulse 1s infinite;
}

@keyframes pulse {
	0% {
		opacity: 1;
		transform: scale(1);
	}
	50% {
		opacity: 0.5;
		transform: scale(1.2);
	}
	100% {
		opacity: 1;
		transform: scale(1);
	}
}

.audio-status {
	text-align: center;
	margin: 10px 0;
}

.success-text {
	color: #34c759;
	font-size: 14px;
}

.platform-tip {
	text-align: center;
	margin: 10px 0;
	font-size: 12px;
	color: #999;
}
</style>
