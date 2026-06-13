import { get, put } from '@/utils/request'

function getConsultReportDashboard() {
  return get('/consult/report/dashboard')
}

function getUrgentConsultReports(params = {}) {
  return get('/consult/report/urgent', params)
}

function listConsultReports(params = {}) {
  return get('/consult/report/list', params)
}

function getConsultReport(reportId) {
  return get(`/consult/report/${reportId}`)
}

function updateConsultReportStatus(reportId, data = {}) {
  return put(`/consult/report/${reportId}/status`, data)
}

export {
  getConsultReport,
  getConsultReportDashboard,
  getUrgentConsultReports,
  listConsultReports,
  updateConsultReportStatus
}
