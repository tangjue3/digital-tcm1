<template>
	<view class="profile-card">
		<view class="profile-card__mist"></view>
		<view class="profile-card__avatar-shell">
			<image v-if="avatar" class="profile-card__avatar" :src="avatar" mode="aspectFill"></image>
			<view v-else class="profile-card__avatar profile-card__avatar--fallback">
				<text>{{ fallbackText }}</text>
			</view>
		</view>
		<view class="profile-card__info">
			<text class="profile-card__eyebrow">{{ eyebrow }}</text>
			<text class="profile-card__primary">{{ primaryText }}</text>
			<text v-if="secondaryText" class="profile-card__secondary">{{ secondaryText }}</text>
			<view class="profile-card__badge">
				<text class="profile-card__badge-dot"></text>
				<text class="profile-card__badge-text">{{ badgeText }}</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	name: 'ProfileCard',
	props: {
		avatar: {
			type: String,
			default: ''
		},
		eyebrow: {
			type: String,
			default: 'PERSONAL CENTER'
		},
		primaryText: {
			type: String,
			required: true
		},
		secondaryText: {
			type: String,
			default: ''
		},
		badgeText: {
			type: String,
			default: '已连接健康档案'
		},
		fallbackText: {
			type: String,
			default: '中'
		}
	}
};
</script>

<style>
.profile-card {
	position: relative;
	display: flex;
	align-items: center;
	gap: 18px;
	padding: 22px 20px;
	overflow: hidden;
	border: 1px solid rgba(255, 255, 255, 0.84);
	border-radius: 28px;
	background: rgba(255, 255, 255, 0.76);
	box-shadow: 0 16px 40px rgba(31, 95, 202, 0.07);
	backdrop-filter: blur(18px);
}

.profile-card__mist {
	position: absolute;
	top: -46px;
	right: -30px;
	width: 160px;
	height: 160px;
	border-radius: 999px;
	background: rgba(163, 196, 232, 0.18);
	filter: blur(12px);
}

.profile-card__avatar-shell {
	position: relative;
	z-index: 1;
	width: 84px;
	height: 84px;
	padding: 4px;
	flex-shrink: 0;
	border-radius: 999px;
	background: linear-gradient(135deg, #a3c4e8, #f2f4f1);
	box-shadow: 0 12px 24px rgba(31, 95, 202, 0.1);
}

.profile-card__avatar {
	width: 100%;
	height: 100%;
	border: 2px solid #ffffff;
	border-radius: 999px;
	background: #f2f4f1;
}

.profile-card__avatar--fallback {
	display: flex;
	align-items: center;
	justify-content: center;
	color: #1f5fca;
	font-size: 28px;
	font-weight: 800;
}

.profile-card__info {
	position: relative;
	z-index: 1;
	display: flex;
	flex: 1;
	min-width: 0;
	flex-direction: column;
}

.profile-card__eyebrow {
	color: #5a7a99;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.14em;
}

.profile-card__primary {
	margin-top: 8px;
	color: #103268;
	font-size: 28px;
	font-weight: 800;
	line-height: 1.2;
}

.profile-card__secondary {
	margin-top: 6px;
	color: #5a7a99;
	font-size: 13px;
	line-height: 1.5;
}

.profile-card__badge {
	display: inline-flex;
	align-items: center;
	align-self: flex-start;
	gap: 8px;
	margin-top: 12px;
	padding: 7px 12px;
	border: 1px solid rgba(208, 231, 234, 0.86);
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.42);
}

.profile-card__badge-dot {
	width: 8px;
	height: 8px;
	border-radius: 999px;
	background: #1f5fca;
}

.profile-card__badge-text {
	color: #065043;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.08em;
}
</style>
