import request from '@/utils/request'

// 查询人体穴位列表
export function listAcupoints(query) {
  return request({
    url: '/system/acupoints/list',
    method: 'get',
    params: query
  })
}

// 查询人体穴位详细
export function getAcupoints(id) {
  return request({
    url: '/system/acupoints/' + id,
    method: 'get'
  })
}

// 新增人体穴位
export function addAcupoints(data) {
  return request({
    url: '/system/acupoints',
    method: 'post',
    data: data
  })
}

// 修改人体穴位
export function updateAcupoints(data) {
  return request({
    url: '/system/acupoints',
    method: 'put',
    data: data
  })
}

// 删除人体穴位
export function delAcupoints(id) {
  return request({
    url: '/system/acupoints/' + id,
    method: 'delete'
  })
}
