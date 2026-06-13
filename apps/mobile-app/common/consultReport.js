import { get, post, uploadFile } from './request.js'
import { ASSET_BASE_URL } from './config.js'

const ATTACHMENT_TYPE_MAP = {
	tonguePhoto: 'tongue_image',
	tongue_image: 'tongue_image',
	facePhoto: 'face_image',
	face_image: 'face_image',
	affectedPhoto: 'affected_part_image',
	affected_part_image: 'affected_part_image',
	examImage: 'inspection_report',
	inspection_report: 'inspection_report',
	voice: 'voice',
	other: 'other'
}

const ATTACHMENT_LABEL_MAP = {
	tongue_image: '舌象照片',
	face_image: '面部照片',
	affected_part_image: '患处照片',
	voice: '语音描述',
	inspection_report: '检查报告',
	other: '其他附件'
}

function ensureSuccess(response, fallbackMessage) {
	if (response && response.code !== undefined && response.code !== 200) {
		throw new Error(response.msg || fallbackMessage)
	}
	return response
}

function parsePath(value) {
	if (Array.isArray(value)) {
		return value
	}
	if (!value) {
		return []
	}
	return String(value).split(' > ').filter(Boolean)
}

function attachmentType(type) {
	return ATTACHMENT_TYPE_MAP[type] || 'other'
}

function attachmentLabel(fileType) {
	return ATTACHMENT_LABEL_MAP[fileType] || '其他附件'
}

function normalizeDisease(item = {}) {
	return {
		code: item.diseaseCode || item.code || '',
		displayName: item.diseaseName || item.name || '',
		name: item.diseaseName || item.name || '',
		path: parsePath(item.diseasePath || item.path),
		riskLevel: item.riskLevel || 'normal',
		riskReason: item.riskReason || ''
	}
}

function normalizeAttachment(item = {}) {
	const fileType = attachmentType(item.fileType || item.type)
	return {
		id: item.id,
		type: fileType,
		label: attachmentLabel(fileType),
		kind: fileType === 'voice' ? 'audio' : inferKind(fileType, item.kind),
		path: item.fileUrl || item.path || '',
		name: item.fileName || item.name || '',
		size: item.fileSize ?? item.size ?? null
	}
}

function inferKind(fileType, kind) {
	if (kind) {
		return kind
	}
	if (['tongue_image', 'face_image', 'affected_part_image', 'inspection_report'].includes(fileType)) {
		return 'image'
	}
	return 'file'
}

function normalizeReportDetail(report = {}) {
	return {
		id: report.reportId,
		reportId: report.reportId,
		reportNo: report.reportNo,
		status: report.status,
		submitTime: report.createTime,
		createTime: report.createTime,
		highestRiskLevel: report.highestRiskLevel || 'normal',
		basicInfo: {
			name: report.patientName || '',
			gender: report.gender || '',
			age: report.age,
			visitType: report.visitType || '',
			chronicDisease: report.basicDisease || '',
			allergyStatus: report.allergyHistoryType || '',
			allergyDetail: report.allergyHistoryDetail || '',
			pastHistoryStatus: report.pastHistoryType || '',
			pastHistoryDetail: report.pastHistoryDetail || '',
			medicationStatus: report.currentMedicationType || '',
			medicationDetail: report.currentMedicationDetail || ''
		},
		diseases: (report.diseaseList || []).map(normalizeDisease),
		attachments: (report.attachmentList || []).map(normalizeAttachment),
		complaint: {
			description: report.chiefComplaint || '',
			duration: report.duration || '',
			severity: report.severityLevel ?? '',
			suddenWorse: report.suddenWorse || '',
			lifeImpact: report.lifeImpact ? String(report.lifeImpact).split(',').filter(Boolean) : []
		}
	}
}

function normalizeReportListItem(report = {}) {
	return {
		id: report.reportId,
		reportId: report.reportId,
		reportNo: report.reportNo,
		patientName: report.patientName || '',
		gender: report.gender || '',
		age: report.age,
		chiefComplaint: report.chiefComplaint || '',
		diseaseSummary: report.diseaseSummary || '',
		highestRiskLevel: report.highestRiskLevel || 'normal',
		status: report.status || 'pending',
		createTime: report.createTime
	}
}

function needsUpload(filePath = '') {
	if (!filePath) {
		return false
	}
	return !/^https?:\/\//.test(filePath) && !String(filePath).startsWith('/profile/')
}

