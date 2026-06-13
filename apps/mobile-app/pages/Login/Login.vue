<template>
	<view class="login-container">
		<view class="logo">
			<image src="../../static/app-logo.png" mode="aspectFit"></image>
		</view>

		<view class="form">
			<view class="input-group">
				<input
					v-model="phoneNumber"
					type="number"
					focus
					placeholder="请输入手机号"
					class="input-field uni-input"
					@input="validateInput('phoneNumber')"
				/>
				<view v-if="!isPhoneNumberValid && phoneNumber" class="error-tip">请输入有效的 11 位手机号</view>
			</view>
			<view class="input-group">
				<input
					v-model="password"
					type="password"
					placeholder="请输入密码"
					class="input-field"
					@input="validateInput('password')"
				/>
				<view v-if="!isPasswordValid && password" class="error-tip">密码长度不能少于 6 位</view>
			</view>
			<button class="login-button" @click="handleLogin">
				{{ submitting ? '登录中...' : '登录' }}
			</button>
		</view>

		<view class="footer">
			<navigator url="/pages/register/register">注册新账号</navigator>
		</view>
	</view>
</template>

<script>
import { clearAuthStorage, loginByPassword } from '../../common/auth.js'

export default {
	data() {
		return {
			phoneNumber: '',
			password: '',
			isPhoneNumberValid: true,
			isPasswordValid: true,
			submitting: false
		}
	},
	computed: {
		isFormValid() {
			return this.isPhoneNumberValid && this.isPasswordValid && this.phoneNumber && this.password
		}
	},
	onLoad() {
		this.redirectIfLoggedIn()
	},
	methods: {
		async redirectIfLoggedIn() {
			if (!uni.getStorageSync('loginType')) {
				return
			}
			uni.switchTab({ url: '/pages/health-plan/health-plan' })
		},
		validateInput(field) {
			switch (field) {
				case 'phoneNumber': {
					const phoneRegex = /^1[3-9]\d{9}$/
					this.isPhoneNumberValid = phoneRegex.test(this.phoneNumber)
					break
				}
				case 'password':
					this.isPasswordValid = this.password.length >= 6
					break
				default:
					break
			}
		},
		async handleLogin() {
			if (!this.isFormValid || this.submitting) {
				return
			}
			this.submitting = true
			try {
				await loginByPassword(this.phoneNumber, this.password)
				uni.showToast({
					title: '登录成功',
					icon: 'success'
				})

				setTimeout(() => {
					uni.switchTab({ url: '/pages/health-plan/health-plan' })
				}, 500)
			} catch (error) {
				console.error(error)
				clearAuthStorage()
				uni.showToast({
					title: '用户名或密码错误',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		}
	}
}
</script>

<style>
.login-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	min-height: 100vh;
	padding: 30px 0;
	background-color: #f8f8f8;
}

.logo {
	margin-bottom: 30px;
}

.logo image {
	width: 200px;
	height: 200px;
}

.form {
	width: 80%;
	max-width: 350px;
}

.input-group {
	margin-bottom: 20px;
}

.input-field {
	margin-bottom: 5px;
	padding: 15px;
	border: 1px solid #007aff;
	border-radius: 5px;
	height: 50px;
	font-size: 16px;
	background-color: #f8f8f8;
}

.error-tip {
	color: #ff6347;
	font-size: 14px;
}

.login-button {
	padding: 5px 15px;
	background-color: #007aff;
	color: white;
	border: none;
	border-radius: 5px;
	font-size: 18px;
	cursor: pointer;
	width: 100%;
}

.footer {
	margin-top: 20px;
	display: flex;
	justify-content: center;
	width: 80%;
	max-width: 350px;
}

.footer navigator {
	color: #007aff;
	text-decoration: none;
}
</style>
