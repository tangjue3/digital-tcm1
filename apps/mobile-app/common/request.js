import { API_BASE_URL, ASSET_BASE_URL } from './config.js'

function joinUrl(baseUrl, url) {
	if (/^https?:\/\//.test(url)) {
		return url
	}
	return `${baseUrl.replace(/\/$/, '')}/${String(url).replace(/^\//, '')}`
}

function getToken() {
	return uni.getStorageSync('token') || uni.getStorageSync('Authorization') || ''
}

function normalizeResponse(res) {
	const body = res && res.data !== undefined ? res.data : res
	if (typeof body === 'string') {
		try {
			return JSON.parse(body)
		} catch (e) {
			return body
		}
	}
	return body
}

function extensionFromMime(type = '') {
	const mime = String(type).split(';')[0].toLowerCase()
	const map = {
		'image/jpeg': 'jpg',
		'image/jpg': 'jpg',
		'image/png': 'png',
		'image/gif': 'gif',
		'image/bmp': 'bmp',
		'audio/webm': 'webm',
		'audio/mp4': 'm4a',
		'audio/mpeg': 'mp3',
		'audio/mp3': 'mp3',
		'audio/wav': 'wav',
		'audio/x-wav': 'wav',
		'audio/aac': 'aac',
		'audio/x-m4a': 'm4a'
	}
	return map[mime] || ''
}

function hasExtension(name = '') {
	return /\.[a-z0-9]{1,8}($|\?)/i.test(String(name))
}

function fileNameForUpload(filePath, blob, options = {}) {
	if (options.fileName && hasExtension(options.fileName)) {
		return options.fileName
	}
	const pathName = String(filePath || '').split(/[?#]/)[0].split('/').pop() || ''
	if (pathName && !pathName.startsWith('blob:') && hasExtension(pathName)) {
		return pathName
	}
	const ext = options.extension || extensionFromMime(blob?.type || options.contentType) || 'dat'
	const baseName = options.defaultName || options.name || 'file'
	return `${baseName}.${ext}`
}

async function uploadBlobFile(url, filePath, options = {}) {
	const token = getToken()
	const response = await fetch(filePath)
	const blob = await response.blob()
	const fileName = fileNameForUpload(filePath, blob, options)
	const formData = new FormData()
	formData.append(options.name || 'file', blob, fileName)

	Object.keys(options.formData || {}).forEach((key) => {
		formData.append(key, options.formData[key])
	})

	const headers = {
		...(options.header || {})
	}
	if (token) {
		headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
	}

	const uploadResponse = await fetch(joinUrl(options.baseUrl || ASSET_BASE_URL, url), {
		method: 'POST',
		headers,
		body: formData
	})
	const text = await uploadResponse.text()
	const data = normalizeResponse({ data: text })
	if (uploadResponse.status === 401 || data?.code === 401) {
		uni.removeStorageSync('token')
		uni.removeStorageSync('loginType')
		uni.removeStorageSync('userInfo')
		uni.removeStorageSync('hasCompletedGuide')
		uni.removeStorageSync('userGuide')
		uni.removeStorageSync('pulseSessionBase')
		throw data || uploadResponse
	}
	return data
}

function request(options) {
	const token = getToken()
	const headers = {
		'Content-Type': 'application/json;charset=UTF-8',
		...(options.header || {})
	}

	if (token) {
		headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
	}

	return new Promise((resolve, reject) => {
		uni.request({
			url: joinUrl(options.baseUrl || API_BASE_URL, options.url),
			method: options.method || 'GET',
			data: options.data || {},
			header: headers,
			timeout: options.timeout || 10000,
			success: (res) => {
				const data = normalizeResponse(res)
				if (res.statusCode === 401 || data?.code === 401) {
					uni.removeStorageSync('token')
					uni.removeStorageSync('loginType')
					uni.removeStorageSync('userInfo')
					uni.removeStorageSync('hasCompletedGuide')
					uni.removeStorageSync('userGuide')
					uni.removeStorageSync('pulseSessionBase')
					reject(data || res)
					return
				}
				resolve(data)
			},
			fail: reject
		})
	})
}

function get(url, data = {}, options = {}) {
	return request({ ...options, url, data, method: 'GET' })
}

function post(url, data = {}, options = {}) {
	return request({ ...options, url, data, method: 'POST' })
}

function uploadFile(url, filePath, options = {}) {
	if (/^(blob:|data:)/.test(String(filePath)) && typeof fetch === 'function' && typeof FormData === 'function') {
		return uploadBlobFile(url, filePath, options)
	}

	const token = getToken()
	const headers = {
		...(options.header || {})
	}

	if (token) {
		headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
	}

	return new Promise((resolve, reject) => {
		uni.uploadFile({
			url: joinUrl(options.baseUrl || ASSET_BASE_URL, url),
			filePath,
			name: options.name || 'file',
			fileName: options.fileName,
			formData: options.formData || {},
			header: headers,
			success: (res) => {
				const data = normalizeResponse(res)
				if (res.statusCode === 401 || data?.code === 401) {
					uni.removeStorageSync('token')
					uni.removeStorageSync('loginType')
					uni.removeStorageSync('userInfo')
					uni.removeStorageSync('hasCompletedGuide')
					uni.removeStorageSync('userGuide')
					uni.removeStorageSync('pulseSessionBase')
					reject(data || res)
					return
				}
				resolve(data)
			},
			fail: reject
		})
	})
}

export { request, get, post, uploadFile, joinUrl }
