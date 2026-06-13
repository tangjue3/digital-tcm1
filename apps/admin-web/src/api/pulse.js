import { get } from '@/utils/request'

function getLatestPulse(userId) {
  return get(`/pulse/latest/${userId}`)
}

function listPulseRecords(params) {
  return get('/pulse/list', params)
}

export { getLatestPulse, listPulseRecords }
