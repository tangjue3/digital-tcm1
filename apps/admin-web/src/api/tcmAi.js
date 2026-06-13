import { get, post } from '@/utils/request'

function sendTcmChat(payload) {
  return post('/ai/tcm-chat', payload, {
    timeout: 60000
  })
}

function getTcmKbStatus() {
  return get('/ai/tcm-kb/status')
}

function reloadTcmKb() {
  return post('/ai/tcm-kb/reload')
}

export {
  sendTcmChat,
  getTcmKbStatus,
  reloadTcmKb
}
