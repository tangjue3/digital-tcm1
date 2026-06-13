<template>
	<view class="premium-tabbar">
		<view
			v-for="item in tabs"
			:key="item.key"
			:class="['premium-tabbar__item', { 'premium-tabbar__item--active': active === item.key }]"
			@tap="switchTo(item)"
		>
			<view class="premium-tabbar__icon" v-html="item.icon"></view>
			<text class="premium-tabbar__label">{{ item.label }}</text>
			<view v-if="active === item.key" class="premium-tabbar__dot"></view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'PremiumTabBar',
	props: {
		active: {
			type: String,
			required: true
		}
	},
	data() {
		return {
			tabs: [
				{
					key: 'health-plan',
					label: '健康计划',
					url: '/pages/health-plan/health-plan',
					icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="m9 14 2 2 4-4"/></svg>'
				},
				{
					key: 'zice',
					label: '健康自测',
					url: '/pages/zice/zice',
					icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="2 13 6 13 8 8 11 16 13 10 15 13 19 13 22 13"/></svg>'
				},
				{
					key: 'expo',
					label: '健康博览',
					url: '/pages/expo/expo',
					icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H19a1 1 0 0 1 1 1v18a1 1 0 0 1-1 1H6.5a2.5 2.5 0 0 1 0-5H20"/></svg>'
				},
				{
					key: 'my',
					label: '个人中心',
					url: '/pages/my/my',
					icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="5"/><path d="M20 21a8 8 0 0 0-16 0"/></svg>'
				}
			]
		};
	},
	mounted() {
		uni.hideTabBar({
			animation: false,
			fail: () => {}
		});
	},
	methods: {
		switchTo(item) {
			if (item.key === this.active) return;
			uni.switchTab({
				url: item.url
			});
		}
	}
};
</script>

<style>
.premium-tabbar {
	position: fixed;
	right: 16px;
	bottom: calc(14px + env(safe-area-inset-bottom));
	left: 16px;
	z-index: 9999;
	display: flex;
	align-items: center;
	justify-content: space-around;
	min-height: 72px;
	padding: 8px 10px 10px;
	border: 1px solid rgba(255, 255, 255, 0.88);
	border-radius: 999px;
	background: rgba(240, 245, 255, 0.92);
	box-shadow: 0 16px 40px rgba(31, 95, 202, 0.14);
	backdrop-filter: blur(18px);
	box-sizing: border-box;
}

.premium-tabbar__item {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	min-width: 70px;
	flex-direction: column;
	color: #5a7a99;
}

.premium-tabbar__icon {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 30px;
	height: 30px;
	border-radius: 999px;
	background: transparent;
	transition: background 0.25s ease, box-shadow 0.25s ease;
}

.premium-tabbar__icon svg {
	width: 16px;
	height: 16px;
	display: block;
}

.premium-tabbar__label {
	margin-top: 4px;
	font-size: 10px;
	font-weight: 800;
	letter-spacing: 0.06em;
	line-height: 1;
}

.premium-tabbar__dot {
	width: 5px;
	height: 5px;
	margin-top: 4px;
	border-radius: 999px;
	background: #1f5fca;
}

.premium-tabbar__item--active {
	color: #1f5fca;
}

.premium-tabbar__item--active .premium-tabbar__icon {
	background: #1f5fca;
	color: #ffffff;
	box-shadow: 0 8px 18px rgba(31, 95, 202, 0.22);
}
</style>
