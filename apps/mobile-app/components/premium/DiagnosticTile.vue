<template>
	<view :class="['diagnostic-tile', { 'diagnostic-tile--wide': wide }]" @tap="$emit('tap')">
		<view class="diagnostic-tile__icon">
			<image v-if="icon" :src="icon" mode="aspectFit"></image>
			<text v-else class="diagnostic-tile__marker">{{ marker }}</text>
		</view>
		<view class="diagnostic-tile__content">
			<text class="diagnostic-tile__title">{{ title }}</text>
			<text v-if="subtitle" class="diagnostic-tile__subtitle">{{ subtitle }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'DiagnosticTile',
	props: {
		title: {
			type: String,
			required: true
		},
		subtitle: {
			type: String,
			default: ''
		},
		icon: {
			type: String,
			default: ''
		},
		marker: {
			type: String,
			default: 'compose'
		},
		wide: {
			type: Boolean,
			default: false
		}
	}
};
</script>

<style>
.diagnostic-tile {
	position: relative;
	display: flex;
	align-items: center;
	min-height: 120px;
	padding: 18px 16px;
	overflow: hidden;
	border: 1px solid rgba(255, 255, 255, 0.84);
	border-radius: 24px;
	background: linear-gradient(145deg, rgba(255, 255, 255, 0.9), rgba(163, 196, 232, 0.42));
	box-shadow: 0 12px 30px rgba(31, 95, 202, 0.08);
	box-sizing: border-box;
}

.diagnostic-tile::after {
	position: absolute;
	right: -24px;
	bottom: -30px;
	width: 92px;
	height: 92px;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.24);
	content: '';
}

.diagnostic-tile--wide {
	grid-column: span 2;
}

.diagnostic-tile__icon {
	position: relative;
	z-index: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 58px;
	height: 58px;
	flex-shrink: 0;
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.88);
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.diagnostic-tile__icon image {
	width: 32px;
	height: 32px;
}

.diagnostic-tile__marker {
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
}

.diagnostic-tile__content {
	position: relative;
	z-index: 1;
	display: flex;
	flex: 1;
	min-width: 0;
	margin-left: 12px;
	flex-direction: column;
}

.diagnostic-tile__title {
	color: #1f5fca;
	font-size: 16px;
	font-weight: 800;
	line-height: 1.4;
}

.diagnostic-tile__subtitle {
	margin-top: 6px;
	color: #5a7a99;
	font-size: 12px;
	line-height: 1.45;
}
</style>
