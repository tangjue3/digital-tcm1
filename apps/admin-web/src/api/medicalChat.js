import { get, post } from '@/utils/request'

function listMedicalChatConversations() {
  return get('/medicalChat/conversations')
}

function getMedicalChatConversation(patientUserId) {
  return get(`/medicalChat/conversation/${patientUserId}`)
}

function sendMedicalChatMessage(patientUserId, content) {
  return post('/medicalChat/message', {
    patientUserId,
    content
  })
}

export {
  listMedicalChatConversations,
  getMedicalChatConversation,
  sendMedicalChatMessage
}
