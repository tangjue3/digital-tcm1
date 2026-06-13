<template>
	<view class="personal-center-container">
		<PremiumTopBar title="云枢四诊" />

		<ProfileCard
			:avatar="avatarUrl"
			:primary-text="primaryText"
			:secondary-text="secondaryText"
			:badge-text="badgeText"
			:fallback-text="fallbackText"
		/>

		<view class="menu-section">
			<ProfileMenuItem
				title="全面检测"
				subtitle="完善问诊、体质与健康数据"
				marker="检"
				@tap="jump('/pages/guide/guide')"
			/>

			<ProfileMenuItem
				title="云枢问答"
				subtitle="继续咨询中医健康问题"
				marker="问"
				@tap="jump('/pages/medical-chat/chat')"
			/>

			<ProfileMenuItem
				title="意见反馈"
				subtitle="告诉我们你的使用感受"
				marker="馈"
				open-type="feedback"
			/>
		</view>

		<view class="menu-spacer"></view>

		<ProfileMenuItem
			title="退出登录"
			subtitle="清除本机登录状态与临时记录"
			marker="退"
			:danger="true"
			:show-arrow="false"
			@tap="logout"
		/>

		<PremiumTabBar active="my" />
	</view>
</template>

<script>
import PremiumTabBar from '../../components/premium/PremiumTabBar.vue';
import PremiumTopBar from '../../components/premium/PremiumTopBar.vue';
import ProfileCard from '../../components/premium/ProfileCard.vue';
import ProfileMenuItem from '../../components/premium/ProfileMenuItem.vue';
import { ASSET_BASE_URL } from '../../common/config.js';

export default {
	components: {
		PremiumTabBar,
		PremiumTopBar,
		ProfileCard,
		ProfileMenuItem
	},
	data() {
		return {
			user: {}
		};
	},
	computed: {
		primaryText() {
			return this.maskPhone(this.user.phoneNumber || this.user.phonenumber) || this.user.nickname || this.user.nickName || '未登录';
		},
		secondaryText() {
			const nickname = this.user.nickname || this.user.nickName || '';
			const username = this.user.userName || '';
			if (nickname && nickname !== this.primaryText) return nickname;
			if (username && username !== this.primaryText) return username;
			return '已连接个人健康档案';
		},
		badgeText() {
			return '已连接健康档案';
		},
		fallbackText() {
			const source = this.user.nickname || this.user.nickName || this.user.userName || '中';
			return String(source).slice(0, 1);
		},
		avatarUrl() {
			const avatar = this.user.avatar || '';
			if (!avatar) return '';
			if (/^https?:\/\//.test(avatar) || avatar.startsWith('/')) {
				return avatar.startsWith('/') ? `${ASSET_BASE_URL}${avatar}` : avatar;
			}
			return `${ASSET_BASE_URL}/${avatar}`;
		}
	},
	onLoad() {
		if (!uni.getStorageSync('loginType')) {
			uni.redirectTo({
				url: '/pages/Login/Login'
			});
			return;
		}
		this.loadUserInfo();
	},
	onShow() {
		this.loadUserInfo();
	},
	methods: {
		loadUserInfo() {
			try {
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo) {
					this.user = JSON.parse(userInfo);
				}
			} catch (e) {
				console.error('获取用户信息失败', e);
			}
		},
		maskPhone(phone) {
			if (!phone) return '';
			const cleanPhone = String(phone);
			if (cleanPhone.length < 7) return cleanPhone;
			return `${cleanPhone.slice(0, 3)}****${cleanPhone.slice(-4)}`;
		},
		jump(url) {
			uni.navigateTo({
				url
			});
		},
		logout() {
			uni.showModal({
				title: '提示',
				content: '确定要退出登录吗？',
				success: function(res) {
					if (res.confirm) {
						uni.removeStorageSync('loginType');
						uni.removeStorageSync('token');
						uni.removeStorageSync('Authorization');
						uni.removeStorageSync('userInfo');
						uni.removeStorageSync('hasCompletedGuide');
						uni.removeStorageSync('userGuide');
						uni.removeStorageSync('pulseSessionBase');
						uni.removeStorageSync('faceData');
						uni.removeStorageSync('hishory');

						uni.reLaunch({
							url: '/pages/Login/Login'
						});
					}
				}
			});
		}
	}
};
</script>

<style>
.personal-center-container {
	min-height: 100vh;
	padding: 18px 18px 116px;
	background-color: #f8faf7;
	background-image:
		radial-gradient(circle at 0% 0%, rgba(208, 231, 234, 0.48) 0, transparent 40%),
		radial-gradient(circle at 100% 48%, rgba(148, 211, 193, 0.18) 0, transparent 42%);
	box-sizing: border-box;
}

.menu-section {
	display: flex;
	flex-direction: column;
	gap: 12px;
	margin-top: 18px;
}

.menu-spacer {
	height: 18px;
}
</style>
