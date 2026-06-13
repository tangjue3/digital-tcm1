<template>
	<view class="hero-panel" @tap="$emit('tap')">
		<view class="hero-panel__waves"></view>
		<view class="hero-panel__orb hero-panel__orb--large"></view>
		<view class="hero-panel__orb hero-panel__orb--small"></view>
		<view class="hero-panel__content">
			<text class="hero-panel__eyebrow">{{ eyebrow }}</text>
			<text class="hero-panel__title">{{ title }}</text>
			<text class="hero-panel__desc">{{ description }}</text>
			<view class="hero-panel__cta" @tap.stop="$emit('tap')">
				<text class="hero-panel__cta-text">{{ buttonText }}</text>
				<text class="hero-panel__cta-arrow">&gt;</text>
			</view>
		</view>
		<image v-if="image" class="hero-panel__image" :src="image" mode="aspectFit"></image>
	</view>
</template>

<script>
export default {
	name: 'SelfTestHeroPanel',
	props: {
		eyebrow: {
			type: String,
			default: 'HOLISTIC CHECK'
		},
		title: {
			type: String,
			required: true
		},
		description: {
			type: String,
			required: true
		},
		buttonText: {
			type: String,
			default: 'START'
		},
		image: {
			type: String,
			default: ''
		}
	}
};
</script>

<style>
.hero-panel {
	position: relative;
	display: flex;
	align-items: stretch;
	min-height: 244px;
	padding: 24px 22px;
	overflow: hidden;
	border-radius: 30px;
	background: linear-gradient(135deg, #1f5fca 0%, #1f5fca 60%, #1f5fca 100%);
	box-shadow: 0 24px 56px rgba(31, 95, 202, 0.2);
}

.hero-panel__waves {
	position: absolute;
	top: -18px;
	right: -48px;
	bottom: -18px;
	left: 46%;
	background:
		radial-gradient(circle at 30% 45%, rgba(255, 255, 255, 0.22) 0, transparent 24%),
		radial-gradient(circle at 78% 28%, rgba(148, 211, 193, 0.2) 0, transparent 18%),
		repeating-radial-gradient(circle at 72% 48%, rgba(255, 255, 255, 0.1) 0 2px, transparent 2px 20px);
	opacity: 0.85;
}

.hero-panel__orb {
	position: absolute;
	border-radius: 999px;
	background: rgba(255, 255, 255, 0.08);
	filter: blur(2px);
}

.hero-panel__orb--large {
	top: -38px;
	right: -24px;
	width: 180px;
	height: 180px;
}

.hero-panel__orb--small {
	right: 92px;
	bottom: 18px;
	width: 74px;
	height: 74px;
	background: rgba(175, 239, 221, 0.16);
}

.hero-panel__content {
	position: relative;
	z-index: 1;
	display: flex;
	max-width: 238px;
	flex-direction: column;
}

.hero-panel__eyebrow {
	margin-bottom: 12px;
	color: rgba(255, 255, 255, 0.78);
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.18em;
}

.hero-panel__title {
	color: #ffffff;
	font-size: 31px;
	font-weight: 900;
	line-height: 1.15;
}

.hero-panel__desc {
	margin-top: 10px;
	color: rgba(255, 255, 255, 0.8);
	font-size: 14px;
	line-height: 1.65;
}

.hero-panel__cta {
	display: inline-flex;
	align-items: center;
	align-self: flex-start;
	gap: 6px;
	margin-top: auto;
	padding: 12px 18px;
	border-radius: 999px;
	background: rgba(31, 95, 202, 0.92);
	box-shadow: 0 12px 24px rgba(0, 0, 0, 0.16);
}

.hero-panel__cta-text {
	color: #ffffff;
	font-size: 12px;
	font-weight: 800;
	letter-spacing: 0.14em;
}

.hero-panel__cta-arrow {
	color: #ffffff;
	font-size: 14px;
	font-weight: 800;
	line-height: 1;
}

.hero-panel__image {
	position: absolute;
	right: 8px;
	bottom: 6px;
	z-index: 1;
	width: 164px;
	height: 164px;
	opacity: 0.72;
}
</style>
