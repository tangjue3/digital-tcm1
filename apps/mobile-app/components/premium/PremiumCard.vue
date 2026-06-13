<template>
	<view :class="cardClass" @tap="$emit('tap')">
		<view class="premium-card__glow"></view>
		<view v-if="$slots.icon" class="premium-card__icon">
			<slot name="icon"></slot>
		</view>
		<view class="premium-card__content">
			<text v-if="eyebrow" class="premium-card__eyebrow">{{ eyebrow }}</text>
			<view v-if="title || $slots.title" class="premium-card__title">
				<slot name="title">{{ title }}</slot>
			</view>
			<text v-if="subtitle" class="premium-card__subtitle">{{ subtitle }}</text>
			<slot></slot>
		</view>
		<view v-if="$slots.action" class="premium-card__action">
			<slot name="action"></slot>
		</view>
	</view>
</template>

<script>
export default {
	name: 'PremiumCard',
	props: {
		title: {
			type: String,
			default: ''
		},
		subtitle: {
			type: String,
			default: ''
		},
		eyebrow: {
			type: String,
			default: ''
		},
		variant: {
			type: String,
			default: 'default'
		}
	},
	computed: {
		cardClass() {
			return ['premium-card', `premium-card--${this.variant}`];
		}
	}
};
</script>

<style>
.premium-card {
	position: relative;
	display: flex;
	align-items: center;
	gap: 16px;
	padding: 20px;
	overflow: hidden;
	border: 1px solid rgba(255, 255, 255, 0.82);
	border-radius: 24px;
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.88), rgba(163, 196, 232, 0.32));
	box-shadow: 0 12px 34px rgba(31, 95, 202, 0.08);
}

.premium-card--hero {
	align-items: stretch;
	min-height: 132px;
	background: linear-gradient(135deg, #1f5fca 0%, #3f8cff 60%, #1f5fca 100%);
	color: #ffffff;
	box-shadow: 0 18px 42px rgba(31, 95, 202, 0.18);
}

.premium-card--danger {
	background: linear-gradient(135deg, rgba(255, 218, 214, 0.56), rgba(255, 255, 255, 0.88));
}

.premium-card__glow {
	position: absolute;
	top: -78px;
	right: -80px;
	width: 180px;
	height: 180px;
	border-radius: 999px;
	background: rgba(148, 211, 193, 0.25);
	filter: blur(26px);
	pointer-events: none;
}

.premium-card--hero .premium-card__glow {
	background: rgba(175, 239, 221, 0.2);
}

.premium-card__icon {
	position: relative;
	z-index: 1;
	flex-shrink: 0;
}

.premium-card__content {
	position: relative;
	z-index: 1;
	flex: 1;
	min-width: 0;
}

.premium-card__eyebrow {
	display: block;
	margin-bottom: 8px;
	color: #5a7a99;
	font-size: 11px;
	font-weight: 700;
	letter-spacing: 0.12em;
	text-transform: uppercase;
}

.premium-card--hero .premium-card__eyebrow,
.premium-card--hero .premium-card__subtitle {
	color: rgba(255, 255, 255, 0.76);
}

.premium-card__title {
	color: #1f5fca;
	font-size: 22px;
	font-weight: 800;
	line-height: 1.28;
}

.premium-card--hero .premium-card__title {
	color: #ffffff;
	font-size: 24px;
}

.premium-card__subtitle {
	display: block;
	margin-top: 8px;
	color: #103268;
	font-size: 14px;
	line-height: 1.55;
}

.premium-card__action {
	position: relative;
	z-index: 1;
	flex-shrink: 0;
}
</style>
