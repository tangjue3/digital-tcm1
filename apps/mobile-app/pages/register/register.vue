<template>
	<view class="register-container">
		<view class="logo">
			<image src="../../static/app-logo.png" mode="aspectFit"></image>
		</view>

		<view class="form">
			<view class="input-group">
				<input
					v-model="phoneNumber"
					type="number"
					placeholder="请输入手机号"
					class="input-field"
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
			<view class="input-group">
				<input
					v-model="confirmPassword"
					type="password"
					placeholder="请再次输入密码"
					class="input-field"
					@input="validateInput('confirmPassword')"
				/>
				<view v-if="!isConfirmPasswordValid && confirmPassword" class="error-tip">两次输入的密码不一致</view>
			</view>
			<view class="input-group">
				<input
					v-model="nickname"
					type="text"
					placeholder="请输入用户名"
					class="input-field"
					@input="validateInput('nickname')"
				/>
				<view v-if="!isNicknameValid && nickname" class="error-tip">用户名不能为空</view>
			</view>
			<button class="register-button" @click="handleRegister">注册</button>
		</view>

		<view class="footer">
			<navigator url="/pages/Login/Login">已有账号？去登录</navigator>
		</view>
	</view>
</template>

<script>
import { loginByPassword, registerByPassword } from '../../common/auth.js'

export default {
	data() {
		return {
			phoneNumber: '',
			password: '',
			confirmPassword: '',
			nickname: '',
			isPhoneNumberValid: true,
			isPasswordValid: true,
			isConfirmPasswordValid: true,
			isNicknameValid: true
		}
	},
	computed: {
		isFormValid() {
			return (
				this.isPhoneNumberValid &&
				this.isPasswordValid &&
				this.isConfirmPasswordValid &&
				this.isNicknameValid &&
				this.phoneNumber &&
				this.password &&
				this.confirmPassword &&
				this.nickname
			)
		}
	},
	methods: {
		validateInput(field) {
			switch (field) {
				case 'phoneNumber': {
					const phoneRegex = /^1[3-9]\d{9}$/
					this.isPhoneNumberValid = phoneRegex.test(this.phoneNumber)
					break
				}
				case 'password':
					this.isPasswordValid = this.password.length >= 6
					if (this.confirmPassword) {
						this.validateInput('confirmPassword')
					}
					break
				case 'confirmPassword':
					this.isConfirmPasswordValid = this.password === this.confirmPassword
					break
				case 'nickname':
					this.isNicknameValid = this.nickname.trim() !== ''
					break
				default:
					break
			}
		},
		async handleRegister() {
			if (!this.isFormValid) {
				return
			}
			try {
				await registerByPassword(this.phoneNumber, this.password, this.nickname)
				await loginByPassword(this.phoneNumber, this.password)
				uni.showToast({
					title: '注册成功',
					icon: 'success'
				})
				setTimeout(() => {
					uni.switchTab({ url: '/pages/health-plan/health-plan' })
				}, 1000)
			} catch (error) {
				console.error(error)
				uni.showToast({
					title: error.message || '注册失败',
					icon: 'none'
				})
			}
		}
	}
}
</script>

<style>
.register-container {
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
	height: 50px;
	border: 1px solid #007aff;
	border-radius: 5px;
	font-size: 16px;
	width: 100%;
	box-sizing: border-box;
	background-color: #f8f8f8;
}

.error-tip {
	color: #ff6347;
	font-size: 14px;
}

.register-button {
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
	width: 80%;
	max-width: 350px;
	text-align: center;
}

.footer navigator {
	color: #007aff;
	text-decoration: none;
}
</style>
