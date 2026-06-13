import request from '@/utils/request'

// 查询药方列表
export function listPrescriptionofdrugs(query) {
  return request({
    url: '/system/prescriptionofdrugs/list',
    method: 'get',
    params: query
  })
}

// 查询药方详细
export function getPrescriptionofdrugs(id) {
  return request({
    url: '/system/prescriptionofdrugs/' + id,
    method: 'get'
  })
}

// 新增药方
export function addPrescriptionofdrugs(data) {
  return request({
    url: '/system/prescriptionofdrugs',
    method: 'post',
    data: data
  })
}

// 修改药方
export function updatePrescriptionofdrugs(data) {
  return request({
    url: '/system/prescriptionofdrugs',
    method: 'put',
    data: data
  })
}

// 删除药方
export function delPrescriptionofdrugs(id) {
  return request({
    url: '/system/prescriptionofdrugs/' + id,
    method: 'delete'
  })
}
