<template>
	<view class="premium-topbar">
		<button v-if="showLeft" class="premium-topbar__action" hover-class="premium-topbar__action--hover" @tap="$emit('leftTap')">
			<view class="premium-topbar__menu">
				<text></text>
				<text></text>
				<text></text>
			</view>
		</button>
		<view v-else class="premium-topbar__placeholder"></view>
		<text class="premium-topbar__title">{{ title }}</text>
		<button v-if="showRight" class="premium-topbar__action" hover-class="premium-topbar__action--hover" @tap="$emit('rightTap')">
			<view class="premium-topbar__signal">
				<view class="premium-topbar__signal-ring"></view>
				<view class="premium-topbar__signal-dot"></view>
			</view>
		</button>
		<view v-else class="premium-topbar__placeholder"></view>
	</view>
</template>

<script>
export default {
	name: 'PremiumTopBar',
	props: {
		title: {
			type: String,
			required: true
		},
		showLeft: {
			type: Boolean,
			default: true
		},
		showRight: {
			type: Boolean,
			default: true
		},
		leftIcon: {
			type: String,
			default: 'bars'
		},
		rightIcon: {
			type: String,
			default: 'notification'
		}
	}
};
</script>

<style>
.premium-topbar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 18px;
	padding: 6px 2px;
}

.premium-topbar__action,
.premium-topbar__placeholder {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 40px;
	height: 40px;
	flex-shrink: 0;
	border: 0;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.58);
	box-shadow: 0 8px 24px rgba(31, 95, 202, 0.08);
}

.premium-topbar__action {
	padding: 0;
}

.premium-topbar__action--hover {
	opacity: 0.82;
}

.premium-topbar__menu {
	display: flex;
	flex-direction: column;
	gap: 4px;
}

.premium-topbar__menu text {
	display: block;
	width: 16px;
	height: 2px;
	border-radius: 999px;
	background: #1f5fca;
}

.premium-topbar__signal {
	position: relative;
	width: 18px;
	height: 18px;
}

.premium-topbar__signal-ring {
	position: absolute;
	top: 1px;
	right: 1px;
	bottom: 1px;
	left: 1px;
	border: 2px solid #1f5fca;
	border-radius: 999px;
}

.premium-topbar__signal-dot {
	position: absolute;
	top: 0;
	right: 0;
	width: 6px;
	height: 6px;
	border-radius: 999px;
	background: #94d3c1;
}

.premium-topbar__title {
	flex: 1;
	padding: 0 14px;
	color: #1f5fca;
	font-size: 20px;
	font-weight: 800;
	text-align: center;
	letter-spacing: 0.01em;
}
</style>
