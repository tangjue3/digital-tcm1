<template>
	<view class="consult-card">
		<view class="consult-card__header">
			<text class="consult-card__eyebrow">PATIENT PROFILE</text>
			<text class="consult-card__title">基础信息</text>
		</view>

		<view class="field">
			<text class="label label--required">姓名</text>
			<input class="input" :value="localValue.name" placeholder="请输入姓名" @input="onInput('name', $event)" />
		</view>

		<view class="field">
			<text class="label label--required">性别</text>
			<radio-group class="choice-row" @change="onRadioChange('gender', $event)">
				<label
					v-for="item in genderOptions"
					:key="item"
					:class="['choice-chip', { 'choice-chip--active': localValue.gender === item }]"
				>
					<radio class="choice-radio" :value="item" :checked="localValue.gender === item" />
					<text>{{ item }}</text>
				</label>
			</radio-group>
		</view>

		<view class="field">
			<text class="label label--required">年龄</text>
			<input class="input" :value="localValue.age" type="number" placeholder="请输入0-120之间的年龄" @input="onInput('age', $event)" />
		</view>

		<view class="field">
			<text class="label">基础病</text>
			<input class="input" :value="localValue.chronicDisease" placeholder="如高血压、糖尿病、心脏病等，可不填" @input="onInput('chronicDisease', $event)" />
		</view>

		<view class="field">
			<text class="label">药物过敏史</text>
			<radio-group class="choice-row" @change="onConditionalChange('allergyStatus', 'allergyDetail', $event)">
				<label
					v-for="item in threeOptions"
					:key="`allergy-${item}`"
					:class="['choice-chip', { 'choice-chip--active': localValue.allergyStatus === item }]"
				>
					<radio class="choice-radio" :value="item" :checked="localValue.allergyStatus === item" />
					<text>{{ item }}</text>
				</label>
			</radio-group>
			<input
				v-if="localValue.allergyStatus === '有'"
				class="input input--detail"
				:value="localValue.allergyDetail"
				placeholder="请填写过敏药物或过敏情况"
				@input="onInput('allergyDetail', $event)"
			/>
		</view>

		<view class="field">
			<text class="label">既往病史</text>
			<radio-group class="choice-row" @change="onConditionalChange('pastHistoryStatus', 'pastHistoryDetail', $event)">
				<label
					v-for="item in threeOptions"
					:key="`history-${item}`"
					:class="['choice-chip', { 'choice-chip--active': localValue.pastHistoryStatus === item }]"
				>
					<radio class="choice-radio" :value="item" :checked="localValue.pastHistoryStatus === item" />
					<text>{{ item }}</text>
				</label>
			</radio-group>
			<input
				v-if="localValue.pastHistoryStatus === '有'"
				class="input input--detail"
				:value="localValue.pastHistoryDetail"
				placeholder="请填写既往病史"
				@input="onInput('pastHistoryDetail', $event)"
			/>
		</view>

		<view class="field">
			<text class="label">当前用药</text>
			<radio-group class="choice-row" @change="onConditionalChange('medicationStatus', 'medicationDetail', $event)">
				<label
					v-for="item in threeOptions"
					:key="`medication-${item}`"
					:class="['choice-chip', { 'choice-chip--active': localValue.medicationStatus === item }]"
				>
					<radio class="choice-radio" :value="item" :checked="localValue.medicationStatus === item" />
					<text>{{ item }}</text>
				</label>
			</radio-group>
			<input
				v-if="localValue.medicationStatus === '有'"
				class="input input--detail"
				:value="localValue.medicationDetail"
				placeholder="请填写当前用药"
				@input="onInput('medicationDetail', $event)"
			/>
		</view>

		<view class="field field--last">
			<text class="label label--required">就诊类型</text>
			<radio-group class="choice-row" @change="onRadioChange('visitType', $event)">
				<label
					v-for="item in visitTypeOptions"
					:key="item"
					:class="['choice-chip', { 'choice-chip--active': localValue.visitType === item }]"
				>
					<radio class="choice-radio" :value="item" :checked="localValue.visitType === item" />
					<text>{{ item }}</text>
				</label>
			</radio-group>
		</view>
	</view>
</template>

<script>
export default {
	name: 'BasicInfoForm',
	props: {
		modelValue: {
			type: Object,
			default: () => ({})
		}
	},
	emits: ['update:modelValue'],
	data() {
		return {
			genderOptions: ['男', '女', '不便透露'],
			visitTypeOptions: ['初诊', '复诊'],
			threeOptions: ['有', '无', '不清楚'],
			localValue: {
				name: '',
				gender: '',
				age: '',
				chronicDisease: '',
				allergyStatus: '',
				allergyDetail: '',
				pastHistoryStatus: '',
				pastHistoryDetail: '',
				medicationStatus: '',
				medicationDetail: '',
				visitType: '',
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
		}
	},
	methods: {
		emitValue() {
			this.$emit('update:modelValue', { ...this.localValue })
		},
		onInput(field, event) {
			this.localValue[field] = event.detail.value
			this.emitValue()
		},
		onRadioChange(field, event) {
			this.localValue[field] = event.detail.value
			this.emitValue()
		},
		onConditionalChange(statusKey, detailKey, event) {
			const value = event.detail.value
			this.localValue[statusKey] = value
			if (value !== '有') {
				this.localValue[detailKey] = ''
			}
			this.emitValue()
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

.label--required::after {
	content: ' *';
	color: #c24134;
}

.input {
	height: 48px;
	padding: 0 14px;
	border: 1px solid rgba(163, 196, 232, 0.42);
	border-radius: 18px;
	background: #f7faf8;
	color: #103268;
	font-size: 15px;
	box-sizing: border-box;
}

.input--detail {
	margin-top: 12px;
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

.choice-radio {
	transform: scale(0.8);
}
</style>
