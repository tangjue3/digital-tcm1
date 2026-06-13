<template>
	<button
		v-if="openType"
		class="profile-menu-item"
		:class="{ 'profile-menu-item--danger': danger }"
		:open-type="openType"
		@tap="$emit('tap')"
	>
		<view class="profile-menu-item__left">
			<view class="profile-menu-item__icon" :class="{ 'profile-menu-item__icon--danger': danger }">{{ marker }}</view>
			<view class="profile-menu-item__copy">
				<text class="profile-menu-item__title">{{ title }}</text>
				<text v-if="subtitle" class="profile-menu-item__subtitle">{{ subtitle }}</text>
			</view>
		</view>
		<text v-if="showArrow" class="profile-menu-item__arrow">→</text>
	</button>

	<button
		v-else
		class="profile-menu-item"
		:class="{ 'profile-menu-item--danger': danger }"
		@tap="$emit('tap')"
	>
		<view class="profile-menu-item__left">
			<view class="profile-menu-item__icon" :class="{ 'profile-menu-item__icon--danger': danger }">{{ marker }}</view>
			<view class="profile-menu-item__copy">
				<text class="profile-menu-item__title">{{ title }}</text>
				<text v-if="subtitle" class="profile-menu-item__subtitle">{{ subtitle }}</text>
			</view>
		</view>
		<text v-if="showArrow" class="profile-menu-item__arrow">→</text>
	</button>
</template>

<script>
export default {
	name: 'ProfileMenuItem',
	props: {
		title: {
			type: String,
			required: true
		},
		subtitle: {
			type: String,
			default: ''
		},
		marker: {
			type: String,
			default: '📋'
		},
		openType: {
			type: String,
			default: ''
		},
		danger: {
			type: Boolean,
			default: false
		},
		showArrow: {
			type: Boolean,
			default: true
		}
	}
};
</script>

<style>
.profile-menu-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	width: 100%;
	padding: 16px;
	border: 1px solid rgba(163, 196, 232, 0.18);
	border-radius: 22px;
	background: rgba(255, 255, 255, 0.92);
	box-shadow: 0 10px 26px rgba(31, 95, 202, 0.05);
	text-align: left;
}

.profile-menu-item::after {
	display: none;
}

.profile-menu-item--danger {
	border-color: rgba(186, 26, 26, 0.16);
	background: linear-gradient(135deg, rgba(255, 218, 214, 0.42), rgba(255, 255, 255, 0.94));
}

.profile-menu-item__left {
	display: flex;
	align-items: center;
	flex: 1;
	min-width: 0;
}

.profile-menu-item__icon {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 48px;
	height: 48px;
	flex-shrink: 0;
	border-radius: 999px;
	background: rgba(208, 231, 234, 0.6);
	color: #1f5fca;
	font-size: 18px;
	font-weight: 800;
}

.profile-menu-item__icon--danger {
	background: rgba(255, 218, 214, 0.84);
	color: #ba1a1a;
}

.profile-menu-item__copy {
	display: flex;
	flex: 1;
	min-width: 0;
	margin-left: 14px;
	flex-direction: column;
}

.profile-menu-item__title {
	color: #103268;
	font-size: 16px;
	font-weight: 800;
	line-height: 1.35;
}

.profile-menu-item--danger .profile-menu-item__title {
	color: #ba1a1a;
}

.profile-menu-item__subtitle {
	margin-top: 5px;
	color: #5a7a99;
	font-size: 12px;
	line-height: 1.5;
}

.profile-menu-item__arrow {
	margin-left: 12px;
	color: #7a8fa8;
	font-size: 24px;
	font-weight: 400;
	line-height: 1;
}
</style>