function attachmentDefaultName(fileType) {
	const map = {
		tongue_image: 'tongue-image',
		face_image: 'face-image',
		affected_part_image: 'affected-part',
		voice: 'voice-description',
		inspection_report: 'inspection-report',
		other: 'attachment'
	}
	return map[fileType] || 'attachment'
}

async function uploadConsultAttachment(item = {}, index = 0) {
	const fileType = attachmentType(item.type || item.fileType)
	const originalPath = item.path || item.fileUrl || ''

	if (!originalPath) {
		return null
	}

	if (!needsUpload(originalPath)) {
		return {
			fileType,
			fileName: item.name || `${attachmentDefaultName(fileType)}-${index + 1}`,
			fileUrl: originalPath,
			fileSize: item.size ?? item.fileSize ?? null,
			remark: item.remark || ''
		}
	}

	const response = ensureSuccess(await uploadFile('/common/upload', originalPath, {
		baseUrl: ASSET_BASE_URL,
		defaultName: attachmentDefaultName(fileType)
	}), '附件上传失败')

	return {
		fileType,
		fileName: response.originalFilename || item.name || response.newFileName || `${attachmentDefaultName(fileType)}-${index + 1}`,
		fileUrl: response.url || response.fileName || '',
		fileSize: item.size ?? item.fileSize ?? null,
		remark: item.remark || ''
	}
}

async function uploadConsultAttachments(attachments = []) {
	const normalized = attachments.filter((item) => item && (item.path || item.fileUrl))
	if (!normalized.length) {
		return []
	}

	const result = []
	for (let index = 0; index < normalized.length; index += 1) {
		const uploaded = await uploadConsultAttachment(normalized[index], index)
		if (uploaded) {
			result.push(uploaded)
		}
	}
	return result
}

async function buildSubmitPayload(form) {
	const basic = form.basicInfo || {}
	const complaint = form.complaint || {}
	const uploadedAttachments = await uploadConsultAttachments(form.attachments || [])

	return {
		patientName: basic.name,
		gender: basic.gender,
		age: Number(basic.age),
		visitType: basic.visitType,
		basicDisease: basic.chronicDisease || '',
		allergyHistoryType: basic.allergyStatus || '无',
		allergyHistoryDetail: basic.allergyDetail || '',
		pastHistoryType: basic.pastHistoryStatus || '无',
		pastHistoryDetail: basic.pastHistoryDetail || '',
		currentMedicationType: basic.medicationStatus || '无',
		currentMedicationDetail: basic.medicationDetail || '',
		selectedDiseases: (form.diseases || []).map((item) => ({
			code: item.code,
			name: item.displayName || item.name,
			path: Array.isArray(item.path) ? item.path.join(' > ') : item.path || '',
			riskLevel: item.riskLevel || 'normal',
			riskReason: item.riskReason || ''
		})),
		chiefComplaint: complaint.description || '',
		duration: complaint.duration || '',
		severityLevel: complaint.severity !== '' && complaint.severity !== null && complaint.severity !== undefined
			? Number(complaint.severity)
			: null,
		suddenWorse: complaint.suddenWorse || '',
		lifeImpact: complaint.lifeImpact || [],
		attachments: uploadedAttachments
	}
}

async function submitConsultReport(form) {
	const payload = await buildSubmitPayload(form)
	const response = ensureSuccess(await post('/app/consult/report', payload, {
		baseUrl: ASSET_BASE_URL
	}), '提交失败')
	return response.data || {}
}

async function getMyConsultReports(params = {}) {
	const response = await get('/app/consult/report/my', params, {
		baseUrl: ASSET_BASE_URL
	})
	ensureSuccess(response, '获取我的报告失败')
	return {
		...response,
		rows: (response.rows || []).map(normalizeReportListItem)
	}
}

async function getConsultReportDetail(reportId) {
	const response = ensureSuccess(await get(`/app/consult/report/${reportId}`, {}, {
		baseUrl: ASSET_BASE_URL
	}), '获取报告详情失败')
	return normalizeReportDetail(response.data || {})
}

async function getLatestMyConsultReport() {
	const result = await getMyConsultReports({
		pageNum: 1,
		pageSize: 1
	})
	return (result.rows || [])[0] || null
}

export {
	getConsultReportDetail,
	getLatestMyConsultReport,
	getMyConsultReports,
	normalizeReportDetail,
	submitConsultReport,
	uploadConsultAttachments
}
