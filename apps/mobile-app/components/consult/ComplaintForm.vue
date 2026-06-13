<template>
	<view class="consult-card">
		<view class="consult-card__header">
			<text class="consult-card__eyebrow">CHIEF COMPLAINT</text>
			<text class="consult-card__title">补充说明 / 主诉</text>
		</view>

		<view class="field">
			<text class="label">主要不适描述</text>
			<textarea
				class="textarea"
				v-model="localValue.description"
				placeholder="请用自己的话描述最想解决的问题，例如：右手臂疼痛两周，活动时加重"
			/>
		</view>

		<view class="field">
			<text class="label">出现多久了</text>
			<input class="input" v-model="localValue.duration" placeholder="如今天、3天、1周、1个月、半年以上" />
		</view>

		<view class="field">
			<text class="label">严重程度：{{ localValue.severity || 0 }} 分</text>
			<slider
				:value="Number(localValue.severity) || 0"
				min="0"
				max="10"
				step="1"
				activeColor="#1f5fca"
				backgroundColor="#dbe5df"
				@change="localValue.severity = $event.detail.value"
			/>
		</view>

		<view class="field">
			<text class="label">是否突然加重</text>
			<radio-group class="choice-row" @change="localValue.suddenWorse = $event.detail.value">
				<label v-for="item in suddenOptions" :key="item" :class="['choice-chip', { 'choice-chip--active': localValue.suddenWorse === item }]">
					<radio class="choice-radio" :value="item" :checked="localValue.suddenWorse === item" />
					<text>{{ item }}</text>
				</label>
			</radio-group>
		</view>

		<view class="field field--last">
			<text class="label">是否影响生活</text>
			<checkbox-group class="choice-row" @change="localValue.lifeImpact = $event.detail.value">
				<label v-for="item in impactOptions" :key="item" :class="['choice-chip', { 'choice-chip--active': localValue.lifeImpact.includes(item) }]">
					<checkbox class="choice-check" :value="item" :checked="localValue.lifeImpact.includes(item)" />
					<text>{{ item }}</text>
				</label>
			</checkbox-group>
		</view>
	</view>
</template>

<script>
export default {
	name: 'ComplaintForm',
	props: {
		modelValue: {
			type: Object,
			default: () => ({})
		}
	},
	emits: ['update:modelValue'],
	data() {
		return {
			suddenOptions: ['是', '否', '不清楚'],
			impactOptions: ['睡眠', '走路', '工作/学习', '饮食', '情绪', '其他'],
			localValue: {
				description: '',
				duration: '',
				severity: 0,
				suddenWorse: '',
				lifeImpact: [],
				...this.modelValue
			}
		}
	},
	watch: {
		modelValue: {
			deep: true,
			handler(value) {
				this.localValue = {
					...this.localValue,
					...(value || {})
				}
			}
		},
		localValue: {
			deep: true,
			handler(value) {
				this.$emit('update:modelValue', { ...value })
			}
		}
	}
}
</script>

<style scoped>
.consult-card {
	padding: 22px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.86);
	box-shadow: 0 18px 40px rgba(31, 95, 202, 0.06);
}

.consult-card__header {
	display: flex;
	flex-direction: column;
	margin-bottom: 18px;
}

.consult-card__eyebrow {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.consult-card__title {
	margin-top: 8px;
	color: #103268;
	font-size: 24px;
	font-weight: 800;
}

.field {
	margin-bottom: 18px;
}

.field--last {
	margin-bottom: 0;
}

.label {
	display: block;
	margin-bottom: 10px;
	color: #103268;
	font-size: 15px;
	font-weight: 700;
}

.input,
.textarea {
	width: 100%;
	border: 1px solid rgba(163, 196, 232, 0.42);
	border-radius: 18px;
	background: #f0f5ff;
	color: #103268;
	font-size: 15px;
	box-sizing: border-box;
}

.input {
	height: 48px;
	padding: 0 14px;
}

.textarea {
	min-height: 126px;
	padding: 12px 14px;
}

.choice-row {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
}

.choice-chip {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	min-height: 38px;
	padding: 0 14px;
	border: 1px solid rgba(163, 196, 232, 0.4);
	border-radius: 999px;
	background: #f7faf8;
	color: #5a7a99;
	font-size: 14px;
}

.choice-chip--active {
	border-color: rgba(31, 95, 202, 0.18);
	background: rgba(163, 196, 232, 0.4);
	color: #1f5fca;
}

.choice-radio,
.choice-check {
	transform: scale(0.8);
}
</style>
