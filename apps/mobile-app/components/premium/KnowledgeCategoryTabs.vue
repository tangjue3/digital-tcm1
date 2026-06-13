<template>
	<scroll-view class="knowledge-category-tabs" scroll-x show-scrollbar="false">
		<view class="knowledge-category-tabs__track">
			<view
				v-for="(item, index) in items"
				:key="item.name || index"
				:class="['knowledge-category-tabs__pill', { 'knowledge-category-tabs__pill--active': index === activeIndex }]"
				@tap="$emit('change', index)"
			>
				<text class="knowledge-category-tabs__label">{{ item.name }}</text>
			</view>
		</view>
	</scroll-view>
</template>

<script>
export default {
	name: 'KnowledgeCategoryTabs',
	props: {
		items: {
			type: Array,
			default: () => []
		},
		activeIndex: {
			type: Number,
			default: 0
		}
	}
};
</script>

<style>
.knowledge-category-tabs {
	margin-top: 16px;
}

.knowledge-category-tabs__track {
	display: inline-flex;
	gap: 10px;
	padding-right: 12px;
}

.knowledge-category-tabs__pill {
	display: flex;
	align-items: center;
	justify-content: center;
	min-width: 96px;
	padding: 12px 18px;
	border: 1px solid rgba(163, 196, 232, 0.22);
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.82);
	box-shadow: 0 10px 24px rgba(31, 95, 202, 0.05);
	box-sizing: border-box;
}

.knowledge-category-tabs__pill--active {
	background: #1f5fca;
	box-shadow: 0 16px 32px rgba(0, 52, 43, 0.18);
}

.knowledge-category-tabs__label {
	color: #3d5a73;
	font-size: 13px;
	font-weight: 800;
	white-space: nowrap;
}

.knowledge-category-tabs__pill--active .knowledge-category-tabs__label {
	color: #ffffff;
}
</style>
