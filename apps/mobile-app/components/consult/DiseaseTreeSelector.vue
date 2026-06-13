<template>
	<view class="selector-card">
		<view class="selector-card__header">
			<text class="selector-card__eyebrow">DISEASE SELECTOR</text>
			<text class="selector-card__title">病种 / 症状选择</text>
		</view>

		<view class="search-box">
			<input
				class="search-input"
				v-model="keyword"
				placeholder="搜索病种或症状，如手臂、咳嗽、失眠"
				confirm-type="search"
			/>
		</view>

		<view v-if="selectedItems.length" class="panel">
			<text class="panel-title">已选择</text>
			<view class="selected-list">
				<view class="selected-chip" v-for="item in selectedItems" :key="item.code" @tap="toggleSelect(item)">
					<text class="selected-chip__text">{{ item.displayName }}</text>
					<RiskTag :level="item.riskLevel" />
					<text class="selected-chip__remove">×</text>
				</view>
			</view>
			<view v-if="hasHighRisk" class="risk-alert">
				这类情况可能需要尽快线下就医，请谨慎填写并及时联系医生。该选择仅用于分诊参考，不代表最终诊断。
			</view>
		</view>

		<view v-if="keyword" class="panel">
			<text class="panel-title">搜索结果</text>
			<view v-if="searchResults.length">
				<view class="disease-row" v-for="item in searchResults" :key="item.code" @tap="toggleSelect(item)">
					<view class="disease-main">
						<text class="disease-name">{{ item.displayName }}</text>
						<text class="disease-path">{{ formatPath(item.path) }}</text>
					</view>
					<RiskTag :level="item.riskLevel" />
					<text :class="['select-state', { 'select-state--active': isSelected(item.code) }]">
						{{ isSelected(item.code) ? '已选' : '选择' }}
					</text>
				</view>
			</view>
			<view v-else class="empty">未找到匹配项，可以换一个关键词再试。</view>
		</view>

		<view v-else class="panel">
			<scroll-view scroll-x class="category-scroll" show-scrollbar="false">
				<view class="category-track">
					<view
						v-for="(category, index) in tree"
						:key="category.code"
						:class="['category-tab', { 'category-tab--active': activeCategoryIndex === index }]"
						@tap="activeCategoryIndex = index"
					>
						{{ category.displayName }}
					</view>
				</view>
			</scroll-view>

			<view class="disease-list" v-if="activeCategory">
				<view class="disease-row" v-for="item in activeCategory.children" :key="item.code" @tap="toggleSelect(item)">
					<view class="disease-main">
						<text class="disease-name">{{ item.displayName }}</text>
						<text class="disease-path">{{ item.code }} · {{ (item.keywords || []).slice(0, 3).join('、') }}</text>
					</view>
					<RiskTag :level="item.riskLevel" />
					<text :class="['select-state', { 'select-state--active': isSelected(item.code) }]">
						{{ isSelected(item.code) ? '已选' : '选择' }}
					</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import RiskTag from './RiskTag.vue'
import { getDiseaseTree, searchDiseases } from '../../utils/diseaseSearch.js'

