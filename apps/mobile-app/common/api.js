import { get, post, uploadFile } from './request.js'
import { ASSET_BASE_URL } from './config.js'

function rowsOf(response) {
	return response?.rows || response?.data?.rows || []
}

function dataOf(response) {
	if (response && Object.prototype.hasOwnProperty.call(response, 'data')) {
		return response.data
	}
	return response || {}
}

function buildAssetUrl(path) {
	if (!path) {
		return ''
	}
	if (/^https?:\/\//.test(path) || path.startsWith('/static') || path.startsWith('../../')) {
		return path
	}
	return `${ASSET_BASE_URL.replace(/\/$/, '')}/${String(path).replace(/^\//, '')}`
}

async function getKnowledgeLists(keyword = '') {
	const article = await get('/article/list', { articleTitle: keyword })
	const recipe = await get('/zhonogyaoshipu/list', { name: keyword })
	const disease = await get('/commondiseases/list', { name: keyword })
	let herb = await get('/chinesemedicine/list', { name: keyword })

	if (!rowsOf(herb).length) {
		herb = await get('/prescriptionofdrugs/list', { name: keyword })
	}

	const video = await get('/video/list', { name: keyword })

	return {
		article: rowsOf(article),
		recipe: rowsOf(recipe),
		disease: rowsOf(disease),
		herb: rowsOf(herb),
		video: rowsOf(video)
	}
}

async function getKnowledgeDetail(type, id) {
	const response = await get(`/${type}/${id}`)
	return dataOf(response)
}

async function uploadCommonFile(filePath, options = {}) {
	if (!filePath || /^https?:\/\//.test(filePath) || String(filePath).startsWith('/profile/')) {
		return filePath || ''
	}
	const response = await uploadFile('/common/upload', filePath, {
		baseUrl: ASSET_BASE_URL,
		defaultName: options.defaultName || 'file',
		extension: options.extension,
		contentType: options.contentType
	})
	if (response?.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || '上传失败')
	}
	return response?.fileName || response?.url || ''
}

async function getAppUserProfile() {
	const response = await get('/app/user/profile', {}, { baseUrl: ASSET_BASE_URL })
	if (response?.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || '获取采集资料失败')
	}
	return response?.data || null
}

async function saveAppUserProfile(profile) {
	const response = await post('/app/user/profile', profile, { baseUrl: ASSET_BASE_URL })
	if (response?.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || '保存采集资料失败')
	}
	return response
}

async function savePulseRecord(record) {
	const response = await post('/app/pulse/upload', record, { baseUrl: ASSET_BASE_URL })
	if (response?.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || '保存脉搏数据失败')
	}
	return response
}

async function getAppChatConversation() {
	const response = await get('/app/chat/conversation', {}, { baseUrl: ASSET_BASE_URL })
	if (response?.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || '获取聊天记录失败')
	}
	return response?.data || { messages: [] }
}

async function sendAppChatMessage(content) {
	const response = await post('/app/chat/message', { content }, { baseUrl: ASSET_BASE_URL })
	if (response?.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || '发送聊天消息失败')
	}
	return response?.data || null
}

async function sendTcmAiChatMessage(payload) {
	const response = await post('/tcm/ai/chat', payload, {
		baseUrl: ASSET_BASE_URL,
		timeout: 60000
	})
	if (response?.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || 'AI 问答暂时不可用')
	}
	return response?.data || null
}

export {
	buildAssetUrl,
	getKnowledgeLists,
	getKnowledgeDetail,
	uploadCommonFile,
	getAppUserProfile,
	saveAppUserProfile,
	savePulseRecord,
	getAppChatConversation,
	sendAppChatMessage,
	sendTcmAiChatMessage
}
