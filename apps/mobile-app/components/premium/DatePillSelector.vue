<template>
	<scroll-view scroll-x class="date-pill-selector">
		<view
			v-for="item in items"
			:key="item.value"
			:class="['date-pill-selector__item', { 'date-pill-selector__item--active': value === item.value }]"
			@tap="$emit('change', item.value)"
		>
			<text class="date-pill-selector__week">{{ item.week }}</text>
			<text class="date-pill-selector__day">{{ item.day }}</text>
		</view>
	</scroll-view>
</template>

<script>
export default {
	name: 'DatePillSelector',
	props: {
		items: {
			type: Array,
			default: () => []
		},
		value: {
			type: String,
			default: ''
		}
	}
};
</script>

<style>
.date-pill-selector {
	width: 100%;
	margin: 6px 0 18px;
	white-space: nowrap;
}

.date-pill-selector__item {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	width: 60px;
	height: 84px;
	margin-right: 12px;
	flex-direction: column;
	border: 1px solid rgba(255, 255, 255, 0.76);
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.38);
	color: #5a7a99;
	box-shadow: 0 8px 20px rgba(31, 95, 202, 0.04);
}

.date-pill-selector__item--active {
	background: #1f5fca;
	color: #ffffff;
	box-shadow: 0 12px 28px rgba(31, 95, 202, 0.2);
	transform: scale(1.04);
}

.date-pill-selector__week {
	margin-bottom: 8px;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.08em;
}

.date-pill-selector__day {
	font-size: 21px;
	font-weight: 800;
}
</style>