export default {
	name: 'DiseaseTreeSelector',
	components: { RiskTag },
	props: {
		modelValue: {
			type: Array,
			default: () => []
		}
	},
	emits: ['update:modelValue'],
	data() {
		const tree = getDiseaseTree()
		const commonSymptomsIndex = tree.findIndex((item) => item.code === 'A17')
		if (commonSymptomsIndex > 0) {
			const [commonSymptoms] = tree.splice(commonSymptomsIndex, 1)
			tree.unshift(commonSymptoms)
		}
		return {
			keyword: '',
			tree,
			activeCategoryIndex: 0,
			selectedItems: [...this.modelValue]
		}
	},
	computed: {
		activeCategory() {
			return this.tree[this.activeCategoryIndex]
		},
		searchResults() {
			return searchDiseases(this.keyword)
		},
		hasHighRisk() {
			return this.selectedItems.some((item) => ['high', 'critical'].includes(item.riskLevel))
		}
	},
	watch: {
		modelValue: {
			deep: true,
			handler(value) {
				this.selectedItems = [...(value || [])]
			}
		},
		selectedItems: {
			deep: true,
			handler(value) {
				this.$emit('update:modelValue', value)
			}
		}
	},
	methods: {
		formatPath(path = []) {
			return path.join(' > ')
		},
		isSelected(code) {
			return this.selectedItems.some((item) => item.code === code)
		},
		toggleSelect(item) {
			if (!item.selectable) {
				return
			}
			if (this.isSelected(item.code)) {
				this.selectedItems = this.selectedItems.filter((selected) => selected.code !== item.code)
			} else {
				this.selectedItems = [...this.selectedItems, item]
			}
		}
	}
}
</script>

<style scoped>
.selector-card {
	padding: 22px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.86);
	box-shadow: 0 18px 40px rgba(31, 95, 202, 0.06);
}

.selector-card__header {
	display: flex;
	flex-direction: column;
	margin-bottom: 18px;
}

.selector-card__eyebrow {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.16em;
}

.selector-card__title {
	margin-top: 8px;
	color: #103268;
	font-size: 24px;
	font-weight: 800;
}

.search-box {
	margin-bottom: 14px;
}

.search-input {
	height: 48px;
	padding: 0 16px;
	border: 1px solid rgba(163, 196, 232, 0.42);
	border-radius: 18px;
	background: #f7faf8;
	font-size: 14px;
	box-sizing: border-box;
}

.panel {
	margin-top: 14px;
	padding: 16px;
	border-radius: 22px;
	background: rgba(240, 245, 255, 0.8);
}

.panel-title {
	display: block;
	margin-bottom: 12px;
	color: #1f5fca;
	font-size: 15px;
	font-weight: 800;
}

.selected-list {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}

.selected-chip {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	padding: 8px 12px;
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.38);
	color: #103268;
	font-size: 13px;
}

.selected-chip__text {
	max-width: 160px;
}

.selected-chip__remove {
	color: #7a8fa8;
	font-size: 14px;
	font-weight: 800;
}

.risk-alert {
	margin-top: 12px;
	padding: 12px 14px;
	border-radius: 16px;
	background: rgba(255, 225, 223, 0.82);
	color: #c24134;
	font-size: 13px;
	line-height: 1.65;
}

.category-scroll {
	margin-bottom: 12px;
}

.category-track {
	display: inline-flex;
	gap: 8px;
	padding-right: 10px;
}

.category-tab {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	padding: 10px 14px;
	border-radius: 999px;
	background: #edf3ef;
	color: #5a7a99;
	font-size: 13px;
	font-weight: 700;
	white-space: nowrap;
}

.category-tab--active {
	background: #1f5fca;
	color: #ffffff;
}

.disease-row {
	display: flex;
	align-items: center;
	gap: 8px;
	padding: 14px 0;
	border-bottom: 1px solid rgba(163, 196, 232, 0.3);
}

.disease-row:last-child {
	border-bottom: 0;
}

.disease-main {
	flex: 1;
	min-width: 0;
}

.disease-name {
	display: block;
	color: #103268;
	font-size: 15px;
	font-weight: 700;
}

.disease-path {
	display: block;
	margin-top: 5px;
	color: #7a8fa8;
	font-size: 12px;
	line-height: 1.5;
}

.select-state {
	min-width: 42px;
	padding: 6px 8px;
	border-radius: 999px;
	background: #edf3ef;
	color: #7a8fa8;
	font-size: 12px;
	font-weight: 700;
	text-align: center;
}

.select-state--active {
	background: rgba(163, 196, 232, 0.48);
	color: #1f5fca;
}

.empty {
	padding: 18px 0 6px;
	color: #98a2b3;
	font-size: 14px;
	text-align: center;
}
</style>
