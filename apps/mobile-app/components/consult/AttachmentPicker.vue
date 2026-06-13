<template>
	<view class="attachment-card">
		<view class="tip">以下资料为可选上传，有助于医生更好了解情况。不上传也可以继续下一步。</view>
		<view class="type-grid">
			<view class="type-item" v-for="item in imageTypes" :key="item.type" @tap="chooseImage(item)">
				<view class="type-icon">{{ item.short }}</view>
				<view class="type-name">{{ item.label }}</view>
			</view>
			<view class="type-item" @tap="chooseFile">
				<view class="type-icon">附</view>
				<view class="type-name">其他附件</view>
			</view>
		</view>
		<view class="voice-box">
			<view class="voice-main">
				<view class="voice-title">语音描述</view>
				<view class="voice-subtitle">{{ isRecording ? '录音中，松开/点击停止' : '可录一段补充说明' }}</view>
			</view>
			<button class="voice-button" @tap="toggleRecord">{{ isRecording ? '停止' : '录音' }}</button>
		</view>
		<view class="attachment-list" v-if="localValue.length">
			<view class="attachment-item" v-for="item in localValue" :key="item.id">
				<image v-if="item.kind === 'image'" class="thumb" :src="item.path" mode="aspectFill"></image>
				<view v-else class="file-icon">{{ item.kind === 'audio' ? '音' : '文' }}</view>
				<view class="attachment-info">
					<view class="attachment-name">{{ item.label }}</view>
					<view class="attachment-path">{{ item.name || item.path }}</view>
				</view>
				<button class="delete-btn" @tap="removeAttachment(item.id)">删除</button>
			</view>
		</view>
	</view>
</template>

<script>
import recorder from '../../common/recorder.js'

export default {
	name: 'AttachmentPicker',
	props: {
		modelValue: {
			type: Array,
			default: () => []
		}
	},
	emits: ['update:modelValue'],
	data() {
		return {
			isRecording: false,
			localValue: [...this.modelValue],
			imageTypes: [
				{ type: 'tonguePhoto', label: '舌象照片', short: '舌' },
				{ type: 'facePhoto', label: '面部照片', short: '面' },
				{ type: 'affectedPhoto', label: '患处照片', short: '患' },
				{ type: 'examImage', label: '检查报告图片', short: '检' }
			]
		}
	},
	watch: {
		localValue: {
			deep: true,
			handler(value) {
				this.$emit('update:modelValue', value)
			}
		}
	},
	methods: {
		addAttachment(attachment) {
			this.localValue = [
				...this.localValue,
				{
					id: `att_${Date.now()}_${Math.random().toString(16).slice(2)}`,
					...attachment
				}
			]
		},
		chooseImage(typeItem) {
			uni.chooseImage({
				count: 1,
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.addAttachment({
						type: typeItem.type,
						label: typeItem.label,
						kind: 'image',
						path: res.tempFilePaths[0],
						name: res.tempFiles?.[0]?.name || typeItem.label
					})
				}
			})
		},
		chooseFile() {
			if (typeof uni.chooseFile !== 'function') {
				uni.showToast({
					title: '当前端暂不支持文件选择，可选择检查报告图片',
					icon: 'none'
				})
				return
			}
			uni.chooseFile({
				count: 1,
				success: (res) => {
					const file = res.tempFiles?.[0] || {}
					this.addAttachment({
						type: 'other',
						label: '其他附件',
						kind: 'file',
						path: file.path || res.tempFilePaths?.[0] || '',
						name: file.name || '其他附件'
					})
				}
			})
		},
		toggleRecord() {
			if (this.isRecording) {
				recorder.stop()
				this.isRecording = false
				return
			}
			uni.showLoading({ title: '启动录音...', mask: false })
			recorder.start(
				{ duration: 60000, format: 'mp3', sampleRate: 16000 },
				() => {
					this.isRecording = true
					uni.hideLoading()
				},
				(res) => {
					this.isRecording = false
					uni.hideLoading()
					if (res.tempFilePath) {
						this.addAttachment({
							type: 'voice',
							label: '语音描述',
							kind: 'audio',
							path: res.tempFilePath,
							name: '语音描述'
						})
					}
				},
				(error) => {
					this.isRecording = false
					uni.hideLoading()
					uni.showToast({
						title: error.message || '录音失败，请检查权限',
						icon: 'none'
					})
				}
			)
		},
		removeAttachment(id) {
			this.localValue = this.localValue.filter((item) => item.id !== id)
		}
	}
}
</script>

<style scoped>
.attachment-card {
	background-color: #ffffff;
	border-radius: 12px;
	padding: 18px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.tip {
	padding: 12px;
	border-radius: 10px;
	background-color: #f5f9ff;
	color: #475467;
	font-size: 14px;
	line-height: 1.5;
	margin-bottom: 16px;
}

.type-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 12px;
}

.type-item {
	display: flex;
	align-items: center;
	padding: 14px;
	border-radius: 12px;
	background-color: #f8fbff;
	border: 1px solid #e6f0ff;
}

.type-icon,
.file-icon {
	width: 38px;
	height: 38px;
	border-radius: 12px;
	background: linear-gradient(to right, #007aff, #00bfff);
	color: #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: bold;
	margin-right: 10px;
	flex-shrink: 0;
}

.type-name {
	font-size: 14px;
	color: #1f2d3d;
}

.voice-box {
	margin-top: 14px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px;
	border-radius: 12px;
	background-color: #fffaf0;
}

.voice-title {
	font-size: 15px;
	font-weight: 700;
	color: #1f2d3d;
}

.voice-subtitle {
	font-size: 12px;
	color: #667085;
	margin-top: 4px;
}

.voice-button {
	margin: 0;
	padding: 0 18px;
	height: 36px;
	line-height: 36px;
	border-radius: 999px;
	background: linear-gradient(to right, #ff9500, #ffb340);
	color: #ffffff;
	font-size: 14px;
}

.attachment-list {
	margin-top: 16px;
}

.attachment-item {
	display: flex;
	align-items: center;
	padding: 12px 0;
	border-bottom: 1px solid #eef2f7;
}

.thumb {
	width: 48px;
	height: 48px;
	border-radius: 8px;
	margin-right: 10px;
	flex-shrink: 0;
}

.attachment-info {
	flex: 1;
	min-width: 0;
}

.attachment-name {
	font-size: 14px;
	font-weight: 600;
	color: #1f2d3d;
}

.attachment-path {
	font-size: 12px;
	color: #98a2b3;
	margin-top: 4px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.delete-btn {
	margin: 0;
	padding: 0 10px;
	height: 30px;
	line-height: 30px;
	border-radius: 999px;
	background-color: #fff1f0;
	color: #d92d20;
	font-size: 12px;
}
</style>
