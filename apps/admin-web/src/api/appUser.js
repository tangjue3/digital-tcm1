import { del, get } from '@/utils/request'

function listAppUsers(params) {
  return get('/appUser/list', params)
}

function getAppUser(userId) {
  return get(`/appUser/${userId}`)
}

function deleteAppUser(userId) {
  return del(`/appUser/${userId}`)
}

export { listAppUsers, getAppUser, deleteAppUser }
