const CONSULTATION_REPORTS_KEY = 'TCM_CONSULTATION_REPORTS'

function safeParse(value, fallback) {
	try {
		return value ? JSON.parse(value) : fallback
	} catch (error) {
		return fallback
	}
}

function getCurrentUserId() {
	const userInfo = safeParse(uni.getStorageSync('userInfo'), {})
	return userInfo.userId || userInfo.userName || userInfo.phoneNumber || userInfo.phonenumber || 'guest'
}

function readAllReports() {
	const reports = safeParse(uni.getStorageSync(CONSULTATION_REPORTS_KEY), [])
	return Array.isArray(reports) ? reports : []
}

function writeAllReports(reports) {
	uni.setStorageSync(CONSULTATION_REPORTS_KEY, JSON.stringify(reports))
}

function normalizeReport(report) {
	const now = new Date().toISOString()
	const userId = report.userId || getCurrentUserId()
	return {
		...report,
		id: report.id || `report_${Date.now()}`,
		reportNo: report.reportNo || `TCM${Date.now()}`,
		userId,
		status: report.status || 'submitted',
		submitTime: report.submitTime || now,
		createdAt: report.createdAt || now,
		updatedAt: now
	}
}

function getConsultationReports() {
	const userId = getCurrentUserId()
	return readAllReports()
		.filter((report) => report.userId === userId)
		.sort((a, b) => new Date(b.submitTime || b.createdAt || 0) - new Date(a.submitTime || a.createdAt || 0))
}

function getLatestConsultationReport() {
	return getConsultationReports()[0] || null
}

function getConsultationReportById(id) {
	const userId = getCurrentUserId()
	return readAllReports().find((report) => report.id === id && report.userId === userId) || null
}

function saveConsultationReport(report) {
	const normalized = normalizeReport(report)
	const reports = readAllReports()
	const index = reports.findIndex((item) => item.id === normalized.id)
	if (index >= 0) {
		reports.splice(index, 1, { ...reports[index], ...normalized })
	} else {
		reports.unshift(normalized)
	}
	writeAllReports(reports)
	return normalized
}

function updateConsultationReport(id, patch) {
	const reports = readAllReports()
	const userId = getCurrentUserId()
	const index = reports.findIndex((item) => item.id === id && item.userId === userId)
	if (index < 0) {
		return null
	}
	const updated = {
		...reports[index],
		...patch,
		updatedAt: new Date().toISOString()
	}
	reports.splice(index, 1, updated)
	writeAllReports(reports)
	return updated
}

function deleteConsultationReport(id) {
	const userId = getCurrentUserId()
	const reports = readAllReports()
	const nextReports = reports.filter((item) => !(item.id === id && item.userId === userId))
	writeAllReports(nextReports)
	return nextReports.length !== reports.length
}

export {
	CONSULTATION_REPORTS_KEY,
	deleteConsultationReport,
	getConsultationReportById,
	getLatestConsultationReport,
	getConsultationReports,
	saveConsultationReport,
	updateConsultationReport
}
