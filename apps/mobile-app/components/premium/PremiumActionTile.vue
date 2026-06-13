<template>
	<view class="premium-action-tile" @tap="$emit('tap')">
		<view class="premium-action-tile__icon">
			<image v-if="icon" :src="icon" mode="aspectFit"></image>
			<text v-else>{{ marker }}</text>
		</view>
		<view class="premium-action-tile__body">
			<text class="premium-action-tile__title">{{ title }}</text>
			<text v-if="subtitle" class="premium-action-tile__subtitle">{{ subtitle }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'PremiumActionTile',
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
			default: '◎'
		}
	}
};
</script>

<style>
.premium-action-tile {
	position: relative;
	display: flex;
	align-items: center;
	gap: 14px;
	min-height: 92px;
	padding: 18px;
	overflow: hidden;
	border: 1px solid rgba(255, 255, 255, 0.86);
	border-radius: 22px;
	background: linear-gradient(145deg, rgba(255, 255, 255, 0.9), rgba(163, 196, 232, 0.34));
	box-shadow: 0 10px 28px rgba(31, 95, 202, 0.07);
}

.premium-action-tile::after {
	position: absolute;
	right: -28px;
	bottom: -34px;
	width: 96px;
	height: 96px;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.26);
	content: "";
}

.premium-action-tile__icon {
	position: relative;
	z-index: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 54px;
	height: 54px;
	flex-shrink: 0;
	border: 1px solid rgba(255, 255, 255, 0.72);
	border-radius: 18px;
	background: rgba(208, 231, 234, 0.72);
	color: #1f5fca;
	font-size: 18px;
	font-weight: 800;
	box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.premium-action-tile__icon image {
	width: 34px;
	height: 34px;
}

.premium-action-tile__body {
	position: relative;
	z-index: 1;
	display: flex;
	flex: 1;
	min-width: 0;
	flex-direction: column;
}

.premium-action-tile__title {
	color: #1f5fca;
	font-size: 16px;
	font-weight: 800;
	line-height: 1.35;
}

.premium-action-tile__subtitle {
	margin-top: 6px;
	color: #5a7a99;
	font-size: 12px;
	line-height: 1.45;
}
</style>
