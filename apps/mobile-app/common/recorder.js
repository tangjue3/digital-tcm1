/**
 * 录音工具类
 * 兼容 H5（浏览器）和 App 端（真机/模拟器）
 */

class Recorder {
	constructor() {
		this.isRecording = false;
		this.audioContext = null;
		this.mediaStream = null;
		this.mediaRecorder = null;
		this.audioChunks = [];
		this.audioBlob = null;
		this.audioUrl = null;
		this.recorderManager = null;

		// 初始化 App 端录音管理器
		// #ifdef APP-PLUS || MP-WEIXIN
		this.recorderManager = uni.getRecorderManager();
		// #endif
	}

	/**
	 * 检测当前平台
	 */
	getPlatform() {
		// #ifdef H5
		return 'h5';
		// #endif
		// #ifdef APP-PLUS
		return 'app';
		// #endif
		// #ifdef MP-WEIXIN
		return 'mp-weixin';
		// #endif
		return 'unknown';
	}

	/**
	 * 检查录音权限
	 */
	async checkPermission() {
		const platform = this.getPlatform();

		if (platform === 'h5') {
			// H5 端检查浏览器是否支持录音
			if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
				throw new Error('当前浏览器不支持录音功能，请使用 Chrome、Firefox 或 Edge 浏览器');
			}
			return true;
		} else {
			// App 端检查权限
			return new Promise((resolve, reject) => {
				// #ifdef APP-PLUS
				plus.android.requestPermissions(
					['android.permission.RECORD_AUDIO'],
					(result) => {
						if (result.granted && result.granted.length > 0) {
							resolve(true);
						} else {
							reject(new Error('录音权限被拒绝，请在设置中开启'));
						}
					},
					(err) => {
						reject(new Error('请求录音权限失败'));
					}
				);
				// #endif
				// #ifdef MP-WEIXIN
				uni.authorize({
					scope: 'scope.record',
					success: () => resolve(true),
					fail: () => reject(new Error('录音权限被拒绝'))
				});
				// #endif
			});
		}
	}

	/**
	 * 开始录音
	 * @param {Object} options 录音配置
	 * @param {Function} onStart 开始录音回调
	 * @param {Function} onStop 停止录音回调
	 * @param {Function} onError 错误回调
	 */
	async start(options = {}, onStart, onStop, onError) {
		if (this.isRecording) {
			console.log('正在录音中...');
			return;
		}

		const platform = this.getPlatform();
		console.log('当前平台:', platform);

		try {
			await this.checkPermission();
		} catch (err) {
			if (onError) onError(err);
			return;
		}

		this.isRecording = true;
		this.onStopCallback = onStop;
		this.onErrorCallback = onError;

		if (platform === 'h5') {
			await this.startH5(options, onStart, onStop, onError);
		} else {
			this.startApp(options, onStart, onStop, onError);
		}
	}

	/**
	 * H5 端开始录音
	 */
	async startH5(options, onStart, onStop, onError) {
		try {
			// 获取麦克风权限
			this.mediaStream = await navigator.mediaDevices.getUserMedia({
				audio: {
					echoCancellation: true,
					noiseSuppression: true,
					sampleRate: 16000
				}
			});

			// 创建 MediaRecorder
			const mimeType = MediaRecorder.isTypeSupported('audio/webm') ? 'audio/webm' : 'audio/mp4';
			this.mediaRecorder = new MediaRecorder(this.mediaStream, {
				mimeType: mimeType
			});

			this.audioChunks = [];

			this.mediaRecorder.ondataavailable = (event) => {
				if (event.data.size > 0) {
					this.audioChunks.push(event.data);
				}
			};

			this.mediaRecorder.onstop = () => {
				this.audioBlob = new Blob(this.audioChunks, { type: mimeType });
				this.audioUrl = URL.createObjectURL(this.audioBlob);

				console.log('H5 录音完成', this.audioUrl);

				if (onStop) {
					onStop({
						tempFilePath: this.audioUrl,
						blob: this.audioBlob,
						duration: 0 // H5 端无法获取精确时长
					});
				}
			};

			this.mediaRecorder.onerror = (err) => {
				console.error('MediaRecorder 错误:', err);
				this.isRecording = false;
				if (onError) onError(err);
			};

			// 开始录音
			this.mediaRecorder.start(100);
			console.log('H5 开始录音');
			if (onStart) onStart();

		} catch (err) {
			console.error('H5 录音启动失败:', err);
			this.isRecording = false;
			if (onError) onError(err);
		}
	}

	/**
	 * App 端开始录音
	 */
	startApp(options, onStart, onStop, onError) {
		// #ifdef APP-PLUS || MP-WEIXIN
		if (!this.recorderManager) {
			this.recorderManager = uni.getRecorderManager();
		}

		// 设置录音停止回调
		this.recorderManager.onStop((res) => {
			console.log('App 录音完成', res);
			this.isRecording = false;
			if (onStop) onStop(res);
		});

		// 设置录音错误回调
		this.recorderManager.onError((err) => {
			console.error('App 录音错误:', err);
			this.isRecording = false;
			if (onError) onError(err);
		});

		// 开始录音
		this.recorderManager.start({
			duration: options.duration || 60000,
			sampleRate: options.sampleRate || 16000,
			numberOfChannels: 1,
			encodeBitRate: options.encodeBitRate || 16000,
			format: options.format || 'mp3'
		});

		console.log('App 开始录音');
		if (onStart) onStart();
		// #endif
	}

	/**
	 * 停止录音
	 */
	stop() {
		if (!this.isRecording) {
			return;
		}

		const platform = this.getPlatform();

		if (platform === 'h5') {
			// H5 端停止录音
			if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
				this.mediaRecorder.stop();
			}
			if (this.mediaStream) {
				this.mediaStream.getTracks().forEach(track => track.stop());
				this.mediaStream = null;
			}
		} else {
			// App 端停止录音
			// #ifdef APP-PLUS || MP-WEIXIN
			if (this.recorderManager) {
				this.recorderManager.stop();
			}
			// #endif
		}

		this.isRecording = false;
	}

	/**
	 * 播放录音
	 */
	play(audioPath, onError) {
		const platform = this.getPlatform();

		if (platform === 'h5') {
			// H5 端播放
			if (this.audioUrl || audioPath) {
				const audio = new Audio(this.audioUrl || audioPath);
				audio.onerror = (err) => {
					console.error('播放错误:', err);
					if (onError) onError(err);
				};
				audio.play();
				return audio;
			}
		} else {
			// App 端播放
			// #ifdef APP-PLUS || MP-WEIXIN
			const innerAudioContext = uni.createInnerAudioContext();
			innerAudioContext.src = audioPath;
			innerAudioContext.onerror = (err) => {
				console.error('播放错误:', err);
				if (onError) onError(err);
			};
			innerAudioContext.play();
			return innerAudioContext;
			// #endif
		}

		return null;
	}

	/**
	 * 获取录音文件（用于上传）
	 */
	getAudioFile() {
		if (this.audioBlob) {
			return {
				blob: this.audioBlob,
				url: this.audioUrl,
				file: new File([this.audioBlob], 'recording.webm', { type: this.audioBlob.type })
			};
		}
		return null;
	}

	/**
	 * 清理资源
	 */
	destroy() {
		this.stop();
		if (this.audioUrl) {
			URL.revokeObjectURL(this.audioUrl);
		}
		this.audioChunks = [];
		this.audioBlob = null;
		this.audioUrl = null;
	}
}

// 导出单例
export default new Recorder();
