import { ASSET_BASE_URL } from './config.js'
import { get, post } from './request.js'
import { getAppUserProfile } from './api.js'

const TEST_USERNAME = '13800138000'
const TEST_PASSWORD = '123456'

function ensureSuccess(data, fallbackMessage) {
	if (data && data.code !== undefined && data.code !== 200) {
		throw new Error(data.msg || fallbackMessage)
	}
	return data || {}
}

function clearAuthStorage() {
	uni.removeStorageSync('token')
	uni.removeStorageSync('Authorization')
	uni.removeStorageSync('loginType')
	uni.removeStorageSync('userInfo')
	uni.removeStorageSync('hasCompletedGuide')
	uni.removeStorageSync('userGuide')
	uni.removeStorageSync('pulseSessionBase')
}

function normalizeUser(user, username) {
	return {
		userId: user.userId,
		userName: user.userName || username,
		phoneNumber: user.phonenumber || username,
		phonenumber: user.phonenumber || username,
		nickname: user.nickName || user.userName || username,
		nickName: user.nickName || user.userName || username,
		avatar: user.avatar || ''
	}
}

function profileToGuideData(profile) {
	if (!profile || !profile.userId) {
		return null
	}

	let remark = {}
	try {
		remark = profile.remark ? JSON.parse(profile.remark) : {}
	} catch (error) {
		remark = {}
	}

	return {
		gender: profile.sex || '',
		age: profile.age || '',
		weight: remark.weight || '',
		height: remark.height || '',
		pulseRate: remark.pulseRate || '',
		medicalHistory: remark.medicalHistory || [],
		facePhoto: profile.faceImage || '',
		tonguePhoto: profile.tongueImage || '',
		audioPath: profile.voiceFile || ''
	}
}

async function doLogin(username, password) {
	return ensureSuccess(await post('/login', {
		username,
		password,
		code: '',
		uuid: ''
	}, { baseUrl: ASSET_BASE_URL }), '登录失败')
}

async function registerByPassword(username, password, nickName = '') {
	return ensureSuccess(await post('/register', {
		username,
		phonenumber: username,
		nickName,
		password,
		code: '',
		uuid: ''
	}, { baseUrl: ASSET_BASE_URL }), '注册失败')
}

async function loginByPassword(username, password) {
	clearAuthStorage()
	const loginResult = await doLogin(username, password)

	const token = loginResult.token
	if (!token) {
		throw new Error('用户名或密码错误')
	}

	uni.setStorageSync('token', token)

	const infoResult = ensureSuccess(await get('/getInfo', {}, {
		baseUrl: ASSET_BASE_URL
	}), '获取用户信息失败')

	const userInfo = normalizeUser(infoResult.user || {}, username)
	uni.setStorageSync('loginType', true)
	uni.setStorageSync('userInfo', JSON.stringify(userInfo))

	try {
		const profile = await getAppUserProfile()
		const guideData = profileToGuideData(profile)
		if (guideData) {
			uni.setStorageSync('userGuide', JSON.stringify(guideData))
			uni.setStorageSync('hasCompletedGuide', true)
		} else {
			uni.removeStorageSync('hasCompletedGuide')
		}
	} catch (error) {
		console.warn('[auth] profile preload failed', error)
	}

	return userInfo
}

export { loginByPassword, registerByPassword, profileToGuideData, TEST_USERNAME, TEST_PASSWORD, clearAuthStorage }
